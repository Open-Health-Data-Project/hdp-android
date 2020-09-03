package org.openhdp.hdt.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    val name: String,
    val type: Int
){
    @PrimaryKey(autoGenerate = true) val id: Int? = null
}