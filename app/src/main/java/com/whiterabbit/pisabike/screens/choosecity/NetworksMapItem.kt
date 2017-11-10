package com.whiterabbit.pisabike.screens.choosecity

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import com.whiterabbit.pisabike.model.BikesNetwork

/**
 * Created by fedepaol on 11/8/17.
 */

data class NetworksMapItem(val network : BikesNetwork) : ClusterItem {
    override fun getSnippet(): String {
        return network.city
    }

    override fun getTitle(): String {
        return network.network
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPosition(): LatLng {
        return LatLng(network.coordinates.latitude, network.coordinates.longitude)
    }
}