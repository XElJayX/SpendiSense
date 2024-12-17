package com.jayanthr.spendisense.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jayanthr.spendisense.data.ExpenseDataBase
import com.jayanthr.spendisense.data.dao.ExpenseDao
import com.jayanthr.spendisense.data.model.ExpenseEntity

// ViewModel for holding SMS details
class SmsViewModel(val dao: ExpenseDao) : ViewModel() {

    suspend fun addExpenseSMS(expenseEntity: ExpenseEntity): Boolean {
        return try {
            dao.insertExpense(expenseEntity)
            true
        } catch (ex: Throwable) {
            false
        }
    }

    class SmsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SmsViewModel::class.java)) {
                val dao: ExpenseDao = ExpenseDataBase.getDatabase(context).expenseDao()
                @Suppress("UNCHECKED_CAST")
                return SmsViewModel(dao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }



}
