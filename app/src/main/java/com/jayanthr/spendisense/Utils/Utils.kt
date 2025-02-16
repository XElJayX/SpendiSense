package com.jayanthr.spendisense.Utils


import android.util.Log
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Utils {

    fun formatDateToHumanReadableFormat(dateInMillis: Long): String{
        val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormatter.format(dateInMillis)
    }

    fun formatToDecimal(number: Double): String{
        return String.format("%.2f",number)
    }
    fun formatDateForChart(dateInMillis: Long): String {
        val dateFormatter = SimpleDateFormat("dd-MMM", Locale.getDefault())
        return dateFormatter.format(dateInMillis)
    }

    fun formatCurrency(amount: Double, locale: Locale = Locale.US): String {
        val currencyFormatter = NumberFormat.getCurrencyInstance(locale)
        return currencyFormatter.format(amount)
    }

    fun formatDayMonthYear(dateInMillis: Long): String {
        val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return dateFormatter.format(dateInMillis)
    }

    fun formatDayMonth(dateInMillis: Long): String {
        val dateFormatter = SimpleDateFormat("dd/MMM", Locale.getDefault())
        return dateFormatter.format(dateInMillis)
    }

    fun formatToDecimalValue(d: Double): String {
        return String.format("%.2f", d)
    }

    fun formatStringDateToMonthDayYear(date: String): String {
        val millis = getMillisFromDate(date)
        return formatDayMonthYear(millis)
    }

    fun getMillisFromDate(date: String): Long {
        return getMilliFromDate(date)
    }

    fun getMilliFromDate(dateFormat: String?): Long {
        var date = Date()
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        try {
            date = formatter.parse(dateFormat)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        println("Today is $date")
        return date.time
    }

    fun parseSMS(message: String): Map<String, String?>{

        val amountRegex = "(\\d{1,3})(,\\d{2,3})*(\\.\\d{2})".toRegex()
        val creditReceiverRegex = "from (.+?)\\.".toRegex()
        val debitReceiverRegex = "to (.+?)\\.|at (.+?)\\.".toRegex()
        val dateRegex = "(\\d{2}-.{3}-\\d{4})|(\\d{2}-\\d{2}-\\d{2})".toRegex()

        var amount = amountRegex.find(message)?.value?.replace(",","")
        var smsDate = dateRegex.find(message)?.value

        var transactionType: String
        var receiver: String?

        when {
            "credited" in message -> {
                transactionType = "Income"
                receiver = creditReceiverRegex.find(message)?.value
            }
            "Balance" in message -> {
                transactionType = "Balance"
                receiver = "Balance"
            }
            else -> {
                transactionType = "Expense"
                receiver = debitReceiverRegex.find(message)?.value
            }
        }
        Log.d("SMSReceiver","Amount: $amount Date: $smsDate Type $transactionType Receiver: $receiver")
        return mapOf("amount" to amount,
            "date" to smsDate,
            "type" to transactionType,
            "receiver" to receiver)
    }

    val bankHeaders = hashSetOf(
        // Public Sector Banks
        "SBIINB", "SBIOTP", "SBIBNK",
        "BARODA", "BOBTXN",
        "PNBSMS", "PNBTXN",
        "CANBNK", "CANTXN",
        "UBINOT", "UBINBX",
        "INDBNK", "INDBIX",
        "CBIOTP", "CBITXN",
        "BKIDTX", "BOIMSG",
        "UCOBNK", "UCOTXN",
        "IOBNOT", "IOBTXN",

        // Private Sector Banks
        "HDFCBK", "HDFCTX",
        "ICICIB", "ICICIN",
        "AXISBK", "AXISNB",
        "KOTAKB", "KOTAKN",
        "INDUSB", "INDUSN",
        "YESBNK", "YESTXN",
        "IDFCBK", "IDFCNB",
        "FEDBNK", "FEDTXN",
        "RBLBNK", "RBLTXT",
        "SIBTXT", "SIBMSG",

        // Regional Rural Banks & Others
        "JKBANK",
        "BANDHN",
        "KVBBNK",
        "TMBBNK",
        "DHLBNK",
        "ESAFBNK",

        // Payment Platforms
        "PYTMNB", "PAYTM",
        "AIRBNK",
        "FINOBN"
    )
}