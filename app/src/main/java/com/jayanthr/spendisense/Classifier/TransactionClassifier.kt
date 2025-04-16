package com.jayanthr.spendisense.Classifier

import android.content.Context
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.Tensor
import java.io.File
import java.io.FileOutputStream
import java.util.*
import org.json.JSONObject
import java.nio.charset.Charset


class TransactionClassifier(context: Context) {
    private lateinit var model: Module
    private lateinit var tfidfVocab: Map<String, Int>
    private lateinit var tfidfIdf: FloatArray
    private lateinit var scalerMeans: FloatArray
    private lateinit var scalerScales: FloatArray
    private lateinit var categories: Array<String>

    init {
        loadModelAndParams(context)
    }

    private fun loadModelAndParams(context: Context) {
        // Load PyTorch model
        model = Module.load(assetFilePath(context, "transaction_classifier_scripted_final.pt"))

        // Load TF-IDF and Scaler parameters
        val tfidfData = loadTfidfData(context, "tfidf_vocab.json")
        tfidfVocab = tfidfData.first
        tfidfIdf = tfidfData.second

        val scalerParams = loadScalerParams(context, "scaler_params.json")
        scalerMeans = scalerParams.first
        scalerScales = scalerParams.second

        categories = loadLabelEncoder(context, "label_encoder_classes.json")
    }

    private fun assetFilePath(context: Context, assetName: String): String {
        val file = File(context.filesDir, assetName)
        if (file.exists() && file.length() > 0) return file.absolutePath

        context.assets.open(assetName).use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                val buffer = ByteArray(4 * 1024)
                var read: Int
                while (inputStream.read(buffer).also { read = it } != -1) {
                    outputStream.write(buffer, 0, read)
                }
                outputStream.flush()
            }
        }
        return file.absolutePath
    }

    private fun loadTfidfData(context: Context, fileName: String): Pair<Map<String, Int>, FloatArray> {
        val jsonString = context.assets.open(fileName).bufferedReader(Charset.defaultCharset()).use { it.readText() }
        val json = JSONObject(jsonString)
        val vocabJson = json.getJSONObject("vocab")
        val idfJson = json.getJSONArray("idf")

        val vocab = mutableMapOf<String, Int>()
        vocabJson.keys().forEach {
            vocab[it] = vocabJson.getInt(it)
        }

        val idf = FloatArray(idfJson.length()) { i -> idfJson.getDouble(i).toFloat() }

        return Pair(vocab, idf)
    }

    private fun loadScalerParams(context: Context, fileName: String): Pair<FloatArray, FloatArray> {
        val jsonString = context.assets.open(fileName).bufferedReader(Charset.defaultCharset()).use { it.readText() }
        val json = JSONObject(jsonString)
        val meansJson = json.getJSONArray("mean")
        val scalesJson = json.getJSONArray("scale")

        val means = FloatArray(meansJson.length()) { i -> meansJson.getDouble(i).toFloat() }
        val scales = FloatArray(scalesJson.length()) { i -> scalesJson.getDouble(i).toFloat() }

        return Pair(means, scales)
    }

    private fun loadLabelEncoder(context: Context, fileName: String): Array<String> {
        val jsonString = context.assets.open(fileName).bufferedReader(Charset.defaultCharset()).use { it.readText() }
        val jsonArray = JSONObject("{\"labels\":$jsonString}").getJSONArray("labels")
        return Array(jsonArray.length()) { i -> jsonArray.getString(i) }
    }
    private fun preprocessInput(merchantName: String, transactionAmount: Float, dayOfWeek: String, month: Int, quarter: Int, isWeekend: Int): FloatArray {
        val tfidfVector = computeTfidfVector(merchantName)
        val dayOfWeekNumeric = dayOfWeekToNumeric(dayOfWeek)
        val numericFeatures = floatArrayOf(transactionAmount, dayOfWeekNumeric, month.toFloat(), quarter.toFloat(), isWeekend.toFloat())
        val scaledNumeric = scaleFeatures(numericFeatures)
        return tfidfVector + scaledNumeric
    }

    private fun computeTfidfVector(text: String): FloatArray {
        val tokens = text.lowercase(Locale.getDefault())
            .split("\\s+|[-.,/]".toRegex())
            .filter { it.isNotEmpty() }

        val termFreq = mutableMapOf<String, Int>()
        for (token in tokens) {
            termFreq[token] = termFreq.getOrDefault(token, 0) + 1
        }

        val vector = FloatArray(tfidfVocab.size) { 0f }
        for ((token, count) in termFreq) {
            val index = tfidfVocab[token]
            if (index != null && index < tfidfIdf.size) {
                vector[index] = count * tfidfIdf[index]
            }
        }
        return vector
    }

    private fun dayOfWeekToNumeric(dayOfWeek: String): Float {
        return when (dayOfWeek.lowercase(Locale.getDefault())) {
            "monday" -> 0.0f
            "tuesday" -> 1.0f
            "wednesday" -> 2.0f
            "thursday" -> 3.0f
            "friday" -> 4.0f
            "saturday" -> 5.0f
            "sunday" -> 6.0f
            else -> 0.0f
        }
    }

    private fun scaleFeatures(features: FloatArray): FloatArray {
        return FloatArray(features.size) { i -> (features[i] - scalerMeans[i]) / scalerScales[i] }
    }

    fun classifyTransaction(merchantName: String, transactionAmount: Float, timestamp: Long): String {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timestamp
        }

        val dayOfWeekInt = calendar.get(Calendar.DAY_OF_WEEK)
        val month = calendar.get(Calendar.MONTH) + 1 // Calendar.MONTH is 0-based
        val isWeekend = if (dayOfWeekInt == Calendar.SATURDAY || dayOfWeekInt == Calendar.SUNDAY) 1 else 0
        val quarter = when (month) {
            in 1..3 -> 1
            in 4..6 -> 2
            in 7..9 -> 3
            else -> 4
        }

        val dayOfWeek = when (dayOfWeekInt) {
            Calendar.SUNDAY -> "sunday"
            Calendar.MONDAY -> "monday"
            Calendar.TUESDAY -> "tuesday"
            Calendar.WEDNESDAY -> "wednesday"
            Calendar.THURSDAY -> "thursday"
            Calendar.FRIDAY -> "friday"
            Calendar.SATURDAY -> "saturday"
            else -> "monday"
        }

        val preprocessedFeatures = preprocessInput(
            merchantName = merchantName,
            transactionAmount = transactionAmount,
            dayOfWeek = dayOfWeek,
            month = month,
            quarter = quarter,
            isWeekend = isWeekend
        )

        val inputTensor = Tensor.fromBlob(preprocessedFeatures, longArrayOf(1, preprocessedFeatures.size.toLong()))
        val outputTensor = model.forward(IValue.from(inputTensor)).toTensor()
        val predictionScores = outputTensor.dataAsFloatArray

        val predictedClass = predictionScores.indices.maxByOrNull { predictionScores[it] } ?: -1
        return categories.getOrNull(predictedClass) ?: "Unknown"
    }

}
