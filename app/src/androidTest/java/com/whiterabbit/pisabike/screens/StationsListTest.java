package com.whiterabbit.pisabike.screens;

import android.app.Instrumentation;
import android.content.Intent;
import android.location.Location;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;

import com.whiterabbit.pisabike.R;
import com.whiterabbit.pisabike.TestApplication;
import com.whiterabbit.pisabike.schedule.SchedulersProvider;
import com.whiterabbit.pisabike.screens.list.StationsListModule;
import com.whiterabbit.pisabike.screens.list.StationsListPresenter;
import com.whiterabbit.pisabike.screens.list.StationsListView;
import com.whiterabbit.pisabike.screens.main.MainActivity;
import com.whiterabbit.pisabike.storage.BikesProvider;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import com.whiterabbit.pisabike.FakeStationsProvider;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class StationsListTest {
    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class, true, false);

    StationsListPresenter mockPresenter;

    Location myLocation;

    StationsListView listView;

    FakeStationsProvider stationsProvider;

    @Before
    public void setUp() throws Exception {
        stationsProvider = new FakeStationsProvider();
        StationsListModule m = mock(StationsListModule.class);
        myLocation = mock(Location.class);
        mockPresenter = mock(StationsListPresenter.class);
        when(m.providePresenter(any(BikesProvider.class),
                                any(SchedulersProvider.class),
                                any(ReactiveLocationProvider.class))).thenReturn(mockPresenter);

        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        TestApplication app
                = (TestApplication) instrumentation.getTargetContext().getApplicationContext();

        app.setStationListModule(m);

    }

    @Test
    public void testStationsList() {
        activityTestRule.launchActivity(new Intent());
        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.action_list), isDisplayed()));
        bottomNavigationItemView.perform(click());

        listView = (StationsListView) activityTestRule.getActivity().getSupportFragmentManager().findFragmentByTag("list");

        InstrumentationRegistry.getInstrumentation().runOnMainSync(() ->
                listView.displayStations(stationsProvider.getList1(), myLocation));

        onView(nthChildOf(withId(R.id.stations_list_view), 0))
                .check(matches(hasDescendant(withText("Duomo"))));
        onView(nthChildOf(withId(R.id.stations_list_view), 1))
                .check(matches(hasDescendant(withText("Aereoporto"))));
    }

    public static Matcher<View> nthChildOf(final Matcher<View> parentMatcher, final int childPosition) {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("with "+childPosition+" child view of type parentMatcher");
            }

            @Override
            public boolean matchesSafely(View view) {
                if (!(view.getParent() instanceof ViewGroup)) {
                    return parentMatcher.matches(view.getParent());
                }

                ViewGroup group = (ViewGroup) view.getParent();
                return parentMatcher.matches(view.getParent()) && group.getChildAt(childPosition).equals(view);
            }
        };
    }



}
