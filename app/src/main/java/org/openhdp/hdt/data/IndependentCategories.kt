package org.openhdp.hdt.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "independent_categories")
data class IndependentCategories(
    val category1: Int,
    val category2: Int
){
    @PrimaryKey(autoGenerate = true) val id: Int? = null
}