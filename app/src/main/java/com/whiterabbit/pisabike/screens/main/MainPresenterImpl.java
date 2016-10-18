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

import android.Manifest;
import android.location.Location;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.whiterabbit.pisabike.Constants;
import com.whiterabbit.pisabike.model.BikesProvider;
import com.whiterabbit.pisabike.model.Station;
import com.whiterabbit.pisabike.schedule.SchedulersProvider;
import com.whiterabbit.pisabike.storage.PrefsStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Observable;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class MainPresenterImpl implements MainPresenter {

    private MainView mView;
    private SchedulersProvider mSchedulersProvider;
    private ReactiveLocationProvider mLocationProvider;
    private Map<String, Station> mStations;
    private CompositeSubscription mSubscription;
    private Location myLocation;
    private Location pisaLocation = new Location("FAKE");
    private RxPermissions mPermissions;
    BikesProvider mBikesProvider;
    private boolean hasPermission;
    private Station mSelectedStation;
    private PrefsStorage mStorage;
    private boolean mMovingToMarker;

    public MainPresenterImpl(SchedulersProvider schedulersProvider,
                             BikesProvider bikesProvider,
                             ReactiveLocationProvider locationProvider,
                             RxPermissions permissions,
                             PrefsStorage storage) {
        mSchedulersProvider = schedulersProvider;
        mLocationProvider = locationProvider;
        mBikesProvider = bikesProvider;
        mPermissions = permissions;
        pisaLocation.setLatitude(23.0);
        pisaLocation.setLongitude(24.0);
        mStations = new HashMap<>();
        mStorage = storage;
    }

    @Override
    public void onViewDetached() {
        if (mSubscription != null)
            mSubscription.unsubscribe();
    }

    @Override
    public void onViewAttached(MainView view, boolean isNew) {
        mView = view;
        mView.getMap();
    }

    private Subscription subscribeStations(boolean hasLocation) {

        return mBikesProvider.getStationsObservables()
                .subscribeOn(mSchedulersProvider.provideBackgroundScheduler())
                .observeOn(mSchedulersProvider.provideMainThreadScheduler())
                .subscribe(this::onStationsChanged);
    }

    private Subscription checkLocation() {
        LocationRequest request = LocationRequest.create() //standard GMS LocationRequest
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setInterval(2000);

        Location mCityLocation = new Location("LOCAL");
        mCityLocation.setLatitude(Constants.MY_LATITUDE);
        mCityLocation.setLongitude(Constants.MY_LONGITUDE);

        return Observable.just(mCityLocation).concatWith(
                mLocationProvider.getUpdatedLocation(request))
                .subscribe(this::onLocationChanged);
    }

    private Subscription centerLocation() {
        LocationRequest request = LocationRequest.create() //standard GMS LocationRequest
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setInterval(200)
                .setNumUpdates(1);

        return mLocationProvider.getUpdatedLocation(request)
                .timeout(4, TimeUnit.SECONDS)
                .first()
                .observeOn(mSchedulersProvider.provideMainThreadScheduler())
                .subscribe(l -> mView.centerCity(l.getLatitude(), l.getLongitude()),
                           e -> mView.centerCity(Constants.MY_LATITUDE, Constants.MY_LONGITUDE));

    }

    private long getNowSeconds() {
        return System.currentTimeMillis() / 1000;
    }

    private void askForUpdate() {
        mView.startUpdating();
        final Subscription sub =
                mBikesProvider.updateBikes().subscribeOn(mSchedulersProvider.provideBackgroundScheduler())
                        .observeOn(mSchedulersProvider.provideMainThreadScheduler())
                        .subscribe(s -> {
                                },
                                e -> mView.stopUpdatingError(),
                                () -> mView.stopUpdating());
        mSubscription.add(sub);
        mView.stopUpdating();
        mStorage.setLastUpdate(getNowSeconds());
    }

    private void askForUpdateIfNeeded() {
        if (mStorage.getLastUpdate() + 60 < getNowSeconds()) {
            askForUpdate();
        }
    }

    private void onStationsChanged(List<Station> stations) {
        for (Station s : stations) {
            Station s1 = mStations.get(s.getName());
            if (s1 == null) {
                mStations.put(s.getName(), s);
            } else {
                s1.setAvailable(s.getAvailable());
                s1.setBroken(s.getBroken());
                s1.setFree(s.getFree());
            }
        }
        mView.drawStationsOnMap(stations);
    }

    private void onLocationChanged(Location l) {
        myLocation = l;
    }

    @Override
    public void onMapReady(boolean mustCenter) {
        mSubscription = new CompositeSubscription();
        mPermissions.request(Manifest.permission.ACCESS_COARSE_LOCATION,
                             Manifest.permission.ACCESS_FINE_LOCATION).subscribe(
                granted -> {
                    hasPermission = granted;
                    if (granted) {
                        if (mustCenter) {
                            mSubscription.add(centerLocation());
                        }
                        mSubscription.add(checkLocation());

                        mView.enableMyLocation();
                    } else {
                        if (mustCenter) {
                            mView.centerCity(Constants.MY_LATITUDE, Constants.MY_LONGITUDE);
                        }
                    }
                    mSubscription.add(subscribeStations(false));
                }
        );

        askForUpdateIfNeeded();
    }

    @Override
    public void onStationClicked(String stationName) {
        Station s = mStations.get(stationName);
        mView.displayStationDetail(s, myLocation);
        mView.highLightStation(s);

        if (mSelectedStation != null && s != mSelectedStation) {
            mView.unHighLightStation(mSelectedStation);
        }
        mSelectedStation = s;
        mMovingToMarker = true;
        mView.centerMapToLocation(new LatLng(mSelectedStation.getLatitude(),
                         mSelectedStation.getLongitude()));
    }

    @Override
    public boolean hasLocationPermission() {
        return false;
    }

    private void disableDetail() {
        if (mSelectedStation != null) {
            mView.hideStationDetail();
            mView.unHighLightStation(mSelectedStation);
            mSelectedStation = null;
        }
    }

    @Override
    public void onCameraMoved() {
        if (!mMovingToMarker) {
            disableDetail();
        }
    }

    @Override
    public void onCameraIdle() {
        mMovingToMarker = false;
    }

    @Override
    public void onCenterLocationClicked() {
        if (myLocation != null) {
            mView.centerMapToLocation(new LatLng(myLocation.getLatitude(),
                                                 myLocation.getLongitude()));
        }
    }

    @Override
    public void onReloadAsked() {
        askForUpdate();
        mView.hideStationDetail();
    }

    @Override
    public void onNavigateClicked() {
        mView.navigateTo(mSelectedStation);
    }

    @Override
    public boolean onBackPressed() {
        if (mSelectedStation != null) {
            disableDetail();
            return true;
        }
        return false;
    }
}