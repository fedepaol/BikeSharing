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

package com.whiterabbit.pisabike.screens.maincontainer;


import com.tbruyelle.rxpermissions.RxPermissions;
import com.whiterabbit.pisabike.schedule.SchedulersProvider;
import com.whiterabbit.pisabike.screens.main.MainPresenter;
import com.whiterabbit.pisabike.screens.main.MainPresenterImpl;
import com.whiterabbit.pisabike.screens.main.MainView;
import com.whiterabbit.pisabike.storage.BikesProvider;
import com.whiterabbit.pisabike.storage.PrefsStorage;

import dagger.Module;
import dagger.Provides;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;

@Module
public class MainContainerModule {
    private MainContainerView mView;
    public MainContainerModule(MainContainerView view) {
        mView = view;
    }

    @Provides
    public MainContainerView provideMainView() {
        return mView;
    }

    @Provides
    public MainContainerPresenter provideMainPresenter(){
        return new MainContainerPresenterImpl();
    }
}
