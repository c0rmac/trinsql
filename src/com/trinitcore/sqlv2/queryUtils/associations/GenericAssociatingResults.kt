package com.trinitcore.sqlv2.queryUtils.associations

import com.trinitcore.sqlv2.commonUtils.QMap
import com.trinitcore.sqlv2.commonUtils.row.Row

abstract class GenericAssociatingResults() {
    abstract fun match(matchingRow: Row, vararg parameters: QMap) : Any?
}