package com.trinitcore.sqlv2.queryObjects.builders

import com.trinitcore.sqlv2.queryObjects.Table
import com.trinitcore.sqlv2.queryUtils.associationV2.Associations
import com.trinitcore.sqlv2.queryUtils.associationV2.GenericAssociation
import com.trinitcore.sqlv2.queryUtils.associationV2.GenericAssociationsManager

open class TableBuilder(public val tableName: String,
                        private var tableColumns: Array<out String> = arrayOf()) : GenericAssociationsManager {
    public var associations = Associations()

    override fun addAssociation(association: GenericAssociation): TableBuilder {
        associations.addAssociation(association)
        return this
    }

    open fun build():Table {
        val build = Table(tableName)
        build.associations = associations
        return build
    }

}














