package br.com.alura.helloapp.database.converter

import androidx.room.TypeConverter
import java.util.Date

class Converter {

    @TypeConverter
    fun fromDateToLong(value: Date?): Long? {
        return value?.time
    }

    @TypeConverter
    fun fromLongToDate(value: Long?): Date? {
        return value?.let { Date(it) }
    }
}