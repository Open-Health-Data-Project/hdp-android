package org.openhdp.hdt.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

class WidgetPreferences(context: Context) {
    private val preferences: SharedPreferences =
        context.getSharedPreferences("widget_preferences", Context.MODE_PRIVATE)

    //using commit instead of apply as value is needed straight away
    @SuppressLint("ApplySharedPref")
    fun setWidgetValues(widgetId: Int, name: String?) {
        if (name == null) return
        val editor = preferences.edit()
        editor.putString("" + widgetId, name)
        editor.commit()
    }

    @SuppressLint("ApplySharedPref")
    fun setWidgetValues(widgetId: Int, viewState: StopwatchViewState) {
        val editor = preferences.edit()
        editor.putString(widgetId.toString(), Gson().toJson(viewState))
        editor.commit()
    }


    fun getWidgetViewState(widgetId: Int): StopwatchViewState? {
        val value = preferences.getString(widgetId.toString(), null) ?: return null
        return try {
            Gson().fromJson(value, StopwatchViewState::class.java)
        } catch (t: Throwable) {
            null
        }

    }

    fun getWidgetName(widgetId: Int): String? {
        return preferences.getString("" + widgetId, null)
    }

    fun removeWidget(widgetId: Int) {
        val editor = preferences.edit()
        editor.remove(widgetId.toString())
        editor.apply()
    }
}