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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Observable;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class StationPresenterTest {

    @Mock
    ReactiveLocationProvider locationProvider;
    @Mock
    BikesProvider bikesProvider;
    @Mock
    StationsListView view;

    @Mock
    Location duomoLocation;

    @Mock
    Location airportLocation;

    @Captor
    private ArgumentCaptor<List<Station>> stationsCaptor;

    SchedulersProvider mSchedulers;

    DataUtil dataUtil = new DataUtil("pisa_to_parse.html");

    StationsListPresenter mPresenter;

    @Before
    public void init() {
        mSchedulers = new FakeSchedulersProvider();
        mPresenter = new StationsListPresenterImpl(bikesProvider, mSchedulers, locationProvider);

        when(duomoLocation.getLatitude()).thenReturn(43.722812);
        when(duomoLocation.getLongitude()).thenReturn(10.395035);

        when(airportLocation.getLatitude()).thenReturn(43.700150);
        when(airportLocation.getLongitude()).thenReturn(10.402397);
    }


    @Test
    public void testSimpleAttach() {
        when(bikesProvider.getStationsObservables()).thenReturn(Observable.just(dataUtil.getTestStations()));

        when(locationProvider.getLastKnownLocation()).thenReturn(Observable.just(duomoLocation))
                                                     .thenReturn(Observable.just(airportLocation));
        when(view.getStationSelectedObservable()).thenReturn(Observable.never());

        mPresenter.attachToView(view);
        mPresenter.detachFromView();
        mPresenter.attachToView(view);

        ArgumentCaptor<Location> locationCaptor = ArgumentCaptor.forClass(Location.class);
        verify(view, times(2)).displayStations(stationsCaptor.capture(), locationCaptor.capture());

        List<Station> stations = stationsCaptor.getAllValues().get(0);
        assertEquals(stations.get(0).getCity(), "PISA");
        assertEquals(stations.get(0).getName(), "Duomo");

        stations = stationsCaptor.getAllValues().get(1);
        assertEquals(stations.get(0).getCity(), "PISA");
        assertEquals(stations.get(0).getName(), "Aeroporto");
    }

    @Test
    public void testUpdate() {
        when(bikesProvider.getStationsObservables()).thenReturn(Observable.just(dataUtil.getTestStations()));
        when(bikesProvider.updateBikes()).thenReturn(Observable.just(null));
        when(locationProvider.getLastKnownLocation()).thenReturn(Observable.just(duomoLocation));

        when(view.getStationSelectedObservable()).thenReturn(Observable.never());

        mPresenter.attachToView(view);
        mPresenter.onUpdateRequested();
        verify(view).toggleLoading(true);
        verify(view).toggleLoading(false);
    }

    @Test
    public void testTwoEmissions() {
        List<Station> stations = dataUtil.getTestStations();
        List<Station> stations1 = new ArrayList<>(stations);

        stations1.get(0).setName("Duomo 1");

        List<List<Station>> results = new ArrayList<>(2);
        results.add(stations);
        results.add(stations1);

        when(bikesProvider.getStationsObservables()).thenReturn(Observable.from(results));

        when(locationProvider.getLastKnownLocation()).thenReturn(Observable.just(duomoLocation))
                .thenReturn(Observable.just(airportLocation));
        when(view.getStationSelectedObservable()).thenReturn(Observable.never());

        mPresenter.attachToView(view);

        ArgumentCaptor<Location> locationCaptor = ArgumentCaptor.forClass(Location.class);
        verify(view, times(2)).displayStations(stationsCaptor.capture(), locationCaptor.capture());

        List<Station> result = stationsCaptor.getAllValues().get(0);
        assertEquals(stations.get(0).getCity(), "PISA");
        assertEquals(stations.get(0).getName(), "Duomo");

        stations = stationsCaptor.getAllValues().get(1);
        assertEquals(stations.get(0).getCity(), "PISA");
        assertEquals(stations.get(0).getName(), "Duomo1");
    }



}
