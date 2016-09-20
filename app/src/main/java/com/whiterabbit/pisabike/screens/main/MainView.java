package com.whiterabbit.pisabike.screens.main;

import android.location.Location;

import com.whiterabbit.pisabike.model.Station;

import java.util.List;

public interface MainView {
    void updateMyLocation(Location l);
    void centerMapToLocation(Location l);
    void drawStationsOnMap(List<Station> stations);
    void displayStationList(List<Station> stations, Location current);
    void hideStationList();

    void stopUpdating();
    void startUpdating();
}
