package com.trinitcore.v2.commonUtils.row

import com.trinitcore.v2.commonUtils.Defaults
import com.trinitcore.v2.commonUtils.QMap
import com.trinitcore.v2.commonUtils.then
import com.trinitcore.v2.queryObjects.Table
import com.trinitcore.v2.queryUtils.builders.Associations
import com.trinitcore.v2.queryUtils.parameters.Where
import org.json.simple.JSONArray
import java.util.*

/**
 * Created by Cormac on 17/08/2017.
 */
class Rows(public val indexColumnKey: String, public val parentTable: Table) : TreeMap<Any, RowType>(), RowType {

    public var associations: Associations = Associations()
    public var associationsAdded = false

    public fun index(i: Int): RowType {
        return this.values.toTypedArray()[i]
    }

    public fun indexAsRows(i: Int): Rows {
        return index(i) as Rows
    }

    public fun indexAsRow(i: Int): Row {
        return index(i) as Row
    }

    public fun rowValues(): List<Row> {
        return this.values.map { it as Row }
    }

    public fun rowsValues(): List<Rows> {
        return this.values.map { it as Rows }
    }

    override fun put(key: Any, value: RowType): RowType? {
        if (value is Row) {
            for (association in associations.values) {
                association.addQueryIndex(row = value)
            }
        }
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
            return updatedRowValues
        }
    }

    public fun deleteRow(where: Where): Row? {
        return delete(where)?.indexAsRow(0)
    }

    public fun delete(where: Where): Rows? {
        return this.parentTable.delete(where)?.let { deletedRowValues ->
            for (row in deletedRowValues.rowValues()) {
                remove(row[indexColumnKey]!!, row)
            }
            return deletedRowValues
        }
    }

    public fun insert(vararg values: QMap): Row? {
        return parentTable.insertValues(values)?.let { row ->
            put(row[indexColumnKey]!!, row)
            return row
        }
    }

    public fun multiValueInsert(values: Array<out Array<QMap>>): Rows? {
        return parentTable.multiValueInsert(values)?.let { insertedRows ->
            for (row in insertedRows.rowValues()) {
                put(row[indexColumnKey]!!, row)
            }
            return insertedRows
        }
    }

    public fun multiInsert(vararg values: Array<QMap>): Rows? {
        return multiValueInsert(values = values)
    }

    private fun handleMultipleValues(key: Any, value: Any): RowType? {
        if (get(key) != null) {
            // Deal with multiple columns in an index column
            val multiValues = get(key)
            if (multiValues is Rows) {
                // Add row to existing array
                val rowValue = value as Row
                multiValues.put(rowValue[multiValues.indexColumnKey]!!, rowValue)
                return super.put(key, multiValues)
            } else {
                // Create a list for multiple rows
                val newMultiValues = Rows(Defaults.indexColumnKey, parentTable)
                val initialRowValue = multiValues as Row
                val newRowValue = value as Row

                newMultiValues.put(newRowValue[Defaults.indexColumnKey]!!, newRowValue)
                newMultiValues.put(initialRowValue[Defaults.indexColumnKey]!!, initialRowValue)

                // val multiValuesWrapper = Row()
                // multiValuesWrapper.put(indexColumnKey, newMultiValues)
                return super.put(key, newMultiValues)
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

    fun addAllAssociations() {
        for (row in this.values) {
            for (association in associations.values) {
                if (row is Row) {
                    association.findAssociatingRows(row)?.let { assocRows ->
                        row.put(association.parameters.columnTitle!!, assocRows)
                    }
                } else if (row is Rows) {
                    row.values
                            .filterIsInstance<Row>()
                            .forEach {
                                association.findAssociatingRows(it)?.let { assocRows ->
                                    it.put(association.parameters.columnTitle!!, assocRows)
                                }
                            }
                }
            }
        }
        associationsAdded = true
    }

    fun toJSONArray(): JSONArray {
        // addAllAssociationsIfNotAdded()
        // NOTE: Key values of this tree should not be outputted!
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

    override fun get(key: Any): RowType? {
        return super.get(key)
    }
}