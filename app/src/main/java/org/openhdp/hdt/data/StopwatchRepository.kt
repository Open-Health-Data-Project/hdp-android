package org.openhdp.hdt.data

import org.openhdp.hdt.data.dao.*
import org.openhdp.hdt.data.entities.Stopwatch
import org.openhdp.hdt.data.entities.Timestamp
import javax.inject.Inject

class StopwatchRepository @Inject constructor(
    val categoryDAO: CategoryDAO,
    val categoryWithStopwatchesDAO: CategoryWithStopwatchesDAO,
    val dailyChangedDurationDAO: DailyChangedDurationDAO,
    val independentCategoriesDAO: IndependentCategoriesDAO,
    val stopwatchDAO: StopwatchDAO,
    val stopwatchWithDailyChangedDurationDAO: StopwatchWithDailyChangedDurationDAO,
    val stopwatchWithTimestampsDAO: StopwatchWithTimestampsDAO,
    val timestampDAO: TimestampDAO
) {

    suspend fun categories() = categoryDAO.getAllCategoriesOrdered()

    suspend fun stopwatches() = stopwatchDAO.getAllStopwatchesInOrder()

    suspend fun timestamps(stopwatchId: String) = timestampDAO.getTimestampsFrom(stopwatchId)

    suspend fun allTimestamps() = timestampDAO.getAllTimestamps()

    suspend fun totalTimestampsCount() = timestampDAO.getAllTimestampsCount()

    suspend fun createStopwatch(stopwatch: Stopwatch) = stopwatchDAO.createStopwatch(stopwatch)

    suspend fun totalStopwatchesCount() = stopwatchDAO.getAllStopwatchesCount()

    suspend fun lastTimestampOf(stopwatchId: String) = timestampDAO.lastTimestampOf(stopwatchId)

    suspend fun updateTimestamp(id: String, toggleTime: Long) =
        timestampDAO.updateTimestamp(id, toggleTime)

    suspend fun createTimestamp(timestamp: Timestamp) {
        timestampDAO.createTimestamp(timestamp)
    }
}