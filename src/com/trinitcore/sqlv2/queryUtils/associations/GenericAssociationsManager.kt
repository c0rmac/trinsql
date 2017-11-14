package com.trinitcore.sqlv2.queryUtils.associations

import com.trinitcore.sqlv2.queryUtils.builders.AssociationBuilder

/**
 * Created by Cormac on 19/08/2017.
 */
interface GenericAssociationsManager {
    public fun addAssociation(association: GenericAssociation): GenericAssociationsManager
    public fun addAssociation(association: AssociationBuilder): GenericAssociationsManager
}