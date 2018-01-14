package com.trinitcore.sqlv2.commonUtils.row

import com.trinitcore.sqlv2.commonUtils.Defaults
import com.trinitcore.sqlv2.commonUtils.QMap
import com.trinitcore.sqlv2.commonUtils.then
import com.trinitcore.sqlv2.queryObjects.Table
import com.trinitcore.sqlv2.queryUtils.associationV2.Associations
import com.trinitcore.sqlv2.queryUtils.associationV2.GenericAssociationHandler
import com.trinitcore.sqlv2.queryUtils.associationV2.table.handler.TableAssociationHandler
import com.trinitcore.sqlv2.queryUtils.module.DataModule
import com.trinitcore.sqlv2.queryUtils.parameters.Where
import org.json.simple.JSONArray
import java.util.*

/**
 * Created by Cormac on 17/08/2017.
 */
open class Rows(public val indexColumnKey: String, public val parentTable: Table) : TreeMap<Any, RowType>(), RowType {

    public var associations: Associations = Associations()
    set(value) {
        associationHandlers = value.handlers()
    }

    public var associationHandlers: Map<String, GenericAssociationHandler> = mapOf()

    public var associationsAdded = false

    public val dispatchedUpdates = mutableListOf<(rows: Rows) -> Unit>()

    private var canRunDispatchedUpdates: Boolean = true

    public fun index(i: Int): RowType {
        return this.values.toTypedArray()[i]
    }

    public fun indexAsRows(i: Int): Rows {
        return index(i) as Rows
    }

    public fun indexAsRow(i: Int): Row {
        return index(i) as Row
    }
/*
    public fun rowValues(): List<Row> {
        return this.values.filterIsInstance<Row>()
    }

    public fun rowsValues(): List<Rows> {
        return this.values.filterIsInstance<Rows>()
    }
    */

    // Warning: May remove reference of this object!!
    public fun rowValues(): List<Row> {
        return this.values.filterIsInstance<Row>()
    }

    public fun rowsValues(): List<Rows> {
        return this.values.filterIsInstance<Rows>()
    }

    public fun findRowsByColumnValue(column: String, value: Any) : Rows {
        return findRowsByColumnValue(arrayOf(QMap(column, value)))
    }

    internal open fun rowsCreation(indexColumnKey: String = Defaults.indexColumnKey) : Rows = Rows(indexColumnKey, parentTable)

    public fun findRowsByColumnValue(parameterMap: Array<QMap>) : Rows {
        val rows = rowsCreation(indexColumnKey)
        this.values.forEach {
            when (it) {
                is Rows -> {
                    val foundRows = it.findRowsByColumnValue(parameterMap)
                    if (!rows.isEmpty()) {
                        rows.putAll(foundRows)
                    }
                }
                is Row -> {
                    var allParametersSatisfied = false
                    for (parameter in parameterMap) {
                        allParametersSatisfied = it[parameter.key] == parameter.value
                        if (!allParametersSatisfied) return@forEach
                    }

                    if (allParametersSatisfied) rows.put(it[indexColumnKey]!!, it)
                }
                else -> {

                }
            }
        }

        return rows
    }

    fun handleInsertRowAssociations(value: RowType) {
        if (value is Row) {
            for (association in associationHandlers.values) {
                if (association is TableAssociationHandler) association.addQueryIndex(row = value)
            }
        }
    }

    override fun put(key: Any, value: RowType): RowType? {
        handleInsertRowAssociations(value)
        var multiValuedReturn = handleMultipleValues(key, value)
        return (multiValuedReturn != null) then multiValuedReturn ?: super.put(key, value)
    }

    public fun updateRow(where: Where, vararg values: QMap): Row? {
        return updateValues(where, values)?.indexAsRow(0)
    }

    public fun update(where: Where, vararg values: QMap): Rows? {
        return updateValues(where, values)
    }

    private fun updateValues(where: Where, values: Array<out QMap>): Rows? {
        return this.parentTable.updateValues(where, values)?.let { updatedRowValues ->
            for (row in updatedRowValues.rowValues()) {
                remove(row[indexColumnKey]!!, row)
                put(row[indexColumnKey]!!, row)
            }
            didPerformUpdates()
            return updatedRowValues
        }
    }

    public fun deleteRow(where: Where): Row? {
        return delete(where)?.indexAsRow(0)
    }

    public fun delete(where: Where): Rows? {
        return this.parentTable.delete(where)?.let { deletedRowValues ->
            val deletedRows = deletedRowValues.rowValues()
            for (row in deletedRows) {
                remove(row[indexColumnKey]!!, row)
            }
            didPerformUpdates()
            return deletedRowValues
        }
    }

    public fun insert(vararg values: QMap): Row? {
        return parentTable.insertValues(values)?.let { row ->
            put(row[indexColumnKey]!!, row)
            didPerformUpdates()
            return row
        }
    }

    public fun multiValueInsert(values: Array<out Array<QMap>>): Rows? {
        return parentTable.multiValueInsert(values)?.let { insertedRows ->
            for (row in insertedRows.rowValues()) {
                put(row[indexColumnKey]!!, row)
            }
            didPerformUpdates()
            return insertedRows
        }
    }

    public fun multiInsert(vararg values: Array<QMap>): Rows? {
        return multiValueInsert(values = values)
    }

    private fun handleMultipleValues(key: Any, value: Any): RowType? {
        if (indexColumnKey != Defaults.indexColumnKey) {
            if (get(key) != null) {
                // Deal with multiple columns in an index column
                val multiValues = get(key)
                if (multiValues is Rows) {
                    // Add row to existing array
                    val rowValue = value as Row
                    multiValues.put(rowValue[multiValues.indexColumnKey]!!, rowValue)
                    return super.put(key, multiValues)
                } else if (multiValues is Row) {
                    // Create a list for multiple rows
                    val newMultiValues = rowsCreation()
                    newMultiValues.associations = parentTable.associations
                    val initialRowValue = multiValues
                    val newRowValue = value as Row

                    newMultiValues.put(newRowValue[Defaults.indexColumnKey]!!, newRowValue)
                    newMultiValues.put(initialRowValue[Defaults.indexColumnKey]!!, initialRowValue)

                    // val multiValuesWrapper = Row()
                    // multiValuesWrapper.put(indexColumnKey, newMultiValues)
                    return super.put(key, newMultiValues)
                }
            }
        }
        return null
    }

    fun addAllAssociationsIfNotAdded() {
        if (!associationsAdded) {
            addAllAssociations()
        }
    }

    fun addAssociation() {

    }

    // These methods must run runDispatchedUpdates()
    fun addAllAssociations() {

        fun dealWithEmptyAssociations(association: TableAssociationHandler, assocObject: Any?, row: Row) {
            if (association.parameters.deleteRowIfNotFound && assocObject == null)
                dispatchedUpdates.add({
                    remove(row.getID(), row)
                })
            else if (association.parameters.skipParentRowIfMatchNotFound && assocObject == null) {
                dispatchedUpdates.add({
                    remove(row.getID(), row)
                })
            }
            /*
            else if (association.parameters.shouldMatches.isNotEmpty() && association.parameters.skipParentRowIfMatchNotFound) {
                association.parameters.shouldMatches.forEach { column, value ->
                    if (assocObject is Row) {
                        if (assocObject[column] == value) {
                            dispatchedUpdates.add({
                                remove(row.getID(), row)
                            })
                        }
                    } else if (assocObject is Rows) {
                        for (assocRow in assocObject.rowValues()) {
                            if (assocObject[column] == value) {
                                dispatchedUpdates.add({
                                    remove(row.getID(), row)
                                })
                            }
                        }
                    }
                }
            }*/
        }

        val handlers = associationHandlers.values
        for (row in this.values) {
            for (associationHandler in associationHandlers.values) {
                if (row is Row) {
                    val assocRow = associationHandler.match(row)?.let { assocRows ->
                        row.put(associationHandler.tableAssociation.getColumnTitle(), assocRows)
                        assocRows
                    }

                    if (associationHandler is TableAssociationHandler) dealWithEmptyAssociations(associationHandler, assocRow, row)
                } else if (row is Rows) {
                    row.values
                            .filterIsInstance<Row>()
                            .forEach {
                                val assocRow = associationHandler.match(it)?.let { assocRows ->
                                    it.put(associationHandler.tableAssociation.getColumnTitle(), assocRows)
                                    assocRows
                                }

                                if (associationHandler is TableAssociationHandler) dealWithEmptyAssociations(associationHandler, assocRow, it)
                            }
                } else if (row is DataModule) {
                    val assocRow = associationHandler.match(row.row)?.let { assocRows ->
                        /* if (associationHandler is TableAssociationHandler) {
                            associationHandler.tableAssociation.moduleInitialisation?.let {
                                row.initialiseAttribute(associationHandler.tableAssociation.getColumnTitle(), assocRows)
                            }
                        } else */row.initialiseAttribute(associationHandler.tableAssociation.getColumnTitle(), assocRows)
                        //row.put(associationHandler.tableAssociation.getColumnTitle(), assocRows)
                        assocRows
                    }

                    //if (associationHandler is TableAssociationHandler) dealWithEmptyAssociations(associationHandler, assocRow, row)
                }
            }
        }
        associationsAdded = true
        runDispatchedUpdates()
    }

    fun toJSONArray(): JSONArray {
        // addAllAssociationsIfNotAdded()
        // NOTE: Key values of this tree should not be outputted!
        runDispatchedUpdates()
        val jsonArray = JSONArray()
        for (value in this.values.toList()) {
            if (value is Row) {
                jsonArray.add(value.toJSONObject())
            } else if (value is Rows) {
                jsonArray.add(value.toJSONArray())
            }
        }
        return jsonArray
    }

    fun remove(key: Any, row: Row) {
        runDispatchedUpdates()

        get(key)?.let { value ->
            when (value) {
                is Rows -> {
                    // Removing a row from a Rows object
                    value.remove(row[Defaults.indexColumnKey])
                }
                else -> {
                    remove(key)
                }
            }
        }
    }

    override fun remove(key: Any) : RowType? {
        val rowType = super.remove(key)
        if (rowType is Row) {
            for (association in associationHandlers.values) {
                if (association is TableAssociationHandler) association.removeQueryIndex(row = rowType)
            }
        }
        return rowType
    }

    override fun get(key: Any): RowType? {
        runDispatchedUpdates()
        return super.get(key)
    }

    fun willPerformUpdates() {

    }

    fun didPerformUpdates() {
        addAllAssociations()
    }

    private fun runDispatchedUpdates() {
            if (dispatchedUpdates.size != 0 && canRunDispatchedUpdates) {
                synchronized(this) {
                    canRunDispatchedUpdates = false
                    dispatchedUpdates.forEach {
                        it(this)
                    }
                    canRunDispatchedUpdates = true

                    dispatchedUpdates.clear()
                }
            }
    }

}