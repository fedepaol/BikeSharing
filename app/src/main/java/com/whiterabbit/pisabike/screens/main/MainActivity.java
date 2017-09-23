/*
 * Copyright (c) 2016 Federico Paolinelli.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.whiterabbit.pisabike.screens.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.whiterabbit.androidutils.AskForRateDialog;
import com.whiterabbit.androidutils.InAppPurchaseHelper;
import com.whiterabbit.pisabike.PisaBikeApplication;
import com.whiterabbit.pisabike.R;
import com.whiterabbit.pisabike.screens.list.StationsFavsFragment;
import com.whiterabbit.pisabike.screens.list.StationsListFragment;
import com.whiterabbit.pisabike.screens.map.MapFragment;
import com.whiterabbit.pisabike.storage.AddressJobKt;
import com.whiterabbit.pisabike.storage.PrefsStorage;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity implements MainView, BottomNavigationView.OnNavigationItemSelectedListener, InAppPurchaseHelper.RemoveAdsListener {
    private static final String MAP_TAG = "map";
    private static final String LIST_TAG = "list";
    private static final String FAVOURITES_TAG = "favourites";

    @Inject
    MainPresenter mPresenter;

    @Inject
    PrefsStorage mStorage;

    @Bind(R.id.bottom_navigation)
    BottomNavigationView mBottomNavigation;

    @Bind(R.id.adView)
    AdView mAdView;

    @Bind(R.id.main_activity_frame)
    FrameLayout mMainLayout;

    InAppPurchaseHelper mPurchaseHelper;

    MapFragment mapFragment;
    StationsListFragment listFragment;
    StationsFavsFragment favsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PisaBikeApplication app = (PisaBikeApplication) getApplication();

        DaggerMainComponent.builder()
                .applicationComponent(app.getComponent())
                .mainModule(app.getMainModule(this))
                .build().inject(this);

        ButterKnife.bind(this);
        mBottomNavigation.setOnNavigationItemSelectedListener(this);
        createFragments();

        mPurchaseHelper = new InAppPurchaseHelper(this, "remove_ads", this);
        mPurchaseHelper.onCreate(30, 50);

        if (InAppPurchaseHelper.isAdsUnlocked(this) == InAppPurchaseHelper.AdsUnlocked.UNLOCKED) {
            mAdView.setVisibility(GONE);
        }

        AskForRateDialog.checkAndAskForRate(40, 70, 120, this);

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        if (savedInstanceState == null) {
            AddressJobKt.scheduleAddressJob();
        }
    }

    private void createFragments() {
        mapFragment = (MapFragment) getSupportFragmentManager().findFragmentByTag(MAP_TAG);
        if (mapFragment == null) {
            mapFragment = MapFragment.createInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_activity_frame, mapFragment, MAP_TAG)
                    .commit();
        }

        favsFragment = (StationsFavsFragment) getSupportFragmentManager().findFragmentByTag(FAVOURITES_TAG);

        if (favsFragment == null) {
            favsFragment = new StationsFavsFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_activity_frame, favsFragment, FAVOURITES_TAG)
                    .addToBackStack(null)
                    .commit();
        }

        listFragment = (StationsListFragment) getSupportFragmentManager().findFragmentByTag(LIST_TAG);

        if (listFragment == null) {
            listFragment = new StationsListFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_activity_frame, listFragment, LIST_TAG)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (mPresenter.onBackPressed()) {
            return;
        }

        finish();
    }

    @Override
    public void displayMap() {
        getSupportFragmentManager().beginTransaction().show(mapFragment)
                .hide(listFragment)
                .hide(favsFragment).commit();

    }

    @Override
    public void displayList() {
        getSupportFragmentManager().beginTransaction().hide(mapFragment)
                .show(listFragment)
                .hide(favsFragment).commit();
    }

    @Override
    public void displayFavourites() {
        getSupportFragmentManager().beginTransaction().hide(mapFragment)
                .hide(listFragment)
                .show(favsFragment).commit();    }

    @Override
    public boolean sendBackPressedToMap() {
        return mapFragment.onBackPressed();
    }

    @Override
    protected void onResume() {
        mPresenter.onViewAttached(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        mPresenter.onViewDetached();
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mPresenter.onSaveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mPresenter.onRestoreState(savedInstanceState);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_map:
                mPresenter.onMapSelectedFromMenu();
                break;
            case R.id.action_list:
                mPresenter.onListSelectedFromMenu();
                break;
            case R.id.action_favorites:
                mPresenter.onFavouritesSelectedFromMenu();
                break;
        }
        return true;
    }

    @Override
    public void displayStationOnMap(Station s) {
        highLightMap();
        MapFragment f = (MapFragment) getSupportFragmentManager().findFragmentByTag(MAP_TAG);
        f.onStationCenterRequested(s);
    }

    @Override
    public void highLightMap() {
        // there seem no clean way to set the item selected programmatically. This is a bad
        // hack to achieve that
        View view = mBottomNavigation.findViewById(R.id.action_map);
        view.performClick();
    }

    @Override
    public void onDisplayStationRequested(Station s) {
        mPresenter.onDisplayStationRequested(s);
    }

    @Override
    public void removeAds() {
        mAdView.setVisibility(GONE);
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (InAppPurchaseHelper.isAdsUnlocked(this) == InAppPurchaseHelper.AdsUnlocked.UNLOCKED) {
            return super.onCreateOptionsMenu(menu);
        }

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.global_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.main_action_remove_ads:
                mPurchaseHelper.unlockAds();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPurchaseHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void exit() {
        finish();
    }
}
