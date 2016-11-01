package com.whiterabbit.pisabike.screens.maincontainer;


import android.os.Bundle;

public interface MainContainerPresenter {
    void onMapSelectedFromMenu();

    void onBackPressed();

    void onListSelectedFromMenu();

    void onSaveState(Bundle state);

    void onRestoreState(Bundle state);

    void onViewAttached(MainContainerView view);

    void onViewDetached();
}
