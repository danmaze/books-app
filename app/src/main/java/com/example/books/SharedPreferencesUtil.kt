package com.example.books

import android.content.Context
import android.content.SharedPreferences
import java.util.*

object SharedPreferencesUtil {
    private const val PREF_NAME = "BooksPreferences"
    const val POSITION = "position"
    const val QUERY = "query"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun getPreferenceString(context: Context, key: String): String {
        return getPrefs(context).getString(key, "")
    }

    fun getPreferenceInt(context: Context, key: String): Int {
        return getPrefs(context).getInt(key, 0)
    }

    fun setPreferenceString(context: Context, key: String, value: String) {
        val editor = getPrefs(context).edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun setPreferenceInt(context: Context, key: String, value: Int) {
        val editor = getPrefs(context).edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun getQueryList(context: Context): ArrayList<String> {
        val queryList = ArrayList<String>()
        for (i in 1..5) {
            var query = getPrefs(context).getString(QUERY + i.toString(), "")
            if (!query!!.isEmpty()) {
                query = query.replace(",", " ")
                queryList.add(query.trim { it <= ' ' })
            }
        }
        return queryList
    }


}
