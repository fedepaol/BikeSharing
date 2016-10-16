package com.whiterabbit.pisabike.screens.main;

public interface MainPresenter {
    void onViewDetached();
    void onViewAttached(MainView view, boolean isNew);
    void onMapReady(boolean mustCenter);
    void onStationClicked(String stationName);

    boolean hasLocationPermission();
    void onCameraMoved();

    void onCenterLocationClicked();

    void onReloadAsked();

    void onNavigateClicked();

    void onCameraIdle();
}
