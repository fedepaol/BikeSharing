package com.whiterabbit.pisabike.screens.map

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

/**
 * Created by fedepaol on 03/06/17.
 */

class MapItem(val station : Station) : ClusterItem {

    val myLocation = LatLng(station.latitude, station.longitude)
    var isSelected = false

    override fun getSnippet(): String {
        return station.name
    }

    override fun getTitle(): String {
        return station.name
    }

    override fun getPosition(): LatLng {
        return myLocation
    }

    override fun hashCode(): Int {
        return station.name.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other != null) {
            val otherItem = other as MapItem
            return otherItem.station == station
        }
        return false
    }
}
