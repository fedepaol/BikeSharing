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

package com.whiterabbit.pisabike.screens.favs;


import com.whiterabbit.pisabike.schedule.SchedulersProvider;
import com.whiterabbit.pisabike.screens.list.StationsFavsPresenter;
import com.whiterabbit.pisabike.screens.list.StationsFavsPresenterImpl;
import com.whiterabbit.pisabike.storage.BikesRepository;
import com.whiterabbit.pisabike.storage.PrefsStorage;

import dagger.Module;
import dagger.Provides;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;

@Module
public class StationsFavsModule {
    @Provides
    public StationsFavsPresenter providePresenter(BikesRepository bikesRepository,
                                                  SchedulersProvider schedulers,
                                                  ReactiveLocationProvider location,
                                                  PrefsStorage storage){
        return new StationsFavsPresenterImpl(bikesRepository, schedulers, location, storage);
    }
}
