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


