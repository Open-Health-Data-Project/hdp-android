package org.openhdp.hdt.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.openhdp.hdt.data.entities.Category

@Entity(tableName = "stopwatches",
    indices = [Index(value = ["categoryId"], unique = true)],
    foreignKeys = [ForeignKey(entity = Category::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("categoryId"),
        onDelete = ForeignKey.CASCADE)]
)
data class Stopwatch(
    var customOrder: Int,
    var name: String,
    var categoryId: Int
){
    @PrimaryKey(autoGenerate = true) var id: Int? = null
}