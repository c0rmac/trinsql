package com.trinitcore.sqlv2.queryUtils.parameters

/**
 * Created by Cormac on 17/08/2017.
 */
class Associating(columnName: String, columnTitle: String? = null, childColumnName: String) {

    val columnName = columnName
    // Associations HashMap will identify from columnTitle
    public val columnTitle = columnTitle ?: columnName

    public val childColumnName = childColumnName

}