package com.trinitcore.sqlv2.queryUtils.associations

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Cormac on 24/10/2017.
 */

public class DateReformatAssociation(newColumnTitle: String, format:String) : ReformatAssociation(newColumnTitle, { row ->
    val simpleFormat = SimpleDateFormat(format)
    simpleFormat.format(Date(row["dateTime"] as Long))
}) {

}