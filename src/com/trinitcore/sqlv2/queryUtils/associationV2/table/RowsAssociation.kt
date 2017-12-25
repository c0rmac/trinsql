package com.trinitcore.sqlv2.queryUtils.associationV2.table

import com.trinitcore.sqlv2.queryUtils.associationV2.Associating

class RowsAssociation(tableName: String,
                      parameters: Associating) :

        TableAssociation(tableName,
                false,
                parameters) {
}