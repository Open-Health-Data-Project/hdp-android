package org.openhdp.hdt.data

import androidx.room.Database
import androidx.room.RoomDatabase
import org.openhdp.hdt.data.dao.*
import org.openhdp.hdt.data.entities.*

@Database(
    entities = [Stopwatch::class, Category::class, Timestamp::class,
        DailyChangedDuration::class, IndependentCategories::class],
    version = 10
    )
abstract class DataTrackingDatabase : RoomDatabase() {

    abstract fun getCategoryDAO(): CategoryDAO

    abstract fun getCategoryWithStopwatchesDAO(): CategoryWithStopwatchesDAO

    abstract fun getDailyChangedDurationDAO(): DailyChangedDurationDAO

    abstract fun getIndependentCategoriesDAO(): IndependentCategoriesDAO

    abstract fun getStopwatchDAO(): StopwatchDAO

    abstract fun getStopwatchWithDailyChangedDurationDAO(): StopwatchWithDailyChangedDurationDAO

    abstract fun getStopwatchWithTimestampsDAO(): StopwatchWithTimestampsDAO

    abstract fun getTimestampDAO(): TimestampDAO

}