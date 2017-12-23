package com.trinitcore.sqlv2.queryUtils.associations

import com.trinitcore.sqlv2.commonUtils.row.Row

class ReformatAssociatingResults(val handler: (row: Row) -> Any?) : GenericAssociatingResults() {
    override fun match(matchingRow: Row): Any? {
        return handler(matchingRow)
    }
}