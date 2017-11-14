package com.trinitcore.sqlv2.queryUtils.parameters

import com.trinitcore.sqlv2.commonUtils.QMap

/**
 * Created by Cormac on 17/08/2017.
 */
interface GenericParameters {
    fun andEqualValues(vararg parameters: QMap): GenericParameters
    fun andLikeValues(vararg parameters: QMap): GenericParameters
    fun andGreaterThanOrEqualValues(vararg parameters: QMap): GenericParameters
    fun andGreaterThanValues(vararg parameters: QMap): GenericParameters
    fun andLessThanOrEqualValues(vararg parameters: QMap): GenericParameters
    fun andLessThanValues(vararg parameters: QMap): GenericParameters

    fun orLikeValues(vararg parameters: QMap): GenericParameters
    fun orEqualValues(vararg parameters: QMap): GenericParameters
    fun orGreaterThanOrEqualValues(vararg parameters: QMap): GenericParameters
    fun orGreaterThanValues(vararg parameters: QMap): GenericParameters
    fun orLessThanOrEqualValues(vararg parameters: QMap): GenericParameters
    fun orLessThanValues(vararg parameters: QMap): GenericParameters

    fun value(key: String, value: Any): GenericParameters
}