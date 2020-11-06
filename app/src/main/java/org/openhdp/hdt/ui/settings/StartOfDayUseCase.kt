package org.openhdp.hdt.ui.settings

import android.app.Application
import android.content.Context
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class StartOfDayUseCase @Inject constructor(private val application: Application) {

    companion object {
        const val KEY_START_OF_DAY = "KEY_START_OF_DAY"
        const val GLOBAL = "GLOBAL"
    }

    private val prefs = application.getSharedPreferences(GLOBAL, Context.MODE_PRIVATE)

    /**
     * total minutes from midnight which represents hour and minute
     */
    fun getCurrentStartOfDay(): StartOfDay {
        val minutes = prefs.getInt(KEY_START_OF_DAY, 0)
        val hours = minutes / 60
        return StartOfDay(hours, minutes % 60)
    }

    fun saveStartOfDay(startOfDay: StartOfDay) {
        val (hours, minutes) = startOfDay
        val hoursInSeconds = hours * 60
        prefs.edit()
            .putInt(KEY_START_OF_DAY, hoursInSeconds + minutes % 60)
            .commit()
    }
}

data class StartOfDay(val hours: Int, val minutes: Int)