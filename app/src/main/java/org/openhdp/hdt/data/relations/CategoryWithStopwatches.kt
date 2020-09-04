package org.openhdp.hdt.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import org.openhdp.hdt.data.Category
import org.openhdp.hdt.data.Stopwatch

data class CategoryWithStopwatches(
    @Embedded val category: Category,
    @Relation(
        parentColumn = "id",
        entityColumn = "categoryId"
    )
    val stopwatches: List<Stopwatch>
)