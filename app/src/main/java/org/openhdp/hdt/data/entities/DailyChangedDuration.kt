package org.openhdp.hdt.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "daily_changed_durations",
    indices = [Index(value = ["stopwatchId"], unique = true)],
    foreignKeys = [ForeignKey(entity = Stopwatch::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("stopwatchId"),
        onDelete = ForeignKey.CASCADE)])
data class DailyChangedDuration(
    var stopwatchId: Int,
    var durationChange: Long
) {
    @PrimaryKey(autoGenerate = true) var date: Int? = null
}