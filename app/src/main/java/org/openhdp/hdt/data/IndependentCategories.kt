package org.openhdp.hdt.data

import androidx.room.Entity

@Entity(tableName = "independent_categories",
    primaryKeys = ["category1, category2"])
data class IndependentCategories(
    val category1: Int,
    val category2: Int
)