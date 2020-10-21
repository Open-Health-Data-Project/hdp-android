package org.openhdp.hdt.data.entities

import androidx.annotation.ColorInt
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    var customOrder: Int,
    var name: String,
    var type: Int,
    @ColorInt
    val color: Int
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}