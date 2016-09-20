package com.whiterabbit.pisabike.screens.main;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.whiterabbit.pisabike.model.Station;

import java.util.List;

import javax.inject.Inject;

public class MainFragment extends Fragment implements MainView {
    @Inject
    MainPresenter mPresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
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
    public void stopUpdating() {

    }

    @Override
    public void startUpdating() {

    }
}
