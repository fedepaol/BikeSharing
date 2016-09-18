package com.whiterabbit.pisabike.screens.main;

import android.database.Cursor;
import android.location.Location;

import com.google.android.gms.location.LocationRequest;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;
import com.whiterabbit.pisabike.apiclient.BikeRestClient;
import com.whiterabbit.pisabike.model.PisaBikeDbHelper;
import com.whiterabbit.pisabike.model.Station;
import com.whiterabbit.pisabike.schedule.SchedulersProvider;

import java.util.List;
import java.util.concurrent.TimeUnit;

import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Observable;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class MainPresenterImpl implements MainPresenter {
    private MainView mView;
    private SchedulersProvider mSchedulersProvider;
    private BikeRestClient mBikeClient;
    private ReactiveLocationProvider mLocationProvider;
    private BriteDatabase mBrite;
    private List<Station> mStations;
    private CompositeSubscription mSubscription;
    private Location myLocation;

    public MainPresenterImpl(MainView view,
                             BriteDatabase brite,
                             SchedulersProvider schedulersProvider,
                             BikeRestClient bikeClient,
                             ReactiveLocationProvider locationProvider) {
        mView = view;
        mBrite = brite;
        mSchedulersProvider = schedulersProvider;
        mLocationProvider = locationProvider;
        mBikeClient = bikeClient;
    }

    @Override
    public void onPause() {
        mSubscription.unsubscribe();
    }

    @Override
    public void onResume() {
        mSubscription = new CompositeSubscription();
        mSubscription.add(subscribeToBrite());
        mSubscription.add(checkLocation());
        mSubscription.add(updateFromServer());
    }

    private Subscription updateFromServer() {
        return Observable.interval(3, TimeUnit.MINUTES)
                .flatMap(i -> mBikeClient.getStations())
                .subscribeOn(mSchedulersProvider.provideBackgroundScheduler())
                .observeOn(mSchedulersProvider.provideBackgroundScheduler())
                .subscribe(l -> {
                    BriteDatabase.Transaction t = mBrite.newTransaction();
                    mBrite.delete(PisaBikeDbHelper.STATION_TABLE, null);
                    for (Station s : l) {
                        mBrite.insert(PisaBikeDbHelper.STATION_TABLE, s.getContentValues());
                    }
                    t.markSuccessful();
                });
    }

    private Subscription subscribeToBrite() {
        Observable<SqlBrite.Query> users = mBrite.createQuery(PisaBikeDbHelper.STATION_TABLE, "SELECT * " +
                                                                            "FROM STATION_TABLE");
        return users.map(q -> {
            Cursor cursor = q.run();
            Station s[] = new Station[cursor.getCount()];
            int i = 0;
            while (cursor.moveToNext()) {
                s[i++] = new Station(cursor);
            }
            return s;
        }).flatMap(Observable::from)
                .toList()
                .flatMap(l -> {
                    LocationRequest request = LocationRequest.create() //standard GMS LocationRequest
                            .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                            .setNumUpdates(1)
                            .setInterval(100);
                    return mLocationProvider.getUpdatedLocation(request).map(location ->
                                                                         sortNearbyStations(l, location));

                })
                .subscribeOn(mSchedulersProvider.provideBackgroundScheduler())
                .observeOn(mSchedulersProvider.provideMainThreadScheduler())
                .subscribe(this::onStationsChanged);
    }

    private List<Station> sortNearbyStations(List<Station> stations, Location l) {
        return stations;
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

    private void onStationsChanged(List<Station> stations) {
        mStations = stations;
        if (stations.size() == 0) {
            // TODO SHOW progress dialog.
            // It's the first time
            return;
        }
    }

    private void onLocationChanged(Location l) {
        mView.updateMyLocation(l);
    }

    @Override
    public void centerPressed() {
        if (myLocation != null) {
            mView.centerMapToLocation(myLocation);
        }
    }
}