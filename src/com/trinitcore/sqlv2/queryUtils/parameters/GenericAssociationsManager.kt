package com.trinitcore.sqlv2.queryUtils.parameters

import com.trinitcore.sqlv2.queryUtils.builders.Association

/**
 * Created by Cormac on 19/08/2017.
 */
interface GenericAssociationsManager {
    public fun addAssociation(association: Association): GenericAssociationsManager
}