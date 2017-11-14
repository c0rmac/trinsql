package com.trinitcore.sqlv2.queryUtils.associations

import com.trinitcore.sqlv2.commonUtils.row.Row

/**
 * Created by Cormac on 23/10/2017.
 */

open class ReformatAssociation : GenericAssociation {

    public val newColumnTitle:String
    public val reformationHandler:(row: Row) -> Any?

    constructor(newColumnTitle: String, reformationHandler: (row: Row) -> Any?) {
        this.newColumnTitle = newColumnTitle
        this.reformationHandler = reformationHandler
    }

    override fun findAssociatingRows(matchingRow: Row): Any? {
        return this.reformationHandler(matchingRow)
    }

    override fun getColumnTitle(): String {
        return newColumnTitle
    }
}