package org.openhdp.hdt.ui.history

import org.openhdp.hdt.data.StopwatchRepository
import org.openhdp.hdt.data.entities.Timestamp
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ProvideHistoricEntriesUseCase @Inject constructor(
    private val stopwatchRepository: StopwatchRepository
) {
    //key is the stopwatch id, value is the name of the stopwatch
    private val stopwatchNames: HashMap<String, String> = HashMap()

    private suspend fun resolveStopwatchName(stopwatchId: String): String {
        val value = stopwatchNames[stopwatchId]
        if (value == null) {
            stopwatchRepository.stopwatches().forEach {
                stopwatchNames[it.id] = it.name
            }
        }
        return stopwatchNames[stopwatchId] ?: ""
    }

    suspend fun execute(stopwatchId: String? = null): List<DayHeader> {
        val result = arrayListOf<DayHeader>()
        val totalTimestamps = if (stopwatchId != null) {
            stopwatchRepository.timestamps(stopwatchId)
        } else {
            stopwatchRepository.allTimestamps()
        }

        val timestamps = totalTimestamps.filter { it.stopTime != null }.sortedBy { it.startTime }

        if (timestamps.isEmpty()) {
            return result
        }

        val firstDay = timestamps.first().toCalendarDay()
        val lastDay = timestamps.last().toCalendarDay()
        Timber.d("first day is ${firstDay.asDate().time} / last day is ${lastDay.asDate().time}")
        includeDaysFrom(timestamps, result)

        return result
    }

    private suspend fun includeDaysFrom(
        timestamps: List<Timestamp>,
        result: ArrayList<DayHeader>
    ) {

        val truncatedTimestamps = timestamps.map {
            Date(it.startTime).truncated()
        }.distinct().toList().sorted()

        for (date in truncatedTimestamps) {
            val list = timestamps.filter { Date(it.startTime).truncated() == date }
            if (list.isNotEmpty()) {
                result.add(
                    DayHeader(
                        false,
                        DAY_FORMAT.format(date),
                        DistinctDay.create(date),
                        list.map {
                            val startDate = Date(it.startTime)
                            val endDate = Date(it.stopTime!!)
                            val label = String.format(
                                "From %02d:%02d to %02d:%02d",
                                startDate.hours,
                                startDate.minutes,
                                endDate.hours,
                                endDate.minutes
                            )
                            TimestampEntry(label, it, resolveStopwatchName(it.stopwatchId))
                        }
                    )
                )
            }
        }
    }

}

val DAY_FORMAT = SimpleDateFormat("d MMMM yyyy", Locale.ENGLISH)


fun Timestamp.toCalendarDay(): DistinctDay {
    return Date(startTime).mapTo { DistinctDay.create(it) }
}

fun <Source, Target> Source.mapTo(mapper: (Source) -> Target): Target = mapper(this)

data class DistinctDay(val year: Int, val month: Int, val dayOfMonth: Int) {

    fun increment(): DistinctDay {
        val date = Date(year, month, dayOfMonth, 0, 0, 0)
        val singleDayInMillis = TimeUnit.DAYS.toMillis(1)
        return create(Date(date.time + singleDayInMillis))
    }

    fun matches(date: Date): Boolean {
        return date.date == dayOfMonth && date.month == month && date.year == year
    }

    fun asDate(): Date = Date(year, month, dayOfMonth, 0, 0, 0)

    companion object {

        fun create(date: Date): DistinctDay {
            return DistinctDay(date.year, date.month, date.date)
        }
    }
}

fun Date.truncated(): Date {
    return Date(year, month, date, 0, 0, 0)
}

data class DayHeader(
    val isExpanded: Boolean,
    val label: String,
    val distinctDay: DistinctDay,
    val entries: List<TimestampEntry>
)

data class TimestampEntry(
    val label: String,
    val timestamp: Timestamp,
    val stopwatchName: String
)

