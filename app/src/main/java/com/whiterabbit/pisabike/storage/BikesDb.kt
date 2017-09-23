package com.whiterabbit.pisabike.storage

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.whiterabbit.pisabike.model.Station

/**
 * Created by fedepaol on 22/09/17.
 */

@Database(entities = arrayOf(Station::class), version = 1)
abstract class BikesDatabasee: RoomDatabase() {
    abstract fun bikesDao(): BikesDao
}