package com.trinitcore.sqlv2.queryUtils.associationV2.format

import com.trinitcore.sqlv2.commonUtils.row.Row
import java.text.SimpleDateFormat
import java.util.*

class DateFormatAssociation
(newColumnTitle: String, format: String, dateTimeColumn: String = "dateTime") :
        ReformatAssociation(newColumnTitle, { row: Row ->
            val simpleFormat = SimpleDateFormat(format)
            simpleFormat.format(Date(row[dateTimeColumn] as Long))
        }) {
}