package com.trinitcore.sqlv2.queryUtils.associationV2

import com.trinitcore.sqlv2.queryUtils.parameters.Where

class Associating(val columnName: String, columnTitle: String? = null, public val childColumnName: String) {

    // Associations HashMap will identify from columnTitle
    public val columnTitle = columnTitle ?: columnName

    public var deleteRowIfNotFound = false

    val skipParentRowHasValues = hashMapOf<String,Any>()
    val skipParentRowExcludesValues = hashMapOf<String,Any>()

    fun deleteRowIfNotFound(): Associating {
        this.deleteRowIfNotFound = true
        return this
    }

    private val shouldMatches = hashMapOf<String, Any>()
    var skipParentRowIfMatchNotFound = false
    fun shouldMatch(column:String, value:Any) : Associating {
        shouldMatches[column] = value
        return this
    }

    var blankRowsIfMatchNotFound = false
    fun blankRowsIfMatchNotFound() : Associating {
        blankRowsIfMatchNotFound = true
        return this
    }

    fun skipParentRowIfMatchNotFound() : Associating {
        skipParentRowIfMatchNotFound = true
        return this
    }

    fun generateWhereParameters(): Where {
        val where = Where()
        shouldMatches.forEach { column, value ->
            where.andEqualValue(column, value)
        }
        return where
    }

    // Parameters for optimising queries
    fun skipRowIfParentRowHasValue(column: String, value: Any) : Associating {
        skipParentRowHasValues[column] = value
        return this
    }

    fun skipRowIfParentRowExcludesValue(column: String, value: Any) : Associating {
        skipParentRowExcludesValues[column] = value
        return this
    }
}