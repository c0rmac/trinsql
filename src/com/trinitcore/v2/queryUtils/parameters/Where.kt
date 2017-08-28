package com.trinitcore.v2.queryUtils.parameters

import com.trinitcore.v2.commonUtils.QMap
import com.trinitcore.v2.commonUtils.then
import com.trinitcore.v2.queryUtils.builders.Query

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

    override fun andLikeValues(vararg parameters: QMap): GenericParameters {
        values(nameSpace = Query.AND, equalizer = Query.LIKE, parameters = parameters)
        return this
    }

    override fun orLikeValues(vararg parameters: QMap): GenericParameters {
        values(nameSpace = Query.OR, equalizer = Query.LIKE, parameters = parameters)
        return this
    }

    override fun orEqualValues(vararg parameters: QMap): Where {
        values(nameSpace = Query.OR, equalizer = Query.EQUALS, parameters = parameters)
        return this
    }

    override fun andEqualValues(vararg parameters: QMap): Where {
        values(nameSpace = Query.AND, equalizer = Query.EQUALS, parameters = parameters)
        return this
    }

    override fun value(key: String, value: Any): Where {
        orLikeValues(QMap(key = key, value = value))
        return this
    }

    override fun toString(): String {
        return (queryString == Query.WHERE) then "" ?: this.queryString
    }

    public fun getQueryParameters(): List<Any> {
        return queryParameters.toList()
    }

}