package org.openhdp.hdt.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import org.openhdp.hdt.data.entities.Stopwatch

@Dao
interface StopwatchDAO {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun createStopwatch(stopwatch: Stopwatch)

    @Update
    suspend fun updateStopwatch(stopwatch: Stopwatch)

    @Delete
    suspend fun deleteStopwatch(stopwatch: Stopwatch)

    @Query("SELECT * FROM stopwatches ORDER BY customOrder")
    suspend fun getAllStopwatchesInOrder(): List<Stopwatch>

    @Query("SELECT COUNT() FROM stopwatches")
    suspend fun getAllStopwatchesCount(): Int

}