package org.openhdp.hdt.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "stopwatches",
    foreignKeys = [ForeignKey(entity = Category::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("categoryId"),
        onDelete = ForeignKey.CASCADE)]
)
data class Stopwatch(
    val name: String,
    val categoryId: Int
){
    @PrimaryKey(autoGenerate = true) val id: Int? = null
}