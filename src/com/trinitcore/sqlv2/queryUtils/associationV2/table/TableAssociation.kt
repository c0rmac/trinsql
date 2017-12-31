package com.trinitcore.sqlv2.queryUtils.associationV2.table

import com.trinitcore.sqlv2.queryUtils.associationV2.Associating
import com.trinitcore.sqlv2.queryUtils.associationV2.GenericAssociation
import com.trinitcore.sqlv2.queryUtils.associationV2.GenericAssociationHandler
import com.trinitcore.sqlv2.queryUtils.associationV2.GenericAssociationsManager
import com.trinitcore.sqlv2.queryUtils.associationV2.table.handler.TableAssociationHandler


open class TableAssociation(val tableName: String,
                            val notArray: Boolean,
                            val parameters: Associating) : GenericAssociation(), GenericAssociationsManager {
    val associations = mutableListOf<GenericAssociation>()

    override fun addAssociation(association: GenericAssociation): TableAssociation {
        associations.add(association)
        return this
    }

    override fun generateAssociationHandler(): GenericAssociationHandler {
        return TableAssociationHandler(this, parameters)
    }

    override fun getColumnTitle(): String = parameters.columnTitle

}