package org.openhdp.hdt.data.dao

import androidx.room.*
import org.openhdp.hdt.data.entities.Timestamp

@Dao
interface TimestampDAO {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun createTimestamp(timestamp: Timestamp)

    @Transaction
    @Query("UPDATE timestamps SET stopTime = :stopTime WHERE id = :id")
    suspend fun updateTimestamp(id: String, stopTime: Long)

    @Delete
    suspend fun deleteTimestamp(timestamp: Timestamp)

    @Query("SELECT COUNT() FROM timestamps")
    suspend fun getAllTimestampsCount(): Int

    @Query("SELECT * FROM timestamps WHERE stopwatchId=:id ORDER BY startTime DESC LIMIT 1")
    suspend fun lastTimestampOf(id: String): Timestamp?

    @Query("SELECT * FROM timestamps WHERE stopwatchId=:id ORDER BY startTime ASC")
    suspend fun getTimestampsFrom(id: String): List<Timestamp>

    @Query("SELECT * FROM timestamps WHERE stopwatchId=:id AND stopTime!=null AND startTime > :fromDate AND stopTime < :toDate ORDER BY startTime ASC")
    suspend fun getTimestampsFromRange(id: String, fromDate: Long, toDate: Long): List<Timestamp>

    @Query("SELECT * FROM timestamps WHERE stopTime!=null AND startTime > :fromDate AND stopTime < :toDate ORDER BY startTime ASC")
    suspend fun getTimestampsFromRange(fromDate: Long, toDate: Long): List<Timestamp>

    @Query("SELECT * FROM timestamps ORDER BY startTime ASC")
    suspend fun getAllTimestamps(): List<Timestamp>
}