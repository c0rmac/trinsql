package com.trinitcore.sqlv2.commonUtils.row

import com.trinitcore.sqlv2.commonUtils.Defaults
import com.trinitcore.sqlv2.commonUtils.QMap
import com.trinitcore.sqlv2.queryObjects.Table
import com.trinitcore.sqlv2.queryUtils.parameters.Where
import org.json.simple.JSONObject

/**
 * Created by Cormac on 17/08/2017.
 */
open class Row(public val parentTable: Table, public val parentRows: Rows? = null) : HashMap<String, Any?>(), RowType {

    public fun hasAssociatingValues(associationName: String, associatingColumn: String, associatingValue: Any) : Boolean {
        return get(associationName)?.let {
            if (it is Row) {
                return (it[associatingColumn] == associatingValue)
            } else if (it is Rows) {
                it.values
                        .filter { it is Row && it[associatingColumn] == associatingValue }
                        .forEach { return true }
            }
            return false
        } ?: false
    }

    fun toJSONObject(): JSONObject {
        val jsonObject = JSONObject()
        map { entry ->
            when {
                entry.value is Rows -> jsonObject.put(entry.key, (entry.value as Rows).toJSONArray())
                entry.value is Row -> jsonObject.put(entry.key, (entry.value as Row).toJSONObject())
                else -> jsonObject.put(entry.key, entry.value)
            }
        }

        return jsonObject
    }

    private fun getUniqueWhere() : Where {
        return Where().andEqualValues(QMap(Defaults.indexColumnKey, get(Defaults.indexColumnKey)!!))
    }

    fun update(vararg values:QMap) {
        this.parentTable.updateValues(getUniqueWhere(), values)?.let {
            it.indexAsRow(0).forEach { t, u ->
                put(t, u)
            }
        }
        refreshParentRows()
    }

    public fun delete() {
        this.parentTable.delete(getUniqueWhere())
        refreshParentRows(true)
    }

    private fun refreshParentRows(delete: Boolean = false) {
        parentRows?.dispatchedUpdates!!.add({
            it.remove(get(it.indexColumnKey), this)
            if (!delete) it.put(get(it.indexColumnKey)!!, this)
        })
    }

    fun getID() : Any? {
        return get(parentTable.indexColumnKey)
    }

}