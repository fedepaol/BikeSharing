package com.whiterabbit.pisabike.screens.main;

public interface MainPresenter {
    void onPause();
    void onResume();
    void onMapReady();
    void onStationClicked(String stationName);

    boolean hasLocationPermission();
    void onCameraMoved();

    void onCenterLocationClicked();

    void onReloadAsked();

    void onNavigateClicked();
}
