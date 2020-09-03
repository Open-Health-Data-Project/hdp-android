package org.openhdp.hdt.data

import androidx.room.Embedded
import androidx.room.Relation

data class StopwatchWithDailySums(
    @Embedded val stopwatch: Stopwatch,
    @Relation(
        parentColumn = "id",
        entityColumn = "stopwatchId"
    )
    val timestamps: List<DailySum>
)