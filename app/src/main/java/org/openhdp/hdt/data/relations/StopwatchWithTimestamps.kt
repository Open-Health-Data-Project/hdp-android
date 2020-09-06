package org.openhdp.hdt.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import org.openhdp.hdt.data.entities.Stopwatch
import org.openhdp.hdt.data.entities.Timestamp

data class StopwatchWithTimestamps(
    @Embedded val stopwatch: Stopwatch,
    @Relation(
        parentColumn = "id",
        entityColumn = "stopwatchId"
    )
    val timestamps: List<Timestamp>
)