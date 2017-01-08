package com.whiterabbit.pisabike.screens;

import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.whiterabbit.pisabike.TestApplication;
import com.whiterabbit.pisabike.schedule.SchedulersProvider;
import com.whiterabbit.pisabike.screens.list.StationsListModule;
import com.whiterabbit.pisabike.screens.list.StationsListPresenter;
import com.whiterabbit.pisabike.screens.main.MainActivity;
import com.whiterabbit.pisabike.storage.BikesProvider;

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import pl.charmas.android.reactivelocation.ReactiveLocationProvider;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class StationsListTest {
    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class, true, false);

    @Mock
    StationsListPresenter mockPresenter;

    @Before
    public void setUp() throws Exception {

        StationsListModule m = mock(StationsListModule.class);
        when(m.providePresenter(any(BikesProvider.class),
                                any(SchedulersProvider.class),
                                any(ReactiveLocationProvider.class))).thenReturn(mockPresenter);

        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        TestApplication app
                = (TestApplication) instrumentation.getTargetContext().getApplicationContext();

        app.setStationListModule(m);
        activityTestRule.launchActivity(new Intent());
    }



}
