package org.openhdp.hdp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "timestamps",
    foreignKeys = arrayOf(
        ForeignKey(entity = Stopwatch::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("stopwatch_id"),
        onDelete = ForeignKey.CASCADE)))
data class Timestamp(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "stopwatch_id") val stopwatchId: Int,
    @ColumnInfo(name = "start_time") val startTime: Int,
    @ColumnInfo(name = "stop_time") val stopTime: Int
)