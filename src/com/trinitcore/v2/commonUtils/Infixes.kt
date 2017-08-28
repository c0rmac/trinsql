package com.trinitcore.v2.commonUtils

/**
 * Created by Cormac on 17/08/2017.
 */
infix fun <T> Boolean.then(param: T): T? = if (this) param else null