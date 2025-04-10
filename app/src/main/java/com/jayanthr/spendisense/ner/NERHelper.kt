package com.jayanthr.spendisense.ner

import android.content.Context
import org.json.JSONObject
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.Tensor
import java.io.*

class NERHelper(context: Context) {
    private val model: Module = Module.load(assetFilePath(context, "v07042.pt"))
    private val wordToIdx: Map<String, Int> = loadJsonMap(context, "word_to_ix.json")
    private val idxToTag: Map<Int, String> = loadJsonMap(context, "ix_to_tag.json")
        .entries.associate { (tag, idx) -> idx.toInt() to tag }

    fun predict(sentence: List<String>): List<Pair<String, String>> {
        // Convert tokens to indices
        val inputIds = sentence.map { (wordToIdx[it] ?: 0).toLong() }.toLongArray()
        val inputTensor = Tensor.fromBlob(inputIds, longArrayOf(1, inputIds.size.toLong()))

        // Create attention mask (all 1s for this implementation)
        val maskData = FloatArray(inputIds.size) { 1.0f }
        val maskTensor = Tensor.fromBlob(maskData, longArrayOf(1, inputIds.size.toLong()))

        // Step 1: Get emissions from model
        val emissionsOutput = model.forward(IValue.from(inputTensor)).toTensor()

        // Step 2: Run viterbi_decode with the emissions tensor and mask
        val output = model.runMethod("viterbi_decode", IValue.from(emissionsOutput), IValue.from(maskTensor))

        // Handle the output - it's a List<List<int>> with one element per batch
        val batchOutput = output.toList()
        val predictedTags = batchOutput[0].toLongList()

        // Zip tokens with predicted tags
        return sentence.zip(predictedTags.toList()) { word, tagIdx ->
            word to (idxToTag[tagIdx.toInt()] ?: "O")
        }
    }

    private fun assetFilePath(context: Context, assetName: String): String {
        val file = File(context.filesDir, assetName)
        if (file.exists() && file.length() > 0) return file.absolutePath

        context.assets.open(assetName).use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
        return file.absolutePath
    }

    private fun loadJsonMap(context: Context, filename: String): Map<String, Int> {
        val jsonString = context.assets.open(filename).bufferedReader().use { it.readText() }
        val jsonObject = JSONObject(jsonString)
        val map = mutableMapOf<String, Int>()
        val keys = jsonObject.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            map[key] = jsonObject.getInt(key)
        }
        return map
    }
}