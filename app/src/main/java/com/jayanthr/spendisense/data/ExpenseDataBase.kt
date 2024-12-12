package com.jayanthr.spendisense.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.jayanthr.spendisense.data.dao.ExpenseDao
import com.jayanthr.spendisense.data.model.ExpenseEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [ExpenseEntity::class], version = 1)
abstract class ExpenseDataBase : RoomDatabase(){

    abstract fun expenseDao(): ExpenseDao


    companion object{
        const val DATABASE_NAME = "expense_database"


        @JvmStatic
        fun getDatabase(context: Context): ExpenseDataBase{
            return Room.databaseBuilder(
                context,
                ExpenseDataBase::class.java,
                DATABASE_NAME
            ).addCallback(object : RoomDatabase.Callback(){
                override fun onCreate(db: SupportSQLiteDatabase){
                    super.onCreate(db)
                    initBasicData((context))}

                fun initBasicData(context: Context){
                    CoroutineScope(Dispatchers.IO).launch{
                        val dao = getDatabase(context).expenseDao()
                        dao.insertExpense(ExpenseEntity(1,"Salary",5000.0,System.currentTimeMillis().toString(), "Salary", "Income" ))
                        dao.insertExpense(ExpenseEntity(2,"Food",160.0,System.currentTimeMillis().toString(), "Food", "Expense"))
                        dao.insertExpense(ExpenseEntity(3,"Petrol",1500.0,System.currentTimeMillis().toString(),"Petrol", "Expense"))
                        dao.insertExpense(ExpenseEntity(4,"Netflix",299.0,System.currentTimeMillis().toString(),"Subscription", "Expense"))
                    }
                }
            }).build()
        }
    }
}