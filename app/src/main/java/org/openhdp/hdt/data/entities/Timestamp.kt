package org.openhdp.hdt.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.openhdp.hdt.data.entities.Stopwatch

@Entity(
    tableName = "timestamps",
    indices = [Index(value = ["stopwatchId"], unique = true)],
    foreignKeys = [ForeignKey(
        entity = Stopwatch::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("stopwatchId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Timestamp(
    var stopwatchId: Int,
    var startTime: Long
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
    var stopTime: Long? = null
}
