package com.whiterabbit.pisabike.screens.main;

import android.Manifest;
import android.content.Context;
import android.location.Location;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.whiterabbit.pisabike.model.BikesProvider;
import com.whiterabbit.pisabike.model.Station;
import com.whiterabbit.pisabike.schedule.SchedulersProvider;

import java.util.List;

import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Observable;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class MainPresenterImpl implements MainPresenter {
    private static class StationsLocation {
        private List<Station> stations;
        private Location location;

        public StationsLocation(List<Station> stations, Location location) {
            this.stations = stations;
            this.location = location;
        }

        public List<Station> getStations() {
            return stations;
        }

        public Location getLocation() {
            return location;
        }
    }

    private MainView mView;
    private SchedulersProvider mSchedulersProvider;
    private ReactiveLocationProvider mLocationProvider;
    private List<Station> mStations;
    private CompositeSubscription mSubscription;
    private Location myLocation;
    private Location pisaLocation = new Location("FAKE");
    private RxPermissions mPermissions;
    BikesProvider mBikesProvider;
    private boolean hasPermission;
    private Station mSelectedStation;

    public MainPresenterImpl(MainView view,
                             SchedulersProvider schedulersProvider,
                             BikesProvider bikesProvider,
                             ReactiveLocationProvider locationProvider,
                             RxPermissions permissions) {
        mView = view;
        mSchedulersProvider = schedulersProvider;
        mLocationProvider = locationProvider;
        mBikesProvider = bikesProvider;
        mPermissions = permissions;
        pisaLocation.setLatitude(23.0);
        pisaLocation.setLongitude(24.0);
    }

    @Override
    public void onPause() {
        if (mSubscription != null)
            mSubscription.unsubscribe();
    }

    @Override
    public void onResume() {
        mView.getMap();
    }

    private Subscription subscribeStations(boolean hasLocation) {
        LocationRequest request = LocationRequest.create() //standard GMS LocationRequest
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setNumUpdates(1)
                .setInterval(100);
        final Observable<Location> locationObservable =
                                        mLocationProvider.getUpdatedLocation(request).first()
                                        .compose(o -> hasLocation? o : Observable.just(pisaLocation));

        return Observable.zip(mBikesProvider.getStationsObservables(), locationObservable,
                                        StationsLocation::new)
                .subscribeOn(mSchedulersProvider.provideBackgroundScheduler())
                .observeOn(mSchedulersProvider.provideMainThreadScheduler())
                .subscribe(s -> this.onStationsChanged(s.getStations(), s.getLocation()));
    }

    private Subscription checkLocation() {
        LocationRequest request = LocationRequest.create() //standard GMS LocationRequest
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setInterval(2000);
        return mLocationProvider.getUpdatedLocation(request)
                .subscribe(this::onLocationChanged);
    }

    private void askForUpdate() {
        mView.startUpdating();
        final Subscription sub =
                mBikesProvider.updateBikes().subscribeOn(mSchedulersProvider.provideBackgroundScheduler())
                      .observeOn(mSchedulersProvider.provideMainThreadScheduler())
                      .subscribe(s -> {},
                                 e -> mView.stopUpdating(),
                                 () -> mView.stopUpdating());
        mSubscription.add(sub);
    }

    private void onStationsChanged(List<Station> stations, Location l) {
        mStations = stations;
        myLocation = l;
        mView.drawStationsOnMap(mStations);
    }

    private void onLocationChanged(Location l) {
        myLocation = l;
    }

    @Override
    public void onMapReady() {
        mSubscription = new CompositeSubscription();
        mPermissions.request(Manifest.permission.ACCESS_COARSE_LOCATION,
                             Manifest.permission.ACCESS_FINE_LOCATION).subscribe(
                granted -> {
                    hasPermission = granted;
                    if (granted) {
                        mSubscription.add(checkLocation());
                        mView.enableMyLocation();
                    }
                    mSubscription.add(subscribeStations(false));
                }
        );

        askForUpdate();
        mView.centerCity();
    }

    @Override
    public void onStationClicked(Station s) {
        // mView.centerMapToLocation(new LatLng(s.getLatitude(), s.getLongitude()));
        mView.displayStationDetail(s, myLocation);
        mView.highLightStation(s);
        if (mSelectedStation != null && s != mSelectedStation) {
            mView.unHighLightStation(mSelectedStation);
        }
        mSelectedStation = s;
    }

    @Override
    public boolean hasLocationPermission() {
        return false;
    }

    @Override
    public void onCameraMoved() {
        mView.hideStationDetail();
        if (mSelectedStation != null) {
            mView.unHighLightStation(mSelectedStation);
            mSelectedStation = null;
        }
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
}