package org.openhdp.hdt.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "daily_changed_durations",
    foreignKeys = [ForeignKey(entity = Stopwatch::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("stopwatchId"),
        onDelete = ForeignKey.CASCADE)])
data class DailyChangedDuration(
    val stopwatchId: Int,
    val durationChange: Long
) {
    @PrimaryKey(autoGenerate = true) val date: Int? = null
}