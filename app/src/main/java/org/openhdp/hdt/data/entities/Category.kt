package org.openhdp.hdt.data.entities

import androidx.annotation.ColorInt
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "categories")
data class Category(
    var customOrder: Int,
    var name: String,
    var type: Int,
    @ColorInt
    val color: Int,
    @PrimaryKey
    val id: String = java.util.UUID.randomUUID().toString()
) : Serializable
