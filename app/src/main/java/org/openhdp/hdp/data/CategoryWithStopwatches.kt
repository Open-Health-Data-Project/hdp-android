package org.openhdp.hdp.data

import androidx.room.Embedded
import androidx.room.Relation

data class CategoryWithStopwatches(
    @Embedded val category: Category,
    @Relation(
        parentColumn = "id",
        entityColumn = "category_id"
    )
    val stopwatches: List<Stopwatch>
)