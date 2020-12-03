package org.openhdp.hdt.data.enums

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun toPrivacyState(state: String): PrivacyState{
        return PrivacyState.valueOf(state)
    }

    @TypeConverter
    fun fromPrivacyState(privacyState: PrivacyState): String{
        return privacyState.toString()
    }

    @TypeConverter
    fun toExtraDataType(type: String): ExtraDataType?{
        return if (type == "") {
            null
        } else {
            ExtraDataType.valueOf(type)
        }
    }

    @TypeConverter
    fun fromExtraDataType(extraDataType: ExtraDataType?): String{
        return extraDataType?.toString() ?: ""
    }
}