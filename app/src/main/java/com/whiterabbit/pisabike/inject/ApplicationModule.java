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

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.whiterabbit.helper.InterstitialHelper;
import com.whiterabbit.pisabike.R;
import com.whiterabbit.pisabike.apiclient.BikeRestClient;
import com.whiterabbit.pisabike.apiclient.GsonProviderKt;
import com.whiterabbit.pisabike.apiclient.HtmlBikeClient;
import com.whiterabbit.pisabike.storage.BikesDatabasee;
import com.whiterabbit.pisabike.storage.PisaBikeDbHelper;
import com.whiterabbit.pisabike.schedule.RealSchedulersProvider;
import com.whiterabbit.pisabike.schedule.SchedulersProvider;
import com.whiterabbit.pisabike.storage.PrefsStorage;
import com.whiterabbit.pisabike.ui.MapMarkerFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;

/**
 * Created by fedepaol on 28/06/15.
 */
@Module
public class ApplicationModule {
    private Application mApp;

    public ApplicationModule(Application app) {
        mApp = app;
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPrefs() {
        return PreferenceManager.getDefaultSharedPreferences(mApp);
    }

    @Provides
    @Singleton
    BikeRestClient provideBikeClient(Gson gson) {
        return new BikeRestClient(gson);
    }

    @Provides
    @Singleton
    ReactiveLocationProvider provideLocation() {
        return new ReactiveLocationProvider(mApp.getApplicationContext());
    }

    @Provides
    @Singleton
    Context provideContext() {
        return mApp.getApplicationContext();
    }

    @Provides
    @Singleton
    SchedulersProvider provideSchedulers() {
        return new RealSchedulersProvider();
    }

    @Provides
    RxPermissions provideRxPermissions(Context c) {
        return RxPermissions.getInstance(c);
    }

    @Provides
    @Singleton
    MapMarkerFactory providerMarkerFactory() {
        return new MapMarkerFactory();
    }

    @Provides
    @Singleton
    PrefsStorage provideStorage() {
        return new PrefsStorage(mApp.getApplicationContext());
    }

    @Provides
    @Singleton
    InterstitialHelper provideInterstitial() {
        return new InterstitialHelper(mApp.getApplicationContext(), 20, 6, mApp.getString(R.string.admob_interstitial), false);
    }

    @Provides
    @Singleton
    Gson provideGSon() {
        return GsonProviderKt.getGson();
    }

    @Provides
    @Singleton
    BikesDatabasee provideDatabase() {
        return Room.databaseBuilder(mApp.getApplicationContext(), BikesDatabasee.class, "bikesdb").build();
    }
}
