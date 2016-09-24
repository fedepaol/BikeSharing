package com.whiterabbit.pisabike.screens.main;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.whiterabbit.pisabike.PisaBikeApplication;
import com.whiterabbit.pisabike.R;
import com.whiterabbit.pisabike.model.Station;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class MainFragment extends Fragment implements MainView, OnMapReadyCallback {


    @Inject
    MainPresenter mPresenter;
    @Inject
    Context mContext;

    MapFragment mMapFragment;

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
        return res;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapFragment = (MapFragment) getActivity().getFragmentManager()
                                     .findFragmentById(R.id. main_map);

        PisaBikeApplication app = (PisaBikeApplication) getActivity().getApplication();
        DaggerMainComponent.builder()
                .applicationComponent(app.getComponent())
                .mainModule(app.getMainModule(this))
                .build().inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.onPause();
    }

    @Override
    public void updateMyLocation(Location l) {

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
        Marker m = map.addMarker(new MarkerOptions().position(new LatLng(lat, lon)));
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
            mMapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mPresenter.onMapReady();
    }

    @Override
    public void centerCity() {
        CameraUpdate center=
                CameraUpdateFactory.newLatLng(new LatLng(43.7228,
                        10.4017));
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);

        mGoogleMap.moveCamera(center);
        mGoogleMap.animateCamera(zoom);
    }
}
