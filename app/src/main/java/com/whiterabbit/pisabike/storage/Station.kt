package com.whiterabbit.pisabike.storage

import android.arch.persistence.room.Entity
import java.util.*


@Entity(primaryKeys = {"name","city"})
class Station (
    val name : String,
    val city: String,
    var latitude: Double,
    var longitude: Double,
    var address: String,
    var available: Long,
    var free: Long,
    var broken: Long,
    var favourite: Boolean,
    var lastUpdate: Date,
    var addressLoaded: Boolean)


