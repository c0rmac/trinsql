package com.trinitcore.v2.commonUtils.row

import com.trinitcore.v2.queryObjects.Table
import org.json.simple.JSONObject

/**
 * Created by Cormac on 17/08/2017.
 */
class Row(public val parentTable: Table) : HashMap<String, Any>(), RowType {

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

    public fun update() {
        //this.parentTable.update()
    }

    public fun delete() {
        //this.parentTable.delete()
    }

}