package com.trinitcore.sqlv2.queryUtils.module

import com.trinitcore.sqlv2.commonUtils.row.Rows
import com.trinitcore.sqlv2.queryObjects.Table

abstract class DataModules<out Module : DataModule>(indexColumnKey: String, parentTable: Table) : Rows(indexColumnKey, parentTable) {

    init {

    }

    override fun get(key: Any): Module? {
        return super.get(key) as Module
    }

}