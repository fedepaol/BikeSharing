package com.whiterabbit.pisabike;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.whiterabbit.pisabike.inject.ApplicationComponent;
import com.whiterabbit.pisabike.inject.ApplicationModule;
import com.whiterabbit.pisabike.inject.DaggerApplicationComponent;
import com.whiterabbit.pisabike.screens.main.MainModule;
import com.whiterabbit.pisabike.screens.main.MainView;
import io.fabric.sdk.android.Fabric;

public class PisaBikeApplication extends Application {
    private ApplicationComponent mComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        initComponent();
        Stetho.initializeWithDefaults(this);
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

    public MainModule getMainModule(MainView view) {
        return new MainModule(view);
    }
}
