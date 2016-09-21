package com.whiterabbit.pisabike.screens.main;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.whiterabbit.pisabike.PisaBikeApplication;
import com.whiterabbit.pisabike.R;
import com.whiterabbit.pisabike.model.Station;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class MainFragment extends Fragment implements MainView, OnMapReadyCallback {
    @Inject
    MainPresenter mPresenter;
    MapFragment mMapFragment;

    private GoogleMap mGoogleMap;

    public static MainFragment createInstance() {
        MainFragment res = new MainFragment();
        return res;
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
                .detailModule(app.getDetailModule(this))
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
}
