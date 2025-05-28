package dev.yahaveliyahu.hw1

import android.content.Context
import com.google.gson.reflect.TypeToken
import com.google.gson.Gson

object MyHighScores {

    private const val PREFS_NAME = "high_scores_prefs"
    private const val SCORES_KEY = "high_scores"
    private const val MAX_SIZE = 10

    fun getScores(context: Context): MutableList<HighScore> {
        // Retrieve the preferences object for reuse in the application (SharedPreferences)
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        // Try to read the string stored in SharedPreferences by key, or return an empty list if there is none
        val json = prefs.getString(SCORES_KEY, null) ?: return mutableListOf()
        // Prepare a Generic type for Gson to convert from a JSON string to a HighScore list
        val type = object : TypeToken<MutableList<HighScore>>() {}.type
        // Convert the JSON to a HighScore list using Gson
        return Gson().fromJson(json, type)
    }

    fun saveScore(context: Context, highScore: HighScore) {
        val scores = getScores(context).toMutableList()

        // Add the new record and sort
        scores.add(highScore)
        scores.sortByDescending { it.score }

        // If more than 10 – remove the last one
        if (scores.size > MAX_SIZE)
            scores.removeAt(scores.size - 1)

        // Resaving
        val json = Gson().toJson(scores)
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putString(SCORES_KEY, json).apply()
    }

    fun isNewHighScore(context: Context, score: Int): Boolean {
        val scores = getScores(context)
        return scores.size < MAX_SIZE || score > scores.minByOrNull { it.score }!!.score
    }
}
