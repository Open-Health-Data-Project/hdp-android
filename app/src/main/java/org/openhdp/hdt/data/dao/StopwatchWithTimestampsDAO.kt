package org.openhdp.hdt.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import org.openhdp.hdt.data.relations.StopwatchWithTimestamps

@Dao
interface StopwatchWithTimestampsDAO {

    @Transaction
    @Query("SELECT * FROM stopwatches")
    fun getStopwatchWithTimestamps(): LiveData<List<StopwatchWithTimestamps>>
}