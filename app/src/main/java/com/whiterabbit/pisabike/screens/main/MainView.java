package com.whiterabbit.pisabike.screens.main;

import android.location.Location;

import com.whiterabbit.pisabike.model.Station;

import java.util.List;

public interface MainView {
    void centerMapToLocation(Location l);
    void drawStationsOnMap(List<Station> stations);
    void displayStationList(List<Station> stations, Location current);
    void hideStationList();
    void displayStationDetail(Station detail, Location current);
    void hideStationDetail();
    void stopUpdating();
    void startUpdating();
    void getMap();
    void centerCity();
    void enableMyLocation();

    void highLightStation(Station s);

    void unHighLightStation(Station s);
}
