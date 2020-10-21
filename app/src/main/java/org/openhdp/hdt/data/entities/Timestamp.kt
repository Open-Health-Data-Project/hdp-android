package org.openhdp.hdt.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "timestamps",
    indices = [Index(value = ["stopwatchId"], unique = false)],
    foreignKeys = [ForeignKey(
        entity = Stopwatch::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("stopwatchId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Timestamp(
    val stopwatchId: String,
    val startTime: Long,
    @PrimaryKey(autoGenerate = false)
    val id: String = java.util.UUID.randomUUID().toString()
) {
    var stopTime: Long? = null
}
