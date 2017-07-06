package com.whiterabbit.pisabike.screens.map;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.whiterabbit.pisabike.model.Station;
import com.whiterabbit.pisabike.ui.MapMarkerFactory;

/**
 * Created by fedepaol on 03/06/17.
 */

public class CameraIdleRenderer extends DefaultClusterRenderer<MapItem> implements GoogleMap.OnCameraIdleListener {
    private GoogleMap.OnCameraIdleListener mListener;
    private MapMarkerFactory mMarkerFactory;
    private Context mContext;

    public CameraIdleRenderer(Context context,
                              GoogleMap map,
                              ClusterManager clusterManager,
                              GoogleMap.OnCameraIdleListener listener) {
        super(context, map, clusterManager);
        mListener = listener;
        mMarkerFactory = new MapMarkerFactory();
        mContext = context;
    }

    @Override
    public void onCameraIdle() {
        mListener.onCameraIdle();
    }

    @Override
    protected void onBeforeClusterItemRendered(MapItem item, MarkerOptions markerOptions) {
        super.onBeforeClusterItemRendered(item, markerOptions);
    }

    @Override
    protected void onClusterItemRendered(MapItem clusterItem, Marker marker) {

        Station s = clusterItem.getStation();
        Bitmap bitmap;
        if (!clusterItem.isSelected()) {
            bitmap = mMarkerFactory.getNotSelectedMapMarker(s.getAvailable(), s.getSpaces(), mContext);
        } else {
            bitmap = mMarkerFactory.getSelectedMapMarker(s.getAvailable(), s.getSpaces(), mContext);
        }

        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bitmap);
        marker.setIcon(icon);
    }
}
