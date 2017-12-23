package com.trinitcore.sqlv2.queryUtils.associations

import com.trinitcore.sqlv2.commonUtils.row.Row

/**
 * Created by Cormac on 23/10/2017.
 */
abstract class GenericAssociation() {
    abstract fun findAssociatingResults(): GenericAssociatingResults
    abstract fun getColumnTitle(): String
}