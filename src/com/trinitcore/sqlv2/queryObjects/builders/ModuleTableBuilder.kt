package com.trinitcore.sqlv2.queryObjects.builders

import com.trinitcore.sqlv2.commonUtils.row.Row
import com.trinitcore.sqlv2.queryObjects.ModuleTable
import com.trinitcore.sqlv2.queryUtils.module.DataModule

class ModuleTableBuilder<out Module : DataModule>(tableName: String, tableColumns: Array<out String> = arrayOf(), val moduleInitialisation: () -> Module) : TableBuilder(tableName, tableColumns) {

    override fun build(): ModuleTable<Module> {
        val t = ModuleTable<Module>(tableName, moduleInitialisation = moduleInitialisation)
        t.associations = associations
        return t
    }

}