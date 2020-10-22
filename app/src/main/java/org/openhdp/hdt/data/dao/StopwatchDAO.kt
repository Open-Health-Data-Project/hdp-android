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

    @Query("SELECT name FROM stopwatches WHERE id=:id")
    fun getStopwatchName(id: Int): LiveData<String>

    @Query("SELECT categoryId FROM stopwatches WHERE id=:id ")
    fun getStopwatchCategoryId(id: Int): LiveData<Int>

    @Query("SELECT * FROM stopwatches ORDER BY customOrder")
    suspend fun getAllStopwatchesInOrder(): List<Stopwatch>

    @Query("SELECT COUNT() FROM stopwatches")
    suspend fun getAllStopwatchesCount(): Int

}