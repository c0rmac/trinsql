package com.trinitcore.sqlv2.commonUtils

/**
 * Created by Cormac on 29/08/2017.
 */
public class AssociatingQMap(columnName: String, vararg value: QMap) : QMap(columnName, value) {
    fun getValueAsQMapArray() : Array<out QMap> {
        return value as Array<out QMap>
    }
}