package com.trinitcore.sqlv2.queryUtils.module

import com.trinitcore.asd.requestTools.SessionAttributes
import com.trinitcore.sqlv2.commonUtils.QMap
import com.trinitcore.sqlv2.commonUtils.row.Row
import com.trinitcore.sqlv2.commonUtils.row.RowType
import com.trinitcore.sqlv2.queryObjects.Table
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.*
import kotlin.reflect.full.*
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField

class Attribute<T: Any>(val columnName: String = "") :
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

    class FieldHolder<T: Any> {
        var didInit = false
        lateinit var field: T
    }
}

internal object RowPool {

    val rowModule = hashMapOf<KClass<*>, Map<String,KMutableProperty<*>>>()

    fun registerModule(rowTypeKClass: KClass<*>, instance: DataModule) {
        if (!rowModule.containsKey(rowTypeKClass)) {
            val initialisableProperties = hashMapOf<String,KMutableProperty<*>>()

            rowTypeKClass.memberProperties.forEach { memberProperty ->
                memberProperty.isAccessible = true
                if (memberProperty is KMutableProperty1<*,*>) {
                    val columnName = (memberProperty as KMutableProperty1<DataModule, *>).getDelegate(instance)?.let { (it as? Attribute<*>)?.columnName }

                    columnName?.let {
                        val name = if (it == "") memberProperty.name else it
                        initialisableProperties.put(name, memberProperty)
                    }
                }
            }
            rowModule.put(rowTypeKClass,initialisableProperties)
        }
    }

    fun findInitialisableProperties(rowTypeKClass: KClass<*>) : Map<String,KMutableProperty<*>> {
        return rowModule[rowTypeKClass]!!
    }
}
 open class DataModule : RowType, InvocationHandler {
     override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any? {

         return null
     }

     companion object {
         fun newInstance(obj: Any, interfaces: Array<Class<*>>): DataModule {
             return java.lang.reflect.Proxy.newProxyInstance(obj.javaClass.classLoader,
                     interfaces,
                     DataModule()) as DataModule
         }
     }

     val parentTable: Table = Table("")

    private fun genericInitialisation(map: Map<String,Any>) {
        RowPool.registerModule(this::class, this)
        optimisedInitialisation(map)
    }

    var optimisedInitialisation = true

    var ID: Int by Attribute()

    fun handlePropertyInitialisation(property: KMutableProperty<*>, subModulePostInitialisation: (module: DataModule) -> Unit, primitiveValueRetrieval: () -> Any?) {
        val returnType = property.returnType
        if (returnType.isSubtypeOf(DataModule::class.starProjectedType)) {
            val instance = (returnType.classifier as KClass<*>).createInstance() as DataModule
            subModulePostInitialisation(instance)
            val javaField = property.javaField!!
            javaField.isAccessible = true
            property.javaField!!.set(this, instance)
        } else {
            property.setter.call(this, primitiveValueRetrieval())
        }
    }

    fun update(vararg qMaps: QMap) {
        println("Updating")
        parentTable.updateByID(ID, *qMaps)
        /*
        val attributes = RowPool.findInitialisableProperties(this::class)
        for (qMap in qMaps) {
            attributes.filter { it.key == qMap.key }
        }
        */
    }

    fun handleUpdate(propertyName: String, value: Any) {
        update(QMap(propertyName, value))
    }

    fun initialiseAttributes(row: Row) {
        genericInitialisation(row as Map<String, Any>)
        if (!optimisedInitialisation)
            RowPool.findInitialisableProperties(this::class).forEach {
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
        if (!optimisedInitialisation)
            RowPool.findInitialisableProperties(this::class).forEach {
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

    open fun attributes() : Map<String, Any>? = null

    open fun optimisedInitialisation(data : Map<String,Any>) {
        optimisedInitialisation = false
    }

}

class TestModule : DataModule() {

    var firstname: String by Attribute()
    var lastname: String by Attribute()

}