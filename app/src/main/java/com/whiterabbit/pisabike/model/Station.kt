package com.whiterabbit.pisabike.model

import android.arch.persistence.room.Entity
import android.location.Location
import java.util.*


@Entity(primaryKeys = arrayOf("name", "network"))
data class Station (
    val name : String,
    val network: String,
    var latitude: Double,
    var longitude: Double,
    var address: String,
    var available: Long,
    var free: Long,
    var broken: Long,
    var favourite: Boolean,
    var lastUpdate: Date,
    var addressLoaded: Boolean) {

    fun getDistanceFrom(l: Location?): Double {
        l?.let {return distance(latitude, longitude, l.latitude, l.longitude) }
        return 0.0;
    }
}

fun distance(latitude1: Double, longitude1: Double,
                     latitude2: Double, longitude2: Double): Double {
    val earthRadius = 6371.0 //Kmeters
    val dLat = Math.toRadians(latitude2 - latitude1)
    val dLng = Math.toRadians(longitude2 - longitude1)
    val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(latitude1)) * Math.cos(Math.toRadians(latitude2)) *
            Math.sin(dLng / 2) * Math.sin(dLng / 2)
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    return earthRadius * c
}



