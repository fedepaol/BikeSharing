package com.whiterabbit.pisabike.screens.main;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.whiterabbit.pisabike.PisaBikeApplication;
import com.whiterabbit.pisabike.R;
import com.whiterabbit.pisabike.model.Station;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainFragment extends Fragment implements MainView, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {


    @Inject
    MainPresenter mPresenter;
    @Inject
    Context mContext;

    @Bind(R.id.main_map)
    MapView mMapView;

    private GoogleMap mGoogleMap;
    private HashMap<String, Station> stationsMap = new HashMap<String, Station>();

    public static MainFragment createInstance() {
        MainFragment res = new MainFragment();
        return res;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View res = inflater.inflate(R.layout.main_fragment, container, false);
        ButterKnife.bind(this, res);
        mMapView.onCreate(savedInstanceState);
        return res;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        PisaBikeApplication app = (PisaBikeApplication) getActivity().getApplication();
        DaggerMainComponent.builder()
                .applicationComponent(app.getComponent())
                .mainModule(app.getMainModule(this))
                .build().inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        mPresenter.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.onPause();
        mMapView.onPause();
    }

    @Override
    public void centerMapToLocation(Location l) {

    }

    @Override
    public void drawStationsOnMap(List<Station> stations) {
        stationsMap.clear();

        for (Station s : stations) {
            addMarker(mGoogleMap, s.getLatitude(), s.getLongitude(), s);
        }

        mGoogleMap.setInfoWindowAdapter(new PopupAdapter(getActivity().getLayoutInflater(), stationsMap));
    }

    private void addMarker(GoogleMap map, double lat, double lon,
                           Station s) {
        Bitmap b = MapMarkerFactory.getMapMarker(s.getAvailable(), s.getFree() + s.getAvailable(), mContext);
        Marker m = map.addMarker(new MarkerOptions().position(new LatLng(lat, lon))
                .icon(BitmapDescriptorFactory.fromBitmap(b)));
        stationsMap.put(m.getId(), s);
    }

    @Override
    public void displayStationList(List<Station> stations, Location current) {

    }

    @Override
    public void hideStationList() {

    }

    @Override
    public void displayStationDetail(Station detail, Location current) {

    }

    @Override
    public void hideStationDetail() {

    }

    @Override
    public void stopUpdating() {

    }

    @Override
    public void startUpdating() {

    }

    @Override
    public void getMap() {
        if (mGoogleMap != null) {
            mPresenter.onMapReady();
        } else {
            mMapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mPresenter.onMapReady();
        mGoogleMap.setOnMarkerClickListener(this);


    }

    @Override
    public void centerCity() {
        CameraUpdate center =
                CameraUpdateFactory.newLatLng(new LatLng(43.7228,
                        10.4017));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);

        mGoogleMap.moveCamera(center);
        mGoogleMap.animateCamera(zoom);
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        Station s = stationsMap.get(marker.getId());
        mPresenter.onStationClicked(s);
        return true;
    }

    @Override
    public void enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(mContext, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
             return;
        }
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
