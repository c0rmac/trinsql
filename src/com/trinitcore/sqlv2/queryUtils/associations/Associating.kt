package com.trinitcore.sqlv2.queryUtils.associations

import com.trinitcore.sqlv2.queryUtils.parameters.Where

/**
 * Created by Cormac on 17/08/2017.
 */
class Associating(val columnName: String, columnTitle: String? = null, public val childColumnName: String) {

    // Associations HashMap will identify from columnTitle
    public val columnTitle = columnTitle ?: columnName

    public var deleteRowIfNotFound = false
    fun deleteRowIfNotFound(): Associating {
        this.deleteRowIfNotFound = true
        return this
    }

    private val shouldMatches = hashMapOf<String, Any>()
    var skipRowIfExcludedMatchFound = false
    fun shouldMatch(column:String, value:Any) : Associating {
        shouldMatches[column] = value
        return this
    }

    fun skipRowIfExcludedMatchFound() : Associating {
        skipRowIfExcludedMatchFound = true
        return this
    }

    fun generateWhereParameters(): Where {
        val where = Where()
        shouldMatches.forEach { column, value ->
            where.andEqualValue(column, value)
        }
        return where
    }
}