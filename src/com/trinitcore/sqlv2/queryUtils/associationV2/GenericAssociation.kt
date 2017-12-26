package com.trinitcore.sqlv2.queryUtils.associationV2

abstract class GenericAssociation {
    abstract fun generateAssociationHandler() : GenericAssociationHandler
    abstract fun getColumnTitle() : String
}