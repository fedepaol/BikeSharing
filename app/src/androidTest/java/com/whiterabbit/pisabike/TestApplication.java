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

package com.whiterabbit.pisabike;


import com.whiterabbit.pisabike.inject.ApplicationModule;
import com.whiterabbit.pisabike.screens.list.StationsListModule;
import com.whiterabbit.pisabike.screens.main.MainModule;
import com.whiterabbit.pisabike.screens.main.MainView;

public class TestApplication extends PisaBikeApplication {
    private MainModule mMainModule;
    private ApplicationModule mApplicationModule;
    private StationsListModule mStationListModule;

    // By usint this two method we can drive whatever module we want during the tests
    // (and with that, drive what classes inject)
    @Override
    public MainModule getMainModule(MainView view) {
        if (mMainModule == null)
            return super.getMainModule(view);
        return mMainModule;
    }

    @Override
    ApplicationModule getApplicationModule() {
        if (mApplicationModule == null)
            return super.getApplicationModule();
        return mApplicationModule;
    }

    @Override
    public StationsListModule getListModule() {
        if (mStationListModule == null)
            return super.getListModule();
        return mStationListModule;
    }

    public void setApplicationModule(ApplicationModule m) {
        mApplicationModule = m;
    }

    public void setMainModule(MainModule m) {
        mMainModule = m;
    }

    public void setStationListModule(StationsListModule module) {
        this.mStationListModule = module;
    }
}
