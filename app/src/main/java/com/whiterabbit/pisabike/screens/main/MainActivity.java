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

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.whiterabbit.pisabike.PisaBikeApplication;
import com.whiterabbit.pisabike.R;
import com.whiterabbit.pisabike.model.Station;
import com.whiterabbit.pisabike.screens.list.StationsListFragment;
import com.whiterabbit.pisabike.screens.map.MapFragment;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MainView, BottomNavigationView.OnNavigationItemSelectedListener {
    private static final String MAP_TAG = "map";
    private static final String LIST_TAG = "list";

    @Inject
    MainPresenter mPresenter;

    @Bind(R.id.bottom_navigation)
    BottomNavigationView mBottomNavigation;

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
    }

    @Override
    public void onBackPressed() {
        if (mPresenter.onBackPressed()) {
            return;
        }

        super.onBackPressed();
    }

    @Override
    public void displayMap() {
        // if there is already
        if (getSupportFragmentManager().findFragmentByTag(MAP_TAG) != null) {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
            }
            return;
        }

        MapFragment fragment = MapFragment.createInstance();
        getSupportFragmentManager().beginTransaction()
                                    .add(R.id.main_activity_frame, fragment, MAP_TAG)
                                    .commit();
    }

    @Override
    public void displayList() {
        if (getSupportFragmentManager().findFragmentByTag(LIST_TAG) != null) {
            return;
        }

        StationsListFragment fragment = new StationsListFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.main_activity_frame, fragment, LIST_TAG)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean sendBackPressedToMap() {
        MapFragment main = (MapFragment) (getSupportFragmentManager().findFragmentById(R.id.main_activity_frame));
        return main != null && main.onBackPressed();

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
        }
        return true;
    }

    @Override
    public void displayStationOnMap(Station s) {
        // there seem no clean way to set the item selected programmatically. This is a bad
        // hack to achieve that
        View view = mBottomNavigation.findViewById(R.id.action_map);
        view.performClick();
        MapFragment f = (MapFragment) getSupportFragmentManager().findFragmentByTag(MAP_TAG);
        f.onStationCenterRequested(s);
    }

    @Override
    public void onDisplayStationRequested(Station s) {
        mPresenter.onDisplayStationRequested(s);
    }
}
