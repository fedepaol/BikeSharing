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

import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;

import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.whiterabbit.pisabike.screens.main.MainActivity;
import com.whiterabbit.pisabike.screens.main.MainModule;
import com.whiterabbit.pisabike.screens.main.MainPresenter;
import com.whiterabbit.pisabike.screens.main.MainView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    private MainPresenter mMockPresenter;

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class, true, false);

    @Before
    public void setUp() throws Exception {

        // a mock module with the mock presenter to be injected..
        MainModule m = mock(MainModule.class);
        mMockPresenter = mock(MainPresenter.class);

        when(m.provideMainView()).thenReturn(mock(MainView.class)); // this is needed to fool dagger
        when(m.provideMainPresenter()).thenReturn(mMockPresenter); // this is needed to fool dagger

        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        TestApplication app
                = (TestApplication) instrumentation.getTargetContext().getApplicationContext();

        // forced to the application object
        app.setMainModule(m);
        activityTestRule.launchActivity(new Intent());
    }

    @Test
    public void testClickDisplayMap() {
        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.action_map), isDisplayed()));
        bottomNavigationItemView.perform(click());

        verify(mMockPresenter).onMapSelectedFromMenu();
    }

    @Test
    public void testClickDisplayList() {
        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.action_list), isDisplayed()));
        bottomNavigationItemView.perform(click());

        verify(mMockPresenter).onListSelectedFromMenu();
    }

    @Test
    public void testDisplayMapThenList() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync( () ->
                activityTestRule.getActivity().displayMap());

        InstrumentationRegistry.getInstrumentation().runOnMainSync( () ->
                activityTestRule.getActivity().displayList());

        onView(withId(R.id.stations_list_view)).check(matches(isDisplayed()));
    }

}
