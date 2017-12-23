package com.trinitcore.sqlv2.queryUtils.associations

import com.trinitcore.sqlv2.commonUtils.QMap
import com.trinitcore.sqlv2.commonUtils.row.Row
import com.trinitcore.sqlv2.commonUtils.row.RowType
import com.trinitcore.sqlv2.commonUtils.row.Rows
import com.trinitcore.sqlv2.queryObjects.Table
import com.trinitcore.sqlv2.queryUtils.builders.AssociationBuilder
import com.trinitcore.sqlv2.queryUtils.builders.Query
import com.trinitcore.sqlv2.queryUtils.parameters.Where

/**
 * Created by Cormac on 17/08/2017.
 */
class Association : GenericAssociation, GenericAssociationsManager {
    override fun getColumnTitle(): String {
        return parameters.columnTitle
    }

    private val tableName: String
    public val parameters: Associating
    internal val notArray: Boolean

    public val indexQueryParameters: Where = Where()
    val queryTable: Table

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

    override fun findAssociatingResults(): AssociatingResults {
        return AssociatingResults(queryTable.find(where = Where().join(indexQueryParameters, Query.AND, true).join(parameters.generateWhereParameters(), Query.AND)), this)
    }

    override public fun addAssociation(association: GenericAssociation): Association {
        this.queryTable.addAssociation(association)
        return this
    }

    override public fun addAssociation(association: AssociationBuilder): Association {
        this.queryTable.addAssociation(association)
        return this
    }

}