package com.jayanthr.spendisense.feature.sms


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import android.widget.Toast
import com.jayanthr.spendisense.Utils.Utils.parseSMS
import com.jayanthr.spendisense.data.model.ExpenseEntity


class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        // Check if the received intent is for an SMS
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            val sender = messages[0].displayOriginatingAddress
            val content = parseSMS(messages[0].displayMessageBody)

            val model = ExpenseEntity(
                null,
                title = content["receiver"].toString(),
                amount = content["amount"]?.toDouble() ?: 0.0,
                date = content["date"].toString(),
                category = "food",
                type = content["type"].toString(),
            )

            Log.d("SmsReceiver", "SMS received from $sender: $content")

            Toast.makeText(context, "MESSAGE RECEIVED FROM $sender", Toast.LENGTH_SHORT).show()

        }
    }
}

