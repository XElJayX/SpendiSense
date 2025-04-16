package com.jayanthr.spendisense.feature.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import android.widget.Toast
import com.jayanthr.spendisense.Classifier.TransactionClassifier
import com.jayanthr.spendisense.Utils.Utils.bankHeaders
import com.jayanthr.spendisense.Utils.Utils.parseSMS
import com.jayanthr.spendisense.data.ExpenseDataBase
import com.jayanthr.spendisense.data.model.ExpenseEntity
import com.jayanthr.spendisense.ner.NERHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SmsReceiver : BroadcastReceiver() {
    private lateinit var nerHelper: NERHelper
    private lateinit var classifier: TransactionClassifier


    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            val sender = messages[0].displayOriginatingAddress?.substring(3) ?: ""
            val messageBody = messages[0].displayMessageBody ?: ""

            // Init NER & Classifier
            nerHelper = NERHelper(context)
            classifier = TransactionClassifier(context)

            // Preprocess SMS
            val tokens = messageBody.split("\\s+".toRegex()).filter { it.isNotBlank() }

            // NER prediction
            val nerTags = try {
                nerHelper.predict(tokens)
            } catch (e: Exception) {
                Log.e("SmsReceiver/NER", "NER processing failed", e)
                emptyList()
            }

            Log.d("SmsReceiver/NER", "Raw NER Output:")
            nerTags.forEach { (word, tag) ->
                Log.d("SmsReceiver/NER", "$word -> $tag")
            }

            val nerEntities = extractEntities(nerTags)
            Log.d("SmsReceiver/NER", "Extracted Entities: $nerEntities")

            // Parse fallback values
            val parsedContent = parseSMS(messageBody)

            // Fallback-safe values
            val merchant = nerEntities["merchant_name"] ?: parsedContent["receiver"].toString()
            val amount = nerEntities["transaction_amount"]?.toDoubleOrNull()
                ?: parsedContent["amount"]?.toDouble() ?: 0.0
            val date = nerEntities["transaction_datetime"] ?: parsedContent["date"].toString()


            // 🔥 Predict category using classifier
            val category = try {
                classifier.classifyTransaction(
                    merchantName = merchant,
                    transactionAmount = amount.toFloat(),
                    timestamp = System.currentTimeMillis()
                )
            } catch (e: Exception) {
                Log.e("SmsReceiver/Classifier", "Prediction failed", e)
                "uncategorized"
            }

            // Log category
            Log.d("SmsReceiver/Classifier", "Predicted Category: $category")

            val model = ExpenseEntity(
                id = null,
                title = merchant,
                amount = amount,
                date = date,
                category = category,
                type = "Expense"
            )

            if (!bankHeaders.contains(sender)) {
                Log.d("SmsReceiver", "Processing non-bank SMS")
                addTransaction(context, model)
            } else {
                Log.d("SmsReceiver", "Ignoring bank SMS from $sender")
            }
        }
    }


    private fun extractEntities(tags: List<Pair<String, String>>): Map<String, String> {
        val entities = mutableMapOf<String, String>()
        val currentEntity = StringBuilder()
        var currentEntityType: String? = null

        // Improved entity extraction that handles multi-word entities
        for ((word, tag) in tags) {
            when {
                tag.startsWith("B-") -> {
                    // If we were building an entity, save it first
                    if (currentEntityType != null && currentEntity.isNotEmpty()) {
                        entities[currentEntityType!!] = currentEntity.toString().trim()
                    }
                    // Start new entity
                    currentEntityType = tag.substring(2).lowercase()
                    currentEntity.clear()
                    currentEntity.append(word)
                }
                tag.startsWith("I-") -> {
                    // Continue building current entity if it matches
                    val entityType = tag.substring(2).lowercase()
                    if (entityType == currentEntityType) {
                        currentEntity.append(" ").append(word)
                    }
                }
                else -> {
                    // Save current entity if any
                    if (currentEntityType != null && currentEntity.isNotEmpty()) {
                        entities[currentEntityType!!] = currentEntity.toString().trim()
                        currentEntityType = null
                        currentEntity.clear()
                    }
                }
            }
        }

        // Save any remaining entity
        if (currentEntityType != null && currentEntity.isNotEmpty()) {
            entities[currentEntityType!!] = currentEntity.toString().trim()
        }

        return entities
    }

    private fun addTransaction(context: Context, model: ExpenseEntity) {
        val dao = ExpenseDataBase.getDatabase(context).expenseDao()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                dao.insertExpense(model)
                Log.d("SmsReceiver", "Transaction added successfully")
                Toast.makeText(context, "Transaction recorded", Toast.LENGTH_SHORT).show()
            } catch (ex: Exception) {
                Log.e("SmsReceiver", "Error saving transaction", ex)
            }
        }
    }
}