package com.trinitcore.sqlv2.queryUtils.associations

import com.trinitcore.sqlv2.commonUtils.row.Row
import com.trinitcore.sqlv2.commonUtils.row.RowType
import com.trinitcore.sqlv2.commonUtils.row.Rows

class AssociatingResults(val queryRows: Rows, val association: Association) : GenericAssociatingResults() {

    override fun match(matchingRow: Row): RowType? {
        val child = this.queryRows!![matchingRow[association.parameters.columnName]]
        if (child is Rows) {
            if (association.notArray) return child.firstEntry().value
            return child
        } else if (child is Row) {
            // This usually happens when child indexColumnKey = ID for the queryTable
            if (association.notArray) return child

            val childRows = Rows(association.parameters.childColumnName, child.parentTable)
            childRows.put(matchingRow[association.parameters.columnName]!!, child)
            return childRows
        }
        return null
    }

}