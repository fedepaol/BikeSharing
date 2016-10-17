package com.whiterabbit.pisabike.inject;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.whiterabbit.pisabike.PisaBikeApplication;
import com.whiterabbit.pisabike.apiclient.BikeRestClient;
import com.whiterabbit.pisabike.apiclient.LocalBikeClient;
import com.whiterabbit.pisabike.model.PisaBikeDbHelper;
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
    LocalBikeClient provideBikeClient() {
        return new LocalBikeClient();
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
    @Singleton
    BriteDatabase provideBrite(Context c, SchedulersProvider schedulers) {
        SqlBrite sqlBrite = SqlBrite.create();
        PisaBikeDbHelper helper = new PisaBikeDbHelper(c);
        return sqlBrite.wrapDatabaseHelper(helper.getDbHelper(),
                                            schedulers.provideBackgroundScheduler());
    }

    @Provides
    @Singleton
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
}
