package org.openhdp.hdt.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "daily_sum",
    foreignKeys = [ForeignKey(entity = Stopwatch::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("stopwatchId"),
        onDelete = ForeignKey.CASCADE)])
data class DailySum(
    val stopwatchId: Int,
    val totalTime: Long,
    val changedTime: Long

) {
    @PrimaryKey(autoGenerate = true) val date: Int? = null
}