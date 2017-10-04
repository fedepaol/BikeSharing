package com.whiterabbit.pisabike.storage



/**
 * Created by fedepaol on 27/09/17.
 */

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

class DateConverter {
    @TypeConverter
    fun toDate(timestamp: Long?): Date? = if (timestamp == null) null else Date(timestamp)

    @TypeConverter
    fun toTimestamp(date: Date?): Long? = date?.time
}