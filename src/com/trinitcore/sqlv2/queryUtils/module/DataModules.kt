package com.trinitcore.sqlv2.queryUtils.module

import com.trinitcore.sqlv2.commonUtils.row.Row
import com.trinitcore.sqlv2.commonUtils.row.RowType
import com.trinitcore.sqlv2.commonUtils.row.Rows
import com.trinitcore.sqlv2.queryObjects.Table

class DataModules<out Module : DataModule>(indexColumnKey: String, parentTable: Table, val moduleInitialisation: (row: Row) -> DataModule) : Rows(indexColumnKey, parentTable) {

    init {

    }

    override fun rowsCreation(indexColumnKey: String): Rows {
        return DataModules<Module>(indexColumnKey, parentTable, moduleInitialisation)
    }

    override fun get(key: Any): Module? {
        return super.get(key) as Module
    }

    override fun put(key: Any, value: RowType): RowType? {
        handleInsertRowAssociations(value)
        val module = if (value is Row) {
            val m = moduleInitialisation(value)
            m.initialiseAttributes(value)
            m
        } else null
        return super.put(key, module ?: value)
    }

}