package org.openhdp.hdt.data

import androidx.room.Embedded
import androidx.room.Relation

data class CategoryWithStopwatches(
    @Embedded val category: Category,
    @Relation(
        parentColumn = "id",
        entityColumn = "categoryId"
    )
    val stopwatches: List<Stopwatch>
)