package com.trinitcore.sqlv2.queryUtils.associations

import com.trinitcore.sqlv2.commonUtils.row.Row

abstract class GenericAssociatingResults() {
    abstract fun match(matchingRow: Row) : Any?
}