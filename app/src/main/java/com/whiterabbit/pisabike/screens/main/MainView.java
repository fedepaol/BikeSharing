package com.whiterabbit.pisabike.screens.main;


import com.whiterabbit.pisabike.model.Station;

public interface MainView {
    void displayMap();
    void displayList();
    boolean sendBackPressedToMap();
    void onDisplayStationRequested(Station s);
    void displayStationOnMap(Station s);
}
