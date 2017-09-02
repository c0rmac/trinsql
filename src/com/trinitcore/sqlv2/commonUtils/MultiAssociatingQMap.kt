package com.trinitcore.sqlv2.commonUtils

/**
 * Created by Cormac on 29/08/2017.
 */
class MultiAssociatingQMap(key: String, vararg value: Array<QMap>) : QMap(key, value) {
    fun getValueAsQMapArrays() : Array<Array<out QMap>> {
        return value as Array<Array<out QMap>>
    }
}