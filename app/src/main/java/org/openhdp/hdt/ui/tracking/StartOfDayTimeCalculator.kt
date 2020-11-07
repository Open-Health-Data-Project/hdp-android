package org.openhdp.hdt.ui.tracking

import org.openhdp.hdt.data.entities.Timestamp
import org.openhdp.hdt.ui.settings.StartOfDay
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.abs

class StartOfDayTimeCalculator @Inject constructor() {

    fun calculate(startOfDay: StartOfDay, timestamps: List<Timestamp>, now: Date = Date()): Long {
        val closestStartOfDay = Date(now.time).apply {
            hours = startOfDay.hours
            minutes = startOfDay.minutes
        }

        var timeSoFar = 0L

        timestamps.forEach { timestamp ->
            val startTime = timestamp.startTime
            val startTimeDate = Date(startTime)
            val stopTime = timestamp.stopTime
            if (stopTime != null) {
                val diff = stopTime - startTime
                val stopTimeDate = Date(stopTime)

                if (closestStartOfDay.before(startTimeDate)) {
                    timeSoFar += diff
                }
                if (startTimeDate.before(closestStartOfDay) && closestStartOfDay.before(stopTimeDate)) {
                    timeSoFar += abs(closestStartOfDay.time - stopTimeDate.time)
                }
            } else {
                val previousStartOfDay = Date(closestStartOfDay.time - TimeUnit.DAYS.toMillis(1))
                if (previousStartOfDay.before(startTimeDate)) {
                    if (now.before(closestStartOfDay)) {
                        timeSoFar += abs(now.time - startTime)
                    } else {
                        //now after closestStartOfDay
                        if (closestStartOfDay.before(startTimeDate)) {
                            timeSoFar += abs(now.time - startTimeDate.time)
                        } else {
                            timeSoFar += abs(now.time - closestStartOfDay.time)
                        }
                    }
                }
            }
        }
        return timeSoFar
    }
}
