package com.whiterabbit.pisabike.screens.main;


import android.os.Bundle;

import com.whiterabbit.pisabike.model.Station;

public interface MainPresenter {
    void onMapSelectedFromMenu();

    boolean onBackPressed();

    void onListSelectedFromMenu();

    void onSaveState(Bundle state);

    void onRestoreState(Bundle state);

    void onViewAttached(MainView view);

    void onViewDetached();

    void onDisplayStationRequested(Station s);
}
