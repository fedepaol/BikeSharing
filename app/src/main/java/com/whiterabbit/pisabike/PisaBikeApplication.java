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

package com.whiterabbit.pisabike;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.evernote.android.job.JobManager;
import com.facebook.stetho.Stetho;
import com.whiterabbit.pisabike.inject.ApplicationComponent;
import com.whiterabbit.pisabike.inject.ApplicationModule;
import com.whiterabbit.pisabike.inject.DaggerApplicationComponent;
import com.whiterabbit.pisabike.model.BikesNetwork;
import com.whiterabbit.pisabike.model.Coordinates;
import com.whiterabbit.pisabike.screens.favs.StationsFavsModule;
import com.whiterabbit.pisabike.screens.list.StationsListModule;
import com.whiterabbit.pisabike.screens.main.MainModule;
import com.whiterabbit.pisabike.screens.main.MainView;
import com.whiterabbit.pisabike.screens.map.MapModule;
import com.whiterabbit.pisabike.screens.map.MapView;
import com.whiterabbit.pisabike.storage.AddressJobCreator;
import com.whiterabbit.pisabike.storage.PrefsStorage;

import javax.inject.Inject;

import io.fabric.sdk.android.Fabric;

public class PisaBikeApplication extends MultiDexApplication {
    private ApplicationComponent mComponent;
    @Inject AddressJobCreator mJobCreator;
    @Inject PrefsStorage mStorage;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        initComponent();
        mComponent.inject(this);
        JobManager.create(this).addJobCreator(mJobCreator);
    }

    ApplicationModule getApplicationModule() {
        return new ApplicationModule(this);
    }

    void initComponent() {
        mComponent = DaggerApplicationComponent.builder()
                .applicationModule(getApplicationModule())
                .build();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public ApplicationComponent getComponent() {
        return mComponent;
    }

    public MapModule getMapModule(MapView view) {
        return new MapModule(view);
    }

    public MainModule getMainModule(MainView view) {
        return new MainModule(view);
    }

    public StationsListModule getListModule() {
        return new StationsListModule();
    }

    public StationsFavsModule getFavsModule() {
        return new StationsFavsModule();
    }
}
