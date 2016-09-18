package com.whiterabbit.pisabike.schedule;

import rx.Scheduler;

public interface SchedulersProvider {
    Scheduler provideMainThreadScheduler();

    Scheduler provideBackgroundScheduler();
}
