package org.openhdp.hdt.ui.history

import org.openhdp.hdt.data.StopwatchRepository
import org.openhdp.hdt.data.entities.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ProvideHistoricEntriesUseCase @Inject constructor(
    private val stopwatchRepository: StopwatchRepository
) {

    suspend fun execute(stopwatchId: String): List<DayHeader> {
        val result = arrayListOf<DayHeader>()
        val totalTimestamps = stopwatchRepository.timestamps(stopwatchId)
        val timestamps = totalTimestamps.filter { it.stopTime != null }
            .sortedBy { it.startTime }

        if (timestamps.isEmpty()) {
            return result
        }

        val firstDay = timestamps.first().toCalendarDay()
        val lastDay = timestamps.last().toCalendarDay()

        if (firstDay == lastDay) {
            // single date?
            includeDaysFrom(distinctDay = firstDay, timestamps, result)
        } else {
            // handle range of days
            val distinctDays = createDistinctDays(stopwatchId, firstDay, lastDay)
            for (distinctDay in distinctDays) {
                includeDaysFrom(distinctDay = distinctDay, timestamps, result)
            }
        }
        return result
    }

    private fun includeDaysFrom(
        distinctDay: DistinctDay,
        timestamps: List<Timestamp>,
        result: ArrayList<DayHeader>
    ) {
        result.add(
            DayHeader(
                false,
                DAY_FORMAT.format(distinctDay.asDate()),
                distinctDay,
                timestamps.filter { distinctDay.matches(Date(it.startTime)) }.map {
                    val startDate = Date(it.startTime)
                    val endDate = Date(it.stopTime!!)
                    val label = String.format(
                        "From %02d:%02d to %02d:%02d",
                        startDate.hours,
                        startDate.minutes,
                        endDate.hours,
                        endDate.minutes
                    )
                    TimestampEntry(label, it)
                }
            )
        )

    }

    suspend fun createDistinctDays(
        id: String,
        firstDay: DistinctDay,
        lastDay: DistinctDay
    ): List<DistinctDay> {
        val list = arrayListOf(firstDay)
        var temporaryDay = firstDay.incrementDay()
        while (temporaryDay != lastDay) {
            val truncatedFirstDate =
                Date(temporaryDay.year, temporaryDay.month, temporaryDay.dayOfMonth, 0, 0, 0)
            val endOfDayDate =
                Date(temporaryDay.year, temporaryDay.month, temporaryDay.dayOfMonth, 23, 59, 59)
            val timestampsFromThisDay = stopwatchRepository.timestampsFromRange(
                id,
                LongRange(truncatedFirstDate.time, endOfDayDate.time)
            )
            if (timestampsFromThisDay.isNotEmpty()) {
                list.add(temporaryDay)
            }
            temporaryDay = temporaryDay.incrementDay()
        }
        list.add(lastDay)
        return list
    }

}

val DAY_FORMAT = SimpleDateFormat("d MMMM yyyy", Locale.ENGLISH)


fun Timestamp.toCalendarDay(): DistinctDay {
    return Date(startTime).mapTo { DistinctDay.create(it) }
}

fun <Source, Target> Source.mapTo(mapper: (Source) -> Target): Target = mapper(this)

data class DistinctDay(val year: Int, val month: Int, val dayOfMonth: Int) {

    fun incrementDay(): DistinctDay {
        val date = Date(year, month, dayOfMonth)
        val singleDayInMillis = TimeUnit.DAYS.toMillis(1)
        return create(Date(date.time + singleDayInMillis))
    }

    fun matches(date: Date): Boolean {
        return date.date == dayOfMonth && date.month == month && date.year == year
    }

    fun asDate(): Date = Date(year, month, dayOfMonth)

    companion object {

        fun create(date: Date): DistinctDay {
            return DistinctDay(date.year, date.month, date.date)
        }
    }
}

data class DayHeader(
    val isExpanded: Boolean,
    val label: String,
    val distinctDay: DistinctDay,
    val entries: List<TimestampEntry>
)

data class TimestampEntry(
    val label: String,
    val timestamp: Timestamp
)

