package org.openhdp.hdt.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import org.openhdp.hdt.data.relations.StopwatchWithDailyChangedDuration

@Dao
interface StopwatchWithDailyChangedDurationDAO {

    @Transaction
    @Query("SELECT * FROM stopwatches")
    fun getStopwatchWithDailyChangedDuration(): LiveData<List<StopwatchWithDailyChangedDuration>>
}