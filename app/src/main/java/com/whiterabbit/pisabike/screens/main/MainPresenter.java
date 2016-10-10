package com.whiterabbit.pisabike.screens.main;

import com.whiterabbit.pisabike.model.Station;

public interface MainPresenter {
    void onPause();
    void onResume();
    void onMapReady();
    void onStationClicked(Station s);

    boolean hasLocationPermission();
    void onCameraMoved();

    void onCenterLocationClicked();

    void onReloadAsked();

    void onNavigateClicked();
}
