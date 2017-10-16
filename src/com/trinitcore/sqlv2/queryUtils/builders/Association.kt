package com.trinitcore.sqlv2.queryUtils.builders

import com.trinitcore.sqlv2.commonUtils.QMap
import com.trinitcore.sqlv2.commonUtils.row.Row
import com.trinitcore.sqlv2.commonUtils.row.Rows
import com.trinitcore.sqlv2.queryObjects.Table
import com.trinitcore.sqlv2.queryUtils.parameters.Associating
import com.trinitcore.sqlv2.queryUtils.parameters.GenericAssociationsManager
import com.trinitcore.sqlv2.queryUtils.parameters.Where

/**
 * Created by Cormac on 17/08/2017.
 */
class Association : GenericAssociationsManager {
    private val tableName: String
    public val parameters: Associating
    private val notArray: Boolean

    public val indexQueryParameters: Where = Where()
    val queryTable: Table
    private var queryRows: Rows? = null

    constructor(tableName: String, notArray: Boolean = false, parameters: Associating) {
        this.tableName = tableName
        this.parameters = parameters
        this.queryTable = Table(tableName)
        this.notArray = notArray
        queryTable.indexColumnKey = parameters.childColumnName
    }

    public fun addQueryIndex(row: Row) {
        row[parameters.columnName]?.let {
            indexQueryParameters.orEqualValues(
                    QMap(parameters.childColumnName, it)
            )
        }
    }

    public fun findAssociatingRows(matchingRow: Row): Any? {
        if (queryRows == null) this.queryRows = queryTable.find(where = indexQueryParameters)
        val child = this.queryRows!![matchingRow[parameters.columnName]]
        if (child is Rows) {
            if (this.notArray) return child.firstEntry().value
            return child
        } else if (child is Row) {
            // This usually happens when child indexColumnKey = ID for the queryTable
            if (this.notArray) return child

            val childRows = Rows(parameters.childColumnName, child.parentTable)
            childRows.put(matchingRow[parameters.columnName]!!, child)
            return childRows
        }
        return null
    }

    override public fun addAssociation(association: Association): Association {
        this.queryTable.addAssociation(association)
        return this
    }

}