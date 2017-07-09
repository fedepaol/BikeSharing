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

package com.whiterabbit.pisabike.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.location.Address;
import android.util.Log;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;
import com.whiterabbit.pisabike.R;
import com.whiterabbit.pisabike.apiclient.BikeRestClient;
import com.whiterabbit.pisabike.apiclient.HtmlBikeClient;
import com.whiterabbit.pisabike.model.Station;
import com.whiterabbit.pisabike.schedule.SchedulersProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Observable;
import rx.subjects.BehaviorSubject;

public class BikesProvider {
    @Inject BriteDatabase mBrite;
    @Inject SchedulersProvider mSchedulersProvider;
    @Inject BikeRestClient mBikeClient;
    @Inject PrefsStorage mPrefsStorage;
    @Inject ReactiveLocationProvider mProvider;
    @Inject Context mContext;


    @Inject
    public BikesProvider() {

    }

    private long getNowSeconds() {
        return System.currentTimeMillis() / 1000;
    }

    private void storeAddress(double lat, double lon, String address) {
        mPrefsStorage.setAddressForLocation(lat, lon, address);
    }

    public Observable<Void> updateBikes() {
        if (mPrefsStorage.getLastUpdate() + 60 > getNowSeconds()) {
            return Observable.just(null);
        }

        mPrefsStorage.setLastUpdate(getNowSeconds());

        BehaviorSubject<Void> requestSubject = BehaviorSubject.create();

        mBikeClient.getStations()
                .subscribeOn(mSchedulersProvider.provideBackgroundScheduler())
                .observeOn(mSchedulersProvider.provideBackgroundScheduler())
                .subscribe(stations -> {
                    BriteDatabase.Transaction t = mBrite.newTransaction();
                    boolean needsAddress = false;
                    try {
                        for (Station s : stations.getStations()) {
                            int updated = mBrite.update(PisaBikeDbHelper.STATION_TABLE, s.getUpdateValues(),
                                    PisaBikeDbHelper.STATION_NAME_COLUMN + " = ?", s.getName());
                            if (updated == 0) {
                                needsAddress = true;
                                s.setAddress(mContext.getString(R.string.loading_address));
                                mBrite.insert(PisaBikeDbHelper.STATION_TABLE, s.getContentValues());
                            }
                        }

                        t.markSuccessful();
                    } finally {
                        t.end();
                    }

                    if (needsAddress) {
                        AddressJobKt.scheduleAddressJob();
                    }

                    requestSubject.onCompleted();
                }, requestSubject::onError);

        return requestSubject;
    }

    public Observable<List<Station>> getStationsObservables() {
        Observable<SqlBrite.Query> stations = mBrite.createQuery(PisaBikeDbHelper.STATION_TABLE, "SELECT * " +
                "FROM Station");
        return stations.map(q -> {
            boolean needsToLoadAddresses = false;
            Cursor cursor = q.run();
            List<Station> s = new ArrayList<>(cursor.getCount());
            while (cursor.moveToNext()) {
                Station station = new Station(cursor);
                s.add(station);
                if (!station.isAddressLoaded()) {
                    needsToLoadAddresses = true;
                }
            }
            cursor.close();
            if (needsToLoadAddresses) {
                AddressJobKt.scheduleAddressJob();
            }
            return s;
        });
    }

    private int setStationPreferred(String stationName, boolean preferred) {
        ContentValues cv = new ContentValues();
        cv.put(PisaBikeDbHelper.STATION_FAVORITE_COLUMN, preferred);
        return mBrite.update(PisaBikeDbHelper.STATION_TABLE,
                cv,
                PisaBikeDbHelper.STATION_NAME_COLUMN + " = ?",
                stationName);
    }

    public Observable<Integer> changePreferredStatus(String stationName, boolean preferred) {
        return Observable.fromCallable(() -> setStationPreferred(stationName, preferred));
    }

    public Observable<List<Station>> searchStationsObservable(String searchParam) {
        String likeArg = "%" + searchParam + "%";

        Observable<SqlBrite.Query> filteredStations = mBrite.createQuery(PisaBikeDbHelper.STATION_TABLE, "SELECT * " +
                "FROM Station where name like ? or address like ?", likeArg, likeArg);
        return filteredStations.map(q -> {
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
