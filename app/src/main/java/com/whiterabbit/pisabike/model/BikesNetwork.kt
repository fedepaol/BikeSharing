package com.whiterabbit.pisabike.model

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by fedepaol on 10/8/17.
 */

@Entity(tableName = "network")
data class BikesNetwork(@PrimaryKey val city: String,
                        val network: String,
                        @Embedded val coordinates: Coordinates)
@Entity
data class Coordinates(val latitude: Double,
                       val longitude: Double)

data class Networks(val networks: Array<BikesNetwork>)