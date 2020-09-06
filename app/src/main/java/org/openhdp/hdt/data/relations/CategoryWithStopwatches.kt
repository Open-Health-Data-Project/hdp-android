package org.openhdp.hdt.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import org.openhdp.hdt.data.entities.Category
import org.openhdp.hdt.data.entities.Stopwatch

data class CategoryWithStopwatches(
    @Embedded val category: Category,
    @Relation(
        parentColumn = "id",
        entityColumn = "categoryId"
    )
    val stopwatches: List<Stopwatch>
)