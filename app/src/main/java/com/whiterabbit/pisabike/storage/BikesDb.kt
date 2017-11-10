package com.whiterabbit.pisabike.storage

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.whiterabbit.pisabike.model.BikesNetwork
import com.whiterabbit.pisabike.model.Station

/**
 * Created by fedepaol on 22/09/17.
 */

@Database(entities = arrayOf(Station::class, BikesNetwork::class), version = 1)
@TypeConverters(DateConverter::class)
abstract class BikesDatabasee: RoomDatabase() {
    abstract fun bikesDao(): BikesDao
}