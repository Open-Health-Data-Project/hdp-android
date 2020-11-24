package org.openhdp.hdt.data.entities

import androidx.room.*
import org.openhdp.hdt.data.enums.Converters
import org.openhdp.hdt.data.enums.ExtraDataType
import org.openhdp.hdt.data.enums.PrivacyState


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
    @TypeConverters(Converters::class)
    var privacyState: PrivacyState,
    var sharedName: String?,
    @TypeConverters(Converters::class)
    var extraDataType: ExtraDataType?,
    @PrimaryKey
    val id: String = java.util.UUID.randomUUID().toString()
)