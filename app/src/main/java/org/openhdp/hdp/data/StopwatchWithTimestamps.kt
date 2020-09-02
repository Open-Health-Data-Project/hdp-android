package org.openhdp.hdp.data

import androidx.room.Embedded
import androidx.room.Relation

data class StopwatchWithTimestamps(
    @Embedded val stopwatch: Stopwatch,
    @Relation(
        parentColumn = "id",
        entityColumn = "stopwatch_id"
    )
    val timestamps: List<Timestamp>
)