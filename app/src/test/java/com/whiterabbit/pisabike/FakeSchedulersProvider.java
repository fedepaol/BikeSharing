package com.whiterabbit.pisabike;


import com.whiterabbit.pisabike.schedule.SchedulersProvider;

import rx.Scheduler;
import rx.schedulers.Schedulers;

public class FakeSchedulersProvider implements SchedulersProvider {
    @Override
    public Scheduler provideMainThreadScheduler() {
        return Schedulers.immediate();
    }

    @Override
    public Scheduler provideBackgroundScheduler() {
        return Schedulers.immediate();
    }
}
