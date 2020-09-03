package org.openhdp.hdt.data

import androidx.room.Embedded
import androidx.room.Relation

data class StopwatchWithTimestamps(
    @Embedded val stopwatch: Stopwatch,
    @Relation(
        parentColumn = "id",
        entityColumn = "stopwatchId"
    )
    val timestamps: List<Timestamp>
)