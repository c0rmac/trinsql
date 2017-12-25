package com.trinitcore.sqlv2.queryUtils.associationV2.format.handler

import com.trinitcore.sqlv2.commonUtils.row.Row
import com.trinitcore.sqlv2.queryUtils.associationV2.GenericAssociation
import com.trinitcore.sqlv2.queryUtils.associationV2.GenericAssociationHandler
import com.trinitcore.sqlv2.queryUtils.associationV2.table.TableAssociation

class ReformatAssociationHandler(val handler: (row: Row) -> Any?, tableAssociation: GenericAssociation) : GenericAssociationHandler(tableAssociation) {
    override fun match(matchingRow: Row): Any? {
        return handler(matchingRow)
    }

}