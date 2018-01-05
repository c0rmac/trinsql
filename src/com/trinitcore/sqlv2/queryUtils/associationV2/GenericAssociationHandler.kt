package com.trinitcore.sqlv2.queryUtils.associationV2

import com.trinitcore.sqlv2.commonUtils.row.Row

abstract class GenericAssociationHandler(open val tableAssociation: GenericAssociation) {
    abstract fun match(matchingRow: Row) : Any?
}