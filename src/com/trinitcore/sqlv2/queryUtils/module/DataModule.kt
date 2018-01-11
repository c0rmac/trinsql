package com.trinitcore.sqlv2.queryUtils.module

import com.trinitcore.asd.requestTools.SessionAttributes
import com.trinitcore.sqlv2.commonUtils.row.Row
import com.trinitcore.sqlv2.commonUtils.row.RowType
import com.trinitcore.sqlv2.queryObjects.Table
import com.trinitcore.sqlv2.queryUtils.module.annotations.Attribute
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.*
import kotlin.reflect.jvm.isAccessible


internal object RowPool {

    val rowModule = hashMapOf<KClass<*>, Map<String,KMutableProperty<*>>>()

    fun registerModule(rowTypeKClass: KClass<*>) {
        if (!rowModule.containsKey(rowTypeKClass)) {
            val initialisableProperties = hashMapOf<String,KMutableProperty<*>>()
            rowTypeKClass.memberProperties.forEach { memberProperty ->
                if (memberProperty is KMutableProperty<*>) {
                    memberProperty.findAnnotation<Attribute>()?.let {
                        memberProperty.isAccessible = true
                        val name = if (it.columnName == "") memberProperty.name else it.columnName
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

abstract class DataModule : RowType {

    abstract val parentTable: Table

    init {
        RowPool.registerModule(this::class)
    }

    var optimisedInitialisation = true

    @Attribute var ID: Int = 0

    fun handlePropertyInitialisation(property: KMutableProperty<*>, subModulePostInitialisation: (module: DataModule) -> Unit, primitiveValueRetrieval: () -> Any?) {
        val returnType = property.returnType
        if (returnType.isSubtypeOf(DataModule::class.starProjectedType)) {
            val instance = (returnType.classifier as KClass<*>).createInstance() as DataModule
            subModulePostInitialisation(instance)
            property.setter.call(this, instance)
        } else {
            property.setter.call(this, primitiveValueRetrieval())
        }
    }

    fun initialiseAttributes(row: Row) {
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

    open fun optimisedInitialisation(data : Map<String,Any>) {
        optimisedInitialisation = false
    }

}

class TestModule : DataModule() {
    override val parentTable: Table
        get() = Table("")

    override fun optimisedInitialisation(data: Map<String,Any>) {

    }

}