package com.jayanthr.spendisense.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jayanthr.spendisense.data.ExpenseDataBase
import com.jayanthr.spendisense.data.dao.ExpenseDao
import com.jayanthr.spendisense.data.model.ExpenseEntity


class HomeViewModel(dao: ExpenseDao) : ViewModel(){
    val expenses = dao.getAllExpenses()

    fun getBalance(List: List<ExpenseEntity>) : String{

        var total= 0.0
        List.forEach {
            if(it.type == "Income"){
                total+= it.amount
            }
            else{
                total -= it.amount
            }
        }


        return "₹ ${total}"
    }

    fun getTotalExpense(List: List<ExpenseEntity>) : String {
        var total = 0.0
        List.forEach {
            if (it.type == "Expense") {
                total += it.amount
            }
        }
        return "₹ ${total}"
    }

    fun getTotalIncome(List: List<ExpenseEntity>) : String{
        var total= 0.0
        List.forEach {
            if(it.type == "Income"){
                total+= it.amount
            }
        }
        return "₹ ${total}"
    }
}


class HomeViewModelFactory(private val context: Context) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(HomeViewModel::class.java)){
            val dao = ExpenseDataBase.getDatabase(context).expenseDao()
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(dao) as T
        }
   throw IllegalArgumentException("UNKNOWN VIEWMODEL CLASS")
    }
}