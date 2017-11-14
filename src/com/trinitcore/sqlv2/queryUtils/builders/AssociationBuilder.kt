package com.trinitcore.sqlv2.queryUtils.builders

import com.trinitcore.sqlv2.commonUtils.row.Rows
import com.trinitcore.sqlv2.queryObjects.Table
import com.trinitcore.sqlv2.queryUtils.associations.*
import com.trinitcore.sqlv2.queryUtils.parameters.Where

/**
 * Created by Cormac on 31/10/2017.
 */
public class AssociationBuilder : GenericBuilder, GenericAssociationsManager {
    private val tableName: String
    private val parameters: Associating
    private val notArray: Boolean

    private val subAssociations: Associations = Associations()
    private val subAssociationBuilders: Associations = Associations()

    constructor(tableName: String, notArray: Boolean = false, parameters: Associating) {
        this.tableName = tableName
        this.parameters = parameters
        this.notArray = notArray
    }

    override fun build(): Association {
        val a = Association(tableName, notArray, parameters)
        subAssociations.forEach { _, sub ->  a.addAssociation(sub) }
        subAssociationBuilders.forEach { _, sub -> a.addAssociation(sub) }
        return a
    }

    override fun addAssociation(association: AssociationBuilder): AssociationBuilder {
        val build = association.build()
        this.subAssociationBuilders.put(build.getColumnTitle(), build)
        return this
    }

    override fun addAssociation(association: GenericAssociation): AssociationBuilder {
        this.subAssociations.put(association.getColumnTitle(), association)
        return this
    }
}