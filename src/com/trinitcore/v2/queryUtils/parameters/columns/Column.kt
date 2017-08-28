package com.trinitcore.v2.queryUtils.parameters.columns

import com.trinitcore.v2.commonUtils.then
import com.trinitcore.v2.queryObjects.SQL
import com.trinitcore.v2.queryUtils.builders.Query

/**
 * Created by Cormac on 28/08/2017.
 */
public open class Column<T>(public val name: String, public val type: String) {
    var defaultValue: T? = null
    var notNull: Boolean = false

    public fun notNull(defaultValue: T? = null): Column<T> {
        this.defaultValue = defaultValue
        this.notNull = true
        return this
    }

    public fun default(value: T): Column<T> {

        return this
    }

    override public fun toString(): String {
        return "$name $type " + (notNull then Query.NOTNULL ?: "") + ((defaultValue != null) then Query.DEFAULT + defaultValue ?: "")
    }

}