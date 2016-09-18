package com.whiterabbit.pisabike.screens.main;

import android.location.Location;

import com.whiterabbit.pisabike.model.Station;

import java.util.List;

public interface MainView {
    void updateMyLocation(Location l);
    void centerMapToLocation(Location l);
    void drawStations(List<Station> stations);
    void displayStationList(List<Station> stations);
    void hideStationList();
}
