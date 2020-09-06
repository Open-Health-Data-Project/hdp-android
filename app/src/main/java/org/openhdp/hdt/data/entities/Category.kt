package org.openhdp.hdt.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    var customOrder: Int,
    var name: String,
    var type: Int
){
    @PrimaryKey(autoGenerate = true) var id: Int? = null
}