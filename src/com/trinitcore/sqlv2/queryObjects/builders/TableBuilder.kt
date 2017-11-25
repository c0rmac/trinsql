package com.trinitcore.sqlv2.queryObjects.builders

import com.trinitcore.sqlv2.queryObjects.Table
import com.trinitcore.sqlv2.queryUtils.associations.Associations
import com.trinitcore.sqlv2.queryUtils.associations.GenericAssociation
import com.trinitcore.sqlv2.queryUtils.associations.GenericAssociationsManager
import com.trinitcore.sqlv2.queryUtils.builders.AssociationBuilder

class TableBuilder(public val tableName: String,
                       private var tableColumns: Array<out String>) : GenericAssociationsManager {
    public var associations = Associations()

    override fun addAssociation(association: AssociationBuilder): TableBuilder {
        associations.addAssociation(association)
        return this
    }

    override fun addAssociation(association: GenericAssociation): TableBuilder {
        associations.addAssociation(association)
        return this
    }

    fun build():Table {
        val build = Table(tableName)
        build.associations = associations
        return build
    }

}














