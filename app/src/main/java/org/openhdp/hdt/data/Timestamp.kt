package org.openhdp.hdt.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "timestamps",
    foreignKeys = [ForeignKey(entity = Stopwatch::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("stopwatchId"),
        onDelete = ForeignKey.CASCADE)]
)
data class Timestamp(
    val stopwatchId: Int,
    val startTime: Int
){
    @PrimaryKey(autoGenerate = true) val id: Int? = null
    val stopTime: Int? = null
}