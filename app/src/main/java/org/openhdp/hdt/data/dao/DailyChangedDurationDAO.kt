package org.openhdp.hdt.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import org.openhdp.hdt.data.entities.DailyChangedDuration

@Dao
interface DailyChangedDurationDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createDailyChangedDuration(dailyChangedDuration: DailyChangedDuration)

    @Update
    suspend fun updateDailyChangedDuration(dailyChangedDuration: DailyChangedDuration)

    @Delete
    suspend fun deleteDailyChangedDuration(dailyChangedDuration: DailyChangedDuration)

    @Query("SELECT * FROM daily_changed_durations")
    fun getAllDailyChangedDurations(): LiveData<List<DailyChangedDuration>>
}