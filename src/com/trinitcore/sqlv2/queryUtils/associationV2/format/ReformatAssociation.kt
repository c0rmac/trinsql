package com.trinitcore.sqlv2.queryUtils.associationV2.format

import com.trinitcore.sqlv2.commonUtils.row.Row
import com.trinitcore.sqlv2.queryUtils.associationV2.GenericAssociation
import com.trinitcore.sqlv2.queryUtils.associationV2.GenericAssociationHandler
import com.trinitcore.sqlv2.queryUtils.associationV2.format.handler.ReformatAssociationHandler

class ReformatAssociation(val newColumnTitle: String,
                          val reformationHandler: (row: Row) -> Any?) : GenericAssociation() {
    override fun generateAssociationHandler(): GenericAssociationHandler {
        return ReformatAssociationHandler(reformationHandler, this)
    }

    override fun getColumnTitle(): String {
        return newColumnTitle
    }
}