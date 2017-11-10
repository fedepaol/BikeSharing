package com.whiterabbit.pisabike.screens.choosecity

import android.content.Context
import android.graphics.Bitmap

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.whiterabbit.pisabike.model.Station
import com.whiterabbit.pisabike.screens.map.MapItem
import com.whiterabbit.pisabike.ui.MapMarkerFactory

/**
 * Created by fedepaol on 03/06/17.
 */

class ChooseNetworkItemRenderer(val mContext: Context,
                                val map: GoogleMap,
                                val clusterManager: ClusterManager<NetworksMapItem>,
                                private val mListener: GoogleMap.OnCameraIdleListener) : DefaultClusterRenderer<NetworksMapItem>(mContext, map, clusterManager), GoogleMap.OnCameraIdleListener {

    private val mMarkerFactory: MapMarkerFactory = MapMarkerFactory()

    override fun onCameraIdle() {
        mListener.onCameraIdle()
    }

    override fun onClusterItemRendered(clusterItem: NetworksMapItem, marker: Marker) {
        return super.onClusterItemRendered(clusterItem, marker)
        /*
        val (_, _, _, _, _, available, free) = clusterItem!!.station
        val bitmap: Bitmap

        bitmap = when (clusterItem.isSelected) {
            true -> mMarkerFactory.getSelectedMapMarker(available, free, mContext)
            else -> mMarkerFactory.getNotSelectedMapMarker(available, free, mContext)
        }

        val icon = BitmapDescriptorFactory.fromBitmap(bitmap)
        marker!!.setIcon(icon) */
        // TODO
    }
}
