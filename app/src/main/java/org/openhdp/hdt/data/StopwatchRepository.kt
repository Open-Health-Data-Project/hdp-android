package org.openhdp.hdt.data

import org.openhdp.hdt.data.dao.*
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

    suspend fun totalTimestampsCount() = timestampDAO.getAllTimestampsCount()

}