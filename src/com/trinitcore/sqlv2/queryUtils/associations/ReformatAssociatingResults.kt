package com.trinitcore.sqlv2.queryUtils.associations

import com.trinitcore.sqlv2.commonUtils.QMap
import com.trinitcore.sqlv2.commonUtils.row.Row

class ReformatAssociatingResults(val handler: (row: Row) -> Any?) : GenericAssociatingResults() {
    override fun match(matchingRow: Row, vararg parameters: QMap): Any? {
        return handler(matchingRow)
    }
}