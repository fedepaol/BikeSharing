package com.whiterabbit.pisabike.inject;

import android.app.Application;
import android.content.Context;

import com.squareup.sqlbrite.BriteDatabase;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.whiterabbit.pisabike.apiclient.BikeRestClient;
import com.whiterabbit.pisabike.apiclient.HtmlBikeClient;
import com.whiterabbit.pisabike.schedule.SchedulersProvider;
import com.whiterabbit.pisabike.storage.PrefsStorage;

import mockdata.FakeSchedulersProvider;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;


public class MockApplicationModule extends ApplicationModule {
    public MockApplicationModule(Application app) {
        super(app);
    }

    PrefsStorage storage;
    HtmlBikeClient bikeClient;
    RxPermissions permissions;
    ReactiveLocationProvider locationProvider;


    @Override
    ReactiveLocationProvider provideLocation() {
        return locationProvider;
    }

    @Override
    SchedulersProvider provideSchedulers() {
        return new FakeSchedulersProvider();
    }

    @Override
    BriteDatabase provideBrite(Context c, SchedulersProvider schedulers) {
        return super.provideBrite(c, schedulers);
    }

    @Override
    RxPermissions provideRxPermissions(Context c) {
        return permissions;
    }

    @Override
    PrefsStorage provideStorage() {
        return storage;
    }

    public void setStorage(PrefsStorage storage) {
        this.storage = storage;
    }

    public void setBikeClient(HtmlBikeClient bikeClient) {
        this.bikeClient = bikeClient;
    }

    public void setPermissions(RxPermissions permissions) {
        this.permissions = permissions;
    }

    public void setLocationProvider(ReactiveLocationProvider locationProvider) {
        this.locationProvider = locationProvider;
    }
}
