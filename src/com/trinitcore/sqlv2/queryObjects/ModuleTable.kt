package com.trinitcore.sqlv2.queryObjects

import com.trinitcore.sqlv2.commonUtils.row.Row
import com.trinitcore.sqlv2.commonUtils.row.Rows
import com.trinitcore.sqlv2.queryUtils.associationV2.GenericAssociation
import com.trinitcore.sqlv2.queryUtils.module.DataModule
import com.trinitcore.sqlv2.queryUtils.module.DataModules
import com.trinitcore.sqlv2.queryUtils.parameters.Where
import com.trinitcore.sqlv2.queryUtils.parameters.columns.Column

class ModuleTable<out Module : DataModule>(named: String, val moduleInitialisation: () -> DataModule, vararg columns: Column<out Any>) : Table(named, *columns) {

    override fun addAssociation(association: GenericAssociation): ModuleTable<Module> {
        super.addAssociation(association)
        return this
    }

    override fun rowsCreation(): DataModules<Module> {
        return DataModules(indexColumnKey,this, moduleInitialisation)
    }

    override fun find(where: Where, associations: Boolean): DataModules<Module> {
        return super.find(where, associations) as DataModules<Module>
    }

    override fun find(where: Where): DataModules<Module> {
        return super.find(where) as DataModules<Module>
    }

    fun findModuleByID(ID: Int) : Module? {
        val a = find(Where().value(indexColumnKey,ID)).firstEntry().value
        return a as? Module?
    }
}
