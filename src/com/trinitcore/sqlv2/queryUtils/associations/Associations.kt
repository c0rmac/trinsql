package com.trinitcore.sqlv2.queryUtils.associations

import com.trinitcore.sqlv2.queryUtils.builders.AssociationBuilder

/**
 * Created by Cormac on 17/08/2017.
 */
class Associations : HashMap<String, GenericAssociation>() {
    fun addAssociation(association: GenericAssociation) {
        put(association.getColumnTitle(),association)
    }

    fun addAssociation(associationBuilder: AssociationBuilder) {
        val build = associationBuilder.build()
        addAssociation(build)
    }
}