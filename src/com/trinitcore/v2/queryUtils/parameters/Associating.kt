package com.trinitcore.v2.queryUtils.parameters

import com.trinitcore.v2.commonUtils.then

/**
 * Created by Cormac on 17/08/2017.
 */
class Associating(columnName: String, columnTitle: String? = null, childColumnName: String) {

    // Associations map will identify from columnName
    val columnName = columnName
    public val columnTitle = (columnTitle == null) then columnName ?: columnTitle

    public val childColumnName = childColumnName

}