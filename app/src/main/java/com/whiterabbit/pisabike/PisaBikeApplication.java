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

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.whiterabbit.pisabike.inject.ApplicationComponent;
import com.whiterabbit.pisabike.inject.ApplicationModule;
import com.whiterabbit.pisabike.inject.DaggerApplicationComponent;
import com.whiterabbit.pisabike.screens.map.MapModule;
import com.whiterabbit.pisabike.screens.map.MapView;

import io.fabric.sdk.android.Fabric;

public class PisaBikeApplication extends Application {
    private ApplicationComponent mComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        initComponent();
    }

    ApplicationModule getApplicationModule() {
        return new ApplicationModule(this);
    }

    void initComponent() {
        mComponent = DaggerApplicationComponent.builder()
                .applicationModule(getApplicationModule())
                .build();
    }

    public ApplicationComponent getComponent() {
        return mComponent;
    }

    public MapModule getMainModule(MapView view) {
        return new MapModule(view);
    }
}
