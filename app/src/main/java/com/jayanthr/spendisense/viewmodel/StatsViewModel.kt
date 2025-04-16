package com.jayanthr.spendisense.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.data.Entry
import com.jayanthr.spendisense.Utils.Utils
import com.jayanthr.spendisense.data.ExpenseDataBase
import com.jayanthr.spendisense.data.dao.ExpenseDao
import com.jayanthr.spendisense.data.model.ExpenseSummary
import java.text.SimpleDateFormat
import java.util.*

class StatsViewModel(dao: ExpenseDao) : ViewModel(){
    val entries = dao.getAllExpensesByData()

    fun getEntriesForChart(entries: List<ExpenseSummary>): List<Entry>{
        val list = mutableListOf<Entry>()
        for(entry in entries){
            val formattedDate = Utils.getMilliFromDate(entry.date)
            list.add(Entry(formattedDate.toFloat(), entry.total_amount.toFloat()))
        }
        return list
    }

    fun getTotalAmount(entries: List<ExpenseSummary>): String {
        return if (entries.isEmpty()) {
            "0"
        } else {
            // Explicitly convert to double for sum calculation
            val total = entries.sumByDouble { it.total_amount.toDouble() }
            total.toInt().toString()
        }
    }

    fun getAveragePerDay(entries: List<ExpenseSummary>): String {
        return if (entries.isEmpty()) {
            "0"
        } else {
            // Group by date to handle multiple entries on the same day
            val dailyTotals = entries.groupBy { it.date }
            val totalAmount = entries.sumByDouble { it.total_amount.toDouble() }
            val daysCount = dailyTotals.size

            val average = if (daysCount > 0) totalAmount / daysCount else 0.0
            average.toInt().toString()
        }
    }

    fun getMostExpensiveDay(entries: List<ExpenseSummary>): String {
        if (entries.isEmpty()) {
            return "None"
        }

        // Group entries by date and sum amounts
        val dailyTotals = entries.groupBy { it.date }
            .mapValues { (_, dateEntries) -> dateEntries.sumByDouble { it.total_amount.toDouble() } }

        // Find date with max spending
        val maxDay = dailyTotals.entries.maxByOrNull { it.value }?.key ?: return "None"

        // Format the date for display
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = inputFormat.parse(maxDay)
            val outputFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            maxDay
        }
    }

    fun getCategoryBreakdown(entries: List<ExpenseSummary>): List<Triple<String, Float, Float>> {
        if (entries.isEmpty()) {
            return emptyList()
        }

        // Group entries by category and calculate totals
        val categoryTotals = entries
            .filter { it.type.isNotBlank() }
            .groupBy { it.type }
            .mapValues { (_, categoryEntries) ->
                categoryEntries.sumByDouble { it.total_amount.toDouble() }.toFloat()
            }
            .toList()
            .sortedByDescending { it.second }

        // Calculate total amount manually
        val totalAmount = categoryTotals.sumBy { it.second.toInt() }.toFloat()

        // Create the result with category name, amount and percentage
        return categoryTotals.map { (category, amount) ->
            val percentage = if (totalAmount > 0) (amount / totalAmount * 100f) else 0f
            Triple(category, amount, percentage)
        }
    }
}

class StatsViewModelFactory(private val context: Context) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(StatsViewModel::class.java)){
            val dao = ExpenseDataBase.getDatabase(context).expenseDao()
            @Suppress("UNCHECKED_CAST")
            return StatsViewModel(dao) as T
        }
        throw IllegalArgumentException("UNKNOWN VIEWMODEL CLASS")
    }
}