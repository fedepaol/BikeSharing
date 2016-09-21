package com.whiterabbit.pisabike.screens.main;

import android.location.Location;

import com.google.android.gms.location.LocationRequest;
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
    BikesProvider mBikesProvider;

    public MainPresenterImpl(MainView view,
                             SchedulersProvider schedulersProvider,
                             BikesProvider bikesProvider,
                             ReactiveLocationProvider locationProvider) {
        mView = view;
        mSchedulersProvider = schedulersProvider;
        mLocationProvider = locationProvider;
        mBikesProvider = bikesProvider;
    }

    @Override
    public void onPause() {
        mSubscription.unsubscribe();
    }

    @Override
    public void onResume() {
        mView.getMap();
    }

    private Subscription subscribeStations() {
        LocationRequest request = LocationRequest.create() //standard GMS LocationRequest
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setNumUpdates(1)
                .setInterval(100);

        return Observable.zip(mBikesProvider.getStationsObservables(),
                       mLocationProvider.getUpdatedLocation(request),
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
                .subscribeOn(mSchedulersProvider.provideBackgroundScheduler())
                .observeOn(mSchedulersProvider.provideMainThreadScheduler())
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
        mView.updateMyLocation(l);
    }

    @Override
    public void centerPressed() {
        if (myLocation != null) {
            mView.centerMapToLocation(myLocation);
        }
    }

    @Override
    public void onMapReady() {
        mSubscription = new CompositeSubscription();
        mSubscription.add(subscribeStations());
        mSubscription.add(checkLocation());
        askForUpdate();
    }
}