package com.whiterabbit.pisabike;

import com.tbruyelle.rxpermissions.RxPermissions;
import com.whiterabbit.pisabike.model.BikesProvider;
import com.whiterabbit.pisabike.schedule.SchedulersProvider;
import com.whiterabbit.pisabike.screens.main.MainPresenter;
import com.whiterabbit.pisabike.screens.main.MainPresenterImpl;
import com.whiterabbit.pisabike.screens.main.MainView;

import org.junit.Before;
import org.junit.Test;

import pl.charmas.android.reactivelocation.ReactiveLocationProvider;

import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class PresenterUnitTest {
    MainPresenter toTest;
    MainView mockView;
    BikesProvider mockBikesProvider;
    ReactiveLocationProvider mockLocation;
    RxPermissions mockPermissions;

    @Before
    public void setup() {
        toTest = new MainPresenterImpl(mockView,
                new FakeSchedulersProvider(),
                mockBikesProvider,
                mockLocation,
                mockPermissions);
    }

    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }
}