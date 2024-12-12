package com.jayanthr.spendisense

import java.text.SimpleDateFormat
import java.util.Locale

object Utils {

    fun formatDateToHumanReadableFormat(dateInMillis: Long): String{
        val dateFormatter = SimpleDateFormat("dd/mm/yyyy", Locale.getDefault())
        return dateFormatter.format(dateInMillis)
    }
}