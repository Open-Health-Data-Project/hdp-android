package org.openhdp.hdt.ui.tracking

import org.openhdp.hdt.data.StopwatchRepository
import org.openhdp.hdt.data.entities.Category
import org.openhdp.hdt.data.entities.Stopwatch
import org.openhdp.hdt.ui.settings.StartOfDay
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class TrackingItemsMapper @Inject constructor(
    private val stopwatchRepository: StopwatchRepository,
    private val startOfDayCalculator: StartOfDayTimeCalculator
) {


    private suspend fun Stopwatch.asTrackingItem(
        categories: List<Category>,
        startOfDay: StartOfDay
    ): TrackingItem? {
        try {
            val stopWatchId = this.id
            val category = categories.firstOrNull { this.categoryId == it.id } ?: return null

            val now = Date()
            val currentTimeInMillis = now.time

            val startOfDayDate = Date(now.time)
            startOfDayDate.hours = startOfDay.hours
            startOfDayDate.minutes = startOfDay.minutes

            var totalTimeInMillis = 0L
            var stopWatchRunning = false

            val timestamps = stopwatchRepository.timestamps(stopWatchId)
            val millisAfterReset = startOfDayCalculator.calculate(startOfDay, timestamps, now)

            timestamps.forEachIndexed { index, timestamp ->
                val stopTime = timestamp.stopTime
                val startTime = timestamp.startTime

                if (stopTime != null) {
                    //timer was paused before
                    totalTimeInMillis += kotlin.math.abs(stopTime - startTime)
                } else {
//                    val timeoutInMillis = TimeUnit.HOURS.toMillis(16L)
//                    val lastActivityDurationTillNow = kotlin.math.abs(currentTime - it.startTime)
                    totalTimeInMillis += kotlin.math.abs(currentTimeInMillis - startTime)
                    if (timestamps.lastIndex == index) {
                        stopWatchRunning = true
                    } //make it still running
                }
            }

            return TrackingItem(
                id,
                name,
                category.name,
                millisAfterReset,
                buttonState = PlaybackButtonState(if (stopWatchRunning) TrackState.ACTIVE else TrackState.INACTIVE),
                startOfDay,
                category.color
            )
        } catch (throwable: Throwable) {
            Timber.e(throwable, "failed to map to tracking item")
            println("failed to map to tracking item $throwable")
            throwable.printStackTrace()
            return null
        }
    }

    suspend fun toTrackingItems(
        stopwatches: List<Stopwatch>,
        categories: List<Category>,
        startOfDay: StartOfDay
    ) = stopwatches.mapNotNull { it.asTrackingItem(categories, startOfDay) }

}