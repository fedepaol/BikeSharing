package mockdata;



import com.whiterabbit.pisabike.schedule.SchedulersProvider;

import rx.Scheduler;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

public class FakeSchedulersProvider implements SchedulersProvider {
    private TestScheduler testScheduler;

    public FakeSchedulersProvider() {
        testScheduler = new TestScheduler();
    }

    @Override
    public Scheduler provideMainThreadScheduler() {
        return Schedulers.immediate();
    }

    @Override
    public Scheduler provideBackgroundScheduler() {
        return Schedulers.immediate();
    }
}
