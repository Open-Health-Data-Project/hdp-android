package org.openhdp.hdt.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import org.openhdp.hdt.data.entities.DailyChangedDuration
import org.openhdp.hdt.data.entities.Stopwatch

data class StopwatchWithDailyChangedDuration(
    @Embedded val stopwatch: Stopwatch,
    @Relation(
        parentColumn = "id",
        entityColumn = "stopwatchId"
    )
    val timestamps: List<DailyChangedDuration>
)