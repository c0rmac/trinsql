package com.trinitcore.sqlv2.queryUtils.module

import com.trinitcore.asd.annotations.Web
import com.trinitcore.asd.requestTools.Parameters
import com.trinitcore.asd.requestTools.SessionAttributes
import com.trinitcore.asd.responseTools.Response
import com.trinitcore.asd.responseTools.status.StatusResult
import com.trinitcore.asd.responseTools.status.json.OKFormStatus
import com.trinitcore.asd.servlet.support.RESTSupport
import com.trinitcore.asd.servlet.support.ReflectiveUtilitiesContainer
import com.trinitcore.asd.user.UserType
import com.trinitcore.sqlv2.commonUtils.Defaults
import com.trinitcore.sqlv2.commonUtils.QMap
import com.trinitcore.sqlv2.commonUtils.row.Row
import com.trinitcore.sqlv2.commonUtils.row.RowType
import com.trinitcore.sqlv2.commonUtils.row.Rows
import com.trinitcore.sqlv2.commonUtils.row.SingularRowType
import com.trinitcore.sqlv2.queryObjects.Table
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.*
import kotlin.reflect.full.*
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField

class Attribute<T : Any>(val columnName: String = "") :
        ReadWriteProperty<Any?, T> {
    private val fieldHolder = FieldHolder<T>()

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return fieldHolder.field
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        val didInit = fieldHolder.didInit
        fieldHolder.field = value
        if (didInit) (thisRef as? DataModule)?.handleUpdate(property.name, value)
        fieldHolder.didInit = true
    }

    class FieldHolder<T : Any> {
        var didInit = false
        lateinit var field: T
    }
}

internal object RowPool {

    val rowModuleWithPrimitiveAttributes = hashMapOf<KClass<*>, Map<String, KMutableProperty<*>>>()
    val rowModuleWithSubModules = hashMapOf<KClass<*>, Map<String, KMutableProperty<*>>>()

    fun registerModule(rowTypeKClass: KClass<*>, instance: DataModule) {
        if (!rowModuleWithPrimitiveAttributes.containsKey(rowTypeKClass)) {
            val initialisablePrimitiveProperties = hashMapOf<String, KMutableProperty<*>>()
            val initialisableSubModulesProperties = hashMapOf<String, KMutableProperty<*>>()

            rowTypeKClass.memberProperties.forEach { memberProperty ->
                memberProperty.isAccessible = true

                    if (memberProperty is KMutableProperty1<*, *>) {
                        val columnName = (memberProperty as KMutableProperty1<DataModule, *>).getDelegate(instance)?.let { (it as? Attribute<*>)?.columnName }

                        columnName?.let {
                            val name = if (it == "") memberProperty.name else it
                            if (memberProperty.returnType.isSubtypeOf(RowType::class.starProjectedType)) {
                                initialisableSubModulesProperties.put(name, memberProperty)
                            } else {
                                initialisablePrimitiveProperties.put(name, memberProperty)
                            }
                        }
                    }
            }
            rowModuleWithPrimitiveAttributes.put(rowTypeKClass, initialisablePrimitiveProperties)
            rowModuleWithSubModules.put(rowTypeKClass, initialisableSubModulesProperties)
        }
    }

    fun findInitialisablePrimitiveProperties(rowTypeKClass: KClass<*>): Map<String, KMutableProperty<*>> {
        return rowModuleWithPrimitiveAttributes[rowTypeKClass]!!
    }

    fun findInitialisableSubModuleProperties(rowTypeKClass: KClass<*>) : Map<String, KMutableProperty<*>> {
        return rowModuleWithSubModules[rowTypeKClass]!!
    }
}

open class DataModule : SingularRowType, RESTSupport {

    // REST Support paternity
    var restparent: RESTSupport? = null
    override fun didPerformFirstTimeInitialisation(): Boolean = restparent != null
    override fun firstTimeInitialisation(parent: RESTSupport?) { restparent = parent }
    override fun getParent(): RESTSupport? = restparent

    // Optional overrides
    override fun bindAdditionalInformationToStatusResult(statusResult: StatusResult, requestingParameters: Parameters, sessionAttributes: SessionAttributes<*>): StatusResult? {
        return null
    }

    val parentTable: Table = Table("")
    lateinit var row: Row

    private fun genericInitialisation(map: Map<String, Any>) {
        RowPool.registerModule(this::class, this)
        optimisedInitialisation(map)
    }

    var optimisedInitialisation = true

    private var _ID:Int = 0

    override fun getID(): Int {
        return _ID
    }

    fun handlePropertyInitialisation(property: KMutableProperty<*>, subModulePostInitialisation: (module: DataModule) -> Unit, primitiveValueRetrieval: () -> Any?) {
        val returnType = property.returnType
        if (!returnType.isSubtypeOf(DataModule::class.starProjectedType)) {
            property.setter.call(this, primitiveValueRetrieval())
        }
    }

    fun update(vararg qMaps: QMap) {
        println("Updating")
        parentTable.updateByID(getID(), *qMaps)
    }

    fun handleUpdate(propertyName: String, value: Any) {
        update(QMap(propertyName, value))
    }

    fun initialiseAttribute(propertyName: String, value: Any) {
        RowPool.findInitialisableSubModuleProperties(this::class)[propertyName]?.setter?.call(this, value)
    }

    fun initialiseAttributes(row: Row) {
        genericInitialisation(row as Map<String, Any>)
        this.row = row
        _ID = row.getID()
        if (!optimisedInitialisation)
            RowPool.findInitialisablePrimitiveProperties(this::class).forEach {
                val key = it.key
                handlePropertyInitialisation(it.value, {
                    it.initialiseAttributes(row[key] as Row)
                }, {
                    row[key]
                })
            }
    }

    fun initialiseAttributes(prefix: String, sessionAttributes: SessionAttributes<*>) {
        genericInitialisation(sessionAttributes as Map<String, Any>)
        _ID = sessionAttributes[prefix + "::" + Defaults.indexColumnKey] as Int
        if (!optimisedInitialisation)
            RowPool.findInitialisablePrimitiveProperties(this::class).forEach {
                val key = prefix + "::" + it.key
                handlePropertyInitialisation(it.value,
                        {
                            it.initialiseAttributes(key, sessionAttributes)
                        },
                        {
                            sessionAttributes[key]
                        })
            }
    }

    open fun attributes(): Map<String, Any>? = null

    open fun optimisedInitialisation(data: Map<String, Any>) {
        optimisedInitialisation = false
    }

    val reflectiveUtilities = ReflectiveUtilitiesContainer(this)

    override fun initializeAsSubWebServlet(methodNames: MutableList<String>, requestingParameters: Parameters, sessionAttributes: SessionAttributes<UserType<*>>, response: Response) {
        val handler = reflectiveUtilities.generateHandler(methodNames, requestingParameters, sessionAttributes, response)
        handler.performDefaultProcedure { handler.executeMethod("toJSONObject") }
    }

    @Web
    open fun toJSONObject() : StatusResult {
        return OKFormStatus("")
    }

}

class TestModule : DataModule() {

    var firstname: String by Attribute()
    var lastname: String by Attribute()
    var categorySelection: SubTestModule by Attribute()
    var unavailableTimeRanges: Rows by Attribute()

}

class SubTestModule : DataModule() {

    var userID: Int by Attribute()

}