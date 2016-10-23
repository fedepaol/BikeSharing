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

import android.database.Cursor;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;
import com.whiterabbit.pisabike.apiclient.HtmlBikeClient;
import com.whiterabbit.pisabike.model.Station;
import com.whiterabbit.pisabike.schedule.SchedulersProvider;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.subjects.BehaviorSubject;

public class BikesProvider {
    @Inject BriteDatabase mBrite;
    @Inject SchedulersProvider mSchedulersProvider;
    @Inject
    HtmlBikeClient mBikeClient;

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
