package org.openhdp.hdt.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson

class WidgetPreferences(context: Context) {

    private val preferences: SharedPreferences =
        context.getSharedPreferences("widget_preferences", Context.MODE_PRIVATE)

    private val gson = Gson()

    @SuppressLint("ApplySharedPref")
    fun saveWidget(stopwatchId: String, viewState: StopwatchViewState) {
        val editor = preferences.edit()
        editor.putString(stopwatchId, gson.toJson(viewState))
        editor.commit()
    }

    fun hasStopwatch(stopwatchId: String): Boolean {
        return getExistingWidgets().any { it.stopwatchId == stopwatchId }
    }

    fun getWidgetViewState(widgetId: Int): StopwatchViewState? {
        return getExistingWidgets().firstOrNull { it.widgetId == widgetId }
    }

    fun removeWidget(widgetId: Int) {
        getExistingWidgets().firstOrNull {
            it.widgetId == widgetId
        }?.let {
            val editor = preferences.edit()
            editor.remove(it.stopwatchId)
            editor.commit()
        }
    }

    private fun getExistingWidgets(): List<StopwatchViewState> = preferences.all.keys.mapNotNull {
        val stopwatchId = it
        val json = preferences.getString(stopwatchId, null) ?: return@mapNotNull null
        try {
            return@mapNotNull gson.fromJson(json, StopwatchViewState::class.java)
        } catch (t: Throwable) {
            return@mapNotNull null
        }
    }
}
