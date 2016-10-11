package com.whiterabbit.pisabike.model;

import android.database.Cursor;

import com.google.android.gms.location.LocationRequest;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;
import com.whiterabbit.pisabike.apiclient.BikeRestClient;
import com.whiterabbit.pisabike.schedule.SchedulersProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

public class BikesProvider {
    @Inject BriteDatabase mBrite;
    @Inject SchedulersProvider mSchedulersProvider;
    @Inject BikeRestClient mBikeClient;

    @Inject
    public BikesProvider() {

    }

    public Observable<Void> updateBikes() {
        BehaviorSubject<Void> requestSubject = BehaviorSubject.create();

        mBikeClient.getStations()
                .subscribeOn(mSchedulersProvider.provideBackgroundScheduler())
                .observeOn(mSchedulersProvider.provideBackgroundScheduler())
                .subscribe(l -> {
                        BriteDatabase.Transaction t = mBrite.newTransaction();
                    try {
                        mBrite.delete(PisaBikeDbHelper.STATION_TABLE, null);
                        for (Station s : l) {
                            mBrite.insert(PisaBikeDbHelper.STATION_TABLE, s.getContentValues());
                        }

                        t.markSuccessful();
                    } finally {
                        t.end();
                    }

                    requestSubject.onCompleted();
                }, requestSubject::onError);

        return requestSubject;
    }

    /*
    public Subscription subscribeServerUpdates() {
        Observable<List<Station>> o =
                Observable.interval(3, TimeUnit.MINUTES).startWith(0L)
                        .flatMap(i -> mBikeClient.getStations());
        return updateServerSubscription(o);
    }*/

    public Observable<List<Station>> getStationsObservables() {
        Observable<SqlBrite.Query> users = mBrite.createQuery(PisaBikeDbHelper.STATION_TABLE, "SELECT * " +
                "FROM Station");
        return users.map(q -> {
            Cursor cursor = q.run();
            List<Station> s = new ArrayList<>(cursor.getCount());
            int i = 0;
            while (cursor.moveToNext()) {
                s.add(new Station(cursor));
            }
            return s;
        });
    }
}
