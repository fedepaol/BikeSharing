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

import android.content.Context;

import com.whiterabbit.pisabike.R;
import com.whiterabbit.pisabike.apiclient.BikeRestClient;
import com.whiterabbit.pisabike.model.BikesNetwork;
import com.whiterabbit.pisabike.model.Station;
import com.whiterabbit.pisabike.schedule.SchedulersProvider;

import java.util.List;

import javax.inject.Inject;

import hu.akarnokd.rxjava.interop.RxJavaInterop;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Observable;
import rx.subjects.BehaviorSubject;

public class BikesRepository {
    @Inject SchedulersProvider mSchedulersProvider;
    @Inject BikeRestClient mBikeClient;
    @Inject PrefsStorage mPrefsStorage;
    @Inject ReactiveLocationProvider mProvider;
    @Inject Context mContext;
    @Inject BikesDatabasee mBikesDatabase;


    @Inject
    public BikesRepository() {

    }

    private long getNowSeconds() {
        return System.currentTimeMillis() / 1000;
    }

    public Observable<Void> updateBikes(String network) {
        if (mPrefsStorage.getLastUpdate() + 60 > getNowSeconds()) {
            return Observable.just(null);
        }

        BehaviorSubject<Void> requestSubject = BehaviorSubject.create();

        mBikeClient.getStations(network)
                .subscribeOn(mSchedulersProvider.provideBackgroundScheduler())
                .observeOn(mSchedulersProvider.provideBackgroundScheduler())
                .subscribe(stations -> {
                    boolean needsAddress = false;
                    for (Station s : stations) {
                        if (mBikesDatabase.bikesDao().getStation(s.getName(), s.getNetwork()) != null) {
                            mBikesDatabase.bikesDao().updateStation(s);
                        } else {
                            s.setAddress(mContext.getString(R.string.loading_address));
                            mBikesDatabase.bikesDao().insertStation(s);
                            needsAddress = true;
                        }
                    }

                    if (needsAddress) {
                        AddressJobKt.scheduleAddressJob();
                    }

                    requestSubject.onCompleted();
                    mPrefsStorage.setLastUpdate(getNowSeconds());
                }, requestSubject::onError);

        return requestSubject;
    }

    public Observable<Void> updateNetworks() {
        if (mPrefsStorage.getLastNetworkUpdate() + 60 > getNowSeconds()) {
            return Observable.just(null);
        }
        BehaviorSubject<Void> requestSubject = BehaviorSubject.create();
        mBikeClient.getNetworks()
                .subscribeOn(mSchedulersProvider.provideBackgroundScheduler())
                .observeOn(mSchedulersProvider.provideBackgroundScheduler())
                .subscribe(networks -> mBikesDatabase.bikesDao().insertAllNetworks(networks),
                           requestSubject::onError,
                        () -> {
                            mPrefsStorage.setLastNetworkUpdate(getNowSeconds());
                            requestSubject.onCompleted();
                        });

        return requestSubject;
    }

    public Observable<List<Station>> getStationsObservables() {
        return RxJavaInterop.toV1Observable(mBikesDatabase.bikesDao().loadAllStations(mPrefsStorage.getCurrentNetwork().getNetwork()));
    }

    private int setStationPreferred(String stationName, String stationCity, boolean preferred) {
        mBikesDatabase.bikesDao().setStationPreferred(stationName, stationCity, preferred);
        return 0; // Completable?
    }

    public Observable<Integer> changePreferredStatus(String stationName, String stationCity, boolean preferred) {
        return Observable.fromCallable(() -> setStationPreferred(stationName, stationCity, preferred));
    }

    public Observable<List<BikesNetwork>> getNetworksObservable() {
        return RxJavaInterop.toV1Observable(mBikesDatabase.bikesDao().loadAllNetworks());
    }
}
