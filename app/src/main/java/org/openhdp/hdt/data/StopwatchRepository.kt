package org.openhdp.hdt.data

import org.openhdp.hdt.data.dao.*
import org.openhdp.hdt.data.entities.Stopwatch
import org.openhdp.hdt.data.entities.Timestamp
import org.openhdp.hdt.data.entities.Category
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

    suspend fun updateStopwatchName(stopwatchId: String, stopwatchName: String) =
        with(stopwatchDAO) {
            findStopwatch(stopwatchId)?.let {
                updateStopwatch(it.copy(name = stopwatchName))
            }

        }

    suspend fun totalStopwatchesCount() = stopwatchDAO.getAllStopwatchesCount()

    suspend fun lastTimestampOf(stopwatchId: String) = timestampDAO.lastTimestampOf(stopwatchId)

    suspend fun updateTimestamp(id: String, toggleTime: Long) =
        timestampDAO.updateTimestamp(id, toggleTime)

    suspend fun createTimestamp(timestamp: Timestamp) {
        timestampDAO.createTimestamp(timestamp)
    }

    suspend fun createOrUpdateCategory(category: Category) {
        if (categoryDAO.findCategory(category.id) != null) {
            categoryDAO.updateCategory(category)
        } else {
            categoryDAO.createCategory(category)
        }
    }

    suspend fun deleteCategory(category: Category) = categoryDAO.deleteCategory(category)
  
    suspend fun deleteStopwatch(stopwatchId: String) = with(stopwatchDAO) {
        findStopwatch(stopwatchId)?.let {
            stopwatchDAO.deleteStopwatch(it)
        }
    }
}