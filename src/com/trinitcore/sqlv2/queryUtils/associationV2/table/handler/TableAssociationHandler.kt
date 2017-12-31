package com.trinitcore.sqlv2.queryUtils.associationV2.table.handler

import com.trinitcore.sqlv2.commonUtils.QMap
import com.trinitcore.sqlv2.commonUtils.row.Row
import com.trinitcore.sqlv2.commonUtils.row.Rows
import com.trinitcore.sqlv2.queryObjects.Table
import com.trinitcore.sqlv2.queryUtils.associationV2.Associating
import com.trinitcore.sqlv2.queryUtils.associationV2.GenericAssociationHandler
import com.trinitcore.sqlv2.queryUtils.associationV2.table.TableAssociation
import com.trinitcore.sqlv2.queryUtils.builders.Query
import com.trinitcore.sqlv2.queryUtils.parameters.Where

class TableAssociationHandler(tableAssociation: TableAssociation, val parameters: Associating) : GenericAssociationHandler(tableAssociation) {

    val queryTable: Table = Table(tableAssociation.tableName)
    var queryRows: Rows? = null
    val notArray = tableAssociation.notArray

    init {
        queryTable.indexColumnKey = parameters.childColumnName
        tableAssociation.associations.forEach { queryTable.associations.addAssociation(it) }
    }

    public val indexQueryParameters: Where = Where()
    public fun addQueryIndex(row: Row) {
        row[parameters.columnName]?.let {
            indexQueryParameters.orEqualValues(
                    QMap(parameters.childColumnName, it)
            )
        }
    }

    override fun match(matchingRow: Row): Any? {
        if (queryRows == null) this.queryRows = queryTable.find(where = Where().join(indexQueryParameters, Query.AND, true).join(parameters.generateWhereParameters(), Query.AND))
        val child = this.queryRows!![matchingRow[parameters.columnName]]
        if (child is Rows) {
            if (notArray) return child.firstEntry().value
            return child
        } else if (child is Row) {
            // This usually happens when child indexColumnKey = ID for the queryTable
            if (notArray) return child

            val childRows = Rows(parameters.childColumnName, child.parentTable)
            childRows.put(matchingRow[parameters.columnName]!!, child)
            return childRows
        }
        return null
    }
}