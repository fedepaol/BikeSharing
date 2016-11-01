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

/**
 * Created by fedepaol on 28/06/15.
 */


import com.whiterabbit.pisabike.inject.ActivityScope;
import com.whiterabbit.pisabike.inject.ApplicationComponent;
import com.whiterabbit.pisabike.screens.main.MainFragment;

import dagger.Component;

@ActivityScope
@Component(modules = {MainContainerModule.class},
           dependencies = {ApplicationComponent.class})
public interface MainContainerComponent {
    void inject(MainActivity m);
}

