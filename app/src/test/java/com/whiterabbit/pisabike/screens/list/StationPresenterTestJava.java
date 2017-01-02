package com.whiterabbit.pisabike.screens.list;


import android.location.Location;

import com.whiterabbit.pisabike.DataUtil;
import com.whiterabbit.pisabike.FakeSchedulersProvider;
import com.whiterabbit.pisabike.model.Station;
import com.whiterabbit.pisabike.schedule.SchedulersProvider;
import com.whiterabbit.pisabike.storage.BikesProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Observable;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class StationPresenterTestJava {

    @Mock
    ReactiveLocationProvider locationProvider;
    @Mock
    BikesProvider bikesProvider;
    @Mock
    StationsListView view;

    @Mock
    Location location;

    @Captor
    private ArgumentCaptor<List<Station>> stationsCaptor;

    SchedulersProvider mSchedulers;

    DataUtil dataUtil = new DataUtil("pisa_to_parse.html");

    StationsListPresenter mPresenter;

    @Before
    public void init() {
        mSchedulers = new FakeSchedulersProvider();
        mPresenter = new StationsListPresenterImpl(bikesProvider, mSchedulers, locationProvider);
    }


    @Test
    public void testSimpleAttach() {
        when(location.getLatitude()).thenReturn(43.722812);
        when(location.getLongitude()).thenReturn(10.395035);

        when(bikesProvider.getStationsObservables()).thenReturn(Observable.just(dataUtil.getTestStations()));
        when(locationProvider.getLastKnownLocation()).thenReturn(Observable.just(location));
        when(view.getStationSelectedObservable()).thenReturn(Observable.never());

        mPresenter.attachToView(view);

        ArgumentCaptor<Location> locationCaptor = ArgumentCaptor.forClass(Location.class);
        verify(view).displayStations(stationsCaptor.capture(), locationCaptor.capture());

        List<Station> stations = stationsCaptor.getValue();
        assertEquals(stations.get(0).getCity(), "PISA");
        assertEquals(stations.get(0).getName(), "Duomo");


    }
}
