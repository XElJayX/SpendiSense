package com.jayanthr.spendisense.feature.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import android.widget.Toast
import com.jayanthr.spendisense.Utils.Utils.bankHeaders
import com.jayanthr.spendisense.Utils.Utils.parseSMS
import com.jayanthr.spendisense.data.ExpenseDataBase
import com.jayanthr.spendisense.data.model.ExpenseEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        // Check if the received intent is for an SMS
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            val sender = messages[0].displayOriginatingAddress.toString().substring(3,)
            val content = parseSMS(messages[0].displayMessageBody)

            //SANITY CHECK
            if (bankHeaders.contains(sender)){
                Log.d("SmsReceiver", "SENDER SUCESSS CHECK")
            val model = ExpenseEntity(
                null,
                title = content["receiver"].toString(),
                amount = content["amount"]?.toDouble() ?: 0.0,
                date = content["date"].toString(),
                category = "food",
                type = content["type"].toString(),
            )
            addTransaction(context,model)
            }
            else{
                Log.d("SmsReceiver", "SENDER FAILED CHECK")
            }

            Log.d("SmsReceiver", "SMS received from $sender: $content")



        }
    }

    fun addTransaction(context: Context, model: ExpenseEntity){
        val dao = ExpenseDataBase.getDatabase(context).expenseDao()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                    dao.insertExpense(model)
                    Log.d("SmsReceiver", "TRANSACTION ADDED")
                    Toast.makeText(context, "TRANSACTION ADDED", Toast.LENGTH_SHORT).show()


            } catch (ex: Exception) {

                ex.printStackTrace()

            }
        }
    }
}

