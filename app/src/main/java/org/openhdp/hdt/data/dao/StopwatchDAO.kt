package org.openhdp.hdt.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import org.openhdp.hdt.data.Stopwatch

@Dao
interface StopwatchDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createStopwatch(stopwatch: Stopwatch)

    @Update
    suspend fun updateStopwatch(stopwatch: Stopwatch)

    @Delete
    suspend fun deleteStopwatch(stopwatch: Stopwatch)

    @Query("SELECT name FROM stopwatches WHERE id=:id ")
    fun getStopwatchName(id: Int): String

    @Query("SELECT categoryId FROM stopwatches WHERE id=:id ")
    fun getStopwatchCategoryId(id: Int): Int

    @Query("SELECT * FROM stopwatches ORDER BY name")
    fun getAllStopwatchesAlphabetically(): LiveData<List<Stopwatch>>

}