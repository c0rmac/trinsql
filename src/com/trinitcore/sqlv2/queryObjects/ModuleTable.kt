package com.trinitcore.sqlv2.queryObjects

import com.trinitcore.sqlv2.commonUtils.row.Row
import com.trinitcore.sqlv2.commonUtils.row.Rows
import com.trinitcore.sqlv2.queryUtils.module.DataModule
import com.trinitcore.sqlv2.queryUtils.module.DataModules
import com.trinitcore.sqlv2.queryUtils.parameters.Where
import com.trinitcore.sqlv2.queryUtils.parameters.columns.Column

class ModuleTable<out Module : DataModule>(named: String, vararg columns: Column<out Any>, val moduleInitialisation: (row: Row) -> DataModule) : Table(named, *columns) {

    override fun rowsCreation(): DataModules<Module> {
        return DataModules(indexColumnKey,this, moduleInitialisation)
    }

    override fun find(where: Where, associations: Boolean): DataModules<Module> {
        return super.find(where, associations) as DataModules<Module>
    }

}