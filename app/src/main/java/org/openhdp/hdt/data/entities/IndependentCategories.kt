package org.openhdp.hdt.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "independent_categories")
data class IndependentCategories(
    var category1: Int,
    var category2: Int
){
    @PrimaryKey(autoGenerate = true) var id: Int? = null
}