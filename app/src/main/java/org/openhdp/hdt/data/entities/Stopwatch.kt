package org.openhdp.hdt.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "stopwatches",
    indices = [Index(value = ["categoryId"], unique = false)],
    foreignKeys = [ForeignKey(
        entity = Category::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("categoryId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Stopwatch(
    var customOrder: Int,
    var name: String,
    var categoryId: String,
    @PrimaryKey
    val id: String = java.util.UUID.randomUUID().toString()
)