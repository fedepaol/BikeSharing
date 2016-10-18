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

package com.whiterabbit.pisabike.inject;

/**
 * Created by fedepaol on 28/06/15.
 */

import android.content.Context;
import android.content.SharedPreferences;

import com.squareup.sqlbrite.BriteDatabase;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.whiterabbit.pisabike.PisaBikeApplication;
import com.whiterabbit.pisabike.apiclient.BikeRestClient;
import com.whiterabbit.pisabike.apiclient.LocalBikeClient;
import com.whiterabbit.pisabike.schedule.SchedulersProvider;
import com.whiterabbit.pisabike.storage.PrefsStorage;
import com.whiterabbit.pisabike.ui.MapMarkerFactory;

import javax.inject.Singleton;

import dagger.Component;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;

@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {
    void inject(PisaBikeApplication app);

    SharedPreferences getSharedPrefs();
    LocalBikeClient getBikeClient();
    ReactiveLocationProvider getReactiveLocationProvider();
    Context getContext();
    SchedulersProvider getSchedulers();
    BriteDatabase getBriteDatabase();
    RxPermissions getPermissions();
    MapMarkerFactory getMarkerFactory();
    PrefsStorage getStorage();
}


