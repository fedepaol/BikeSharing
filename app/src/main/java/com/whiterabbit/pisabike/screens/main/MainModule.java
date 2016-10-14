/*
 * Copyright (C) 2016  Federico Paolinelli.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.whiterabbit.pisabike.screens.main;


import com.tbruyelle.rxpermissions.RxPermissions;
import com.whiterabbit.pisabike.model.BikesProvider;
import com.whiterabbit.pisabike.schedule.SchedulersProvider;
import com.whiterabbit.pisabike.storage.PrefsStorage;

import dagger.Module;
import dagger.Provides;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;

@Module
public class MainModule {
    private MainView mView;
    public MainModule(MainView view) {
        mView = view;
    }

    @Provides
    public MainView provideMainView() {
        return mView;
    }

    @Provides
    public MainPresenter provideMainPresenter(MainView v,
                                              SchedulersProvider schedulersProvider,
                                              BikesProvider bikesProvider,
                                              ReactiveLocationProvider locationProvider,
                                              RxPermissions permissions,
                                              PrefsStorage storage){
        return new MainPresenterImpl(v, schedulersProvider, bikesProvider,
                                     locationProvider, permissions, storage);
    }
}
