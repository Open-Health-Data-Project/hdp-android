package org.openhdp.hdt.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import org.openhdp.hdt.data.entities.Timestamp

@Dao
interface TimestampDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createTimestamp(timestamp: Timestamp)

    @Update
    suspend fun updateTimestamp(timestamp: Timestamp)

    @Delete
    suspend fun deleteTimestamp(timestamp: Timestamp)

    @Query("SELECT * FROM timestamps")
    fun getAllTimestamps(): LiveData<List<Timestamp>>
}