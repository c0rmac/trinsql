package com.trinitcore.sqlv2.queryUtils.associationV2

/**
 * Created by Cormac on 17/08/2017.
 */
class Associations : HashMap<String, GenericAssociation>() {

    fun addAssociation(association: GenericAssociation) {
        put(association.getColumnTitle(),association)
    }

    fun handlers() : Map<String, GenericAssociationHandler> = mapValues { it.value.generateAssociationHandler() }

}