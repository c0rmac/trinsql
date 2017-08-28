package com.trinitcore.v2.queryUtils.parameters

import com.trinitcore.v2.commonUtils.QMap

/**
 * Created by Cormac on 17/08/2017.
 */
interface GenericParameters {
    fun orEqualValues(vararg parameters: QMap): GenericParameters
    fun andEqualValues(vararg parameters: QMap): GenericParameters
    fun orLikeValues(vararg parameters: QMap): GenericParameters
    fun andLikeValues(vararg parameters: QMap): GenericParameters
    fun value(key: String, value: Any): GenericParameters
}