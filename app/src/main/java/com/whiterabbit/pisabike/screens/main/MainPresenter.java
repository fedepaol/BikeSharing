package com.whiterabbit.pisabike.screens.main;


import android.os.Bundle;

public interface MainPresenter {
    void onMapSelectedFromMenu();

    boolean onBackPressed();

    void onListSelectedFromMenu();

    void onSaveState(Bundle state);

    void onRestoreState(Bundle state);

    void onViewAttached(MainView view);

    void onViewDetached();
}
