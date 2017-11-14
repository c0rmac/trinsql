package com.trinitcore.sqlv2.queryUtils.parameters

import com.trinitcore.sqlv2.commonUtils.QMap
import com.trinitcore.sqlv2.commonUtils.then
import com.trinitcore.sqlv2.queryUtils.builders.Query

/**
 * Created by Cormac on 17/08/2017.
 */
class Where : GenericParameters {
    private var queryString: String = Query.WHERE
    private var queryParameters: MutableList<Any> = mutableListOf()

    private fun values(nameSpace: String, equalizer: String, parameters: Array<out QMap>) {
        when (this.queryString != Query.WHERE) { true -> this.queryString += nameSpace
        }

        for (parameter in parameters) {
            this.queryString += Query.WHERE(column = parameter.key, equalizer = equalizer) + nameSpace
            this.queryParameters.add(parameter.value)
        }
        this.queryString = this.queryString.removeSuffix(suffix = nameSpace)
    }

    // AND LIKE
    override fun andLikeValues(vararg parameters: QMap): Where {
        values(nameSpace = Query.AND, equalizer = Query.LIKE, parameters = parameters)
        return this
    }

    fun andLikeValue(key: String, value: Any): Where {
        return andLikeValues(QMap(key, value))
    }

    // AND =
    override fun andEqualValues(vararg parameters: QMap): Where {
        values(nameSpace = Query.AND, equalizer = Query.EQUALS, parameters = parameters)
        return this
    }

    fun andEqualValue(key: String, value: Any): Where {
        return andEqualValues(QMap(key, value))
    }

    // AND GREATER THAN OR EQUAL >=
    override fun andGreaterThanOrEqualValues(vararg parameters: QMap): Where {
        values(Query.AND, Query.GREATER_THAN_OR_EQUALS, parameters)
        return this
    }

    fun andGreaterThanOrEqualValue(key: String, value: Any): Where {
        return andGreaterThanOrEqualValues(QMap(key, value))
    }

    // AND GREATER THAN >
    override fun andGreaterThanValues(vararg parameters: QMap): Where {
        values(Query.AND, Query.GREATER_THAN, parameters)
        return this
    }

    fun andGreaterThanValue(key: String, value: Any): Where {
        return andGreaterThanValues(QMap(key, value))
    }

    // AND LESS THAN OR EQUAL <=
    override fun andLessThanOrEqualValues(vararg parameters: QMap): Where {
        values(Query.AND, Query.LESS_THAN_OR_EQUALS, parameters)
        return this
    }

    fun andLessThanOrEqualValue(key: String, value: Any): Where {
        return andLessThanOrEqualValues(QMap(key, value))
    }

    // AND LESS THAN <
    override fun andLessThanValues(vararg parameters: QMap): Where {
        values(Query.AND, Query.LESS_THAN, parameters)
        return this
    }

    fun andLessThanValue(key: String, value: Any): Where {
        return andLessThanValues(QMap(key, value))
    }

    // OR LIKE
    override fun orLikeValues(vararg parameters: QMap): Where {
        values(nameSpace = Query.OR, equalizer = Query.LIKE, parameters = parameters)
        return this
    }

    fun orLikeValue(key: String, value: Any): Where {
        return orLikeValues(QMap(key, value))
    }

    // OR =
    override fun orEqualValues(vararg parameters: QMap): Where {
        values(nameSpace = Query.OR, equalizer = Query.EQUALS, parameters = parameters)
        return this
    }

    fun orEqualValue(key: String, value: Any): Where {
        return orEqualValues(QMap(key,value))
    }

    // OR GREATER THAN OR EQUAL >=
    override fun orGreaterThanOrEqualValues(vararg parameters: QMap): Where {
        values(Query.OR, Query.GREATER_THAN_OR_EQUALS, parameters)
        return this
    }

    fun orGreaterThanOrEqualValue(key: String, value: Any): Where {
        return orGreaterThanOrEqualValues(QMap(key, value))
    }

    // OR GREATER THAN >
    override fun orGreaterThanValues(vararg parameters: QMap): Where {
        values(Query.OR, Query.GREATER_THAN, parameters)
        return this
    }

    fun orGreaterThanValue(key: String, value: Any): Where {
        return orGreaterThanValues(QMap(key, value))
    }

    // OR LESS THAN OR EQUAL <=
    override fun orLessThanOrEqualValues(vararg parameters: QMap): Where {
        values(Query.OR, Query.LESS_THAN_OR_EQUALS, parameters)
        return this
    }

    fun orLessThanOrEqualValue(key: String, value: Any): Where {
        return orLessThanOrEqualValues(QMap(key, value))
    }

    // OR LESS THAN <
    override fun orLessThanValues(vararg parameters: QMap): Where {
        values(Query.OR, Query.LESS_THAN, parameters)
        return this
    }

    fun orLessThanValue(key: String, value: Any): Where {
        return orLessThanValues(QMap(key, value))
    }

    // Plain
    override fun value(key: String, value: Any): Where {
        andEqualValues(QMap(key = key, value = value))
        return this
    }

    fun orderBy(column: String, rotation: String) {

    }

    override fun toString(): String {
        return (queryString == Query.WHERE) then "" ?: this.queryString
    }

    public fun getQueryParameters(): List<Any> {
        return queryParameters.toList()
    }

    public fun join(where: Where, separator: String, wrap: Boolean = false) : Where {
        if (where.queryParameters.size == 0) return this

        if (queryString != Query.WHERE) queryString += separator

        if (wrap) this.queryString += "("
        queryString += where.toString().removePrefix(Query.WHERE)

        if (wrap) this.queryString += ")"

        queryParameters.addAll(where.queryParameters)

        return this
    }

}