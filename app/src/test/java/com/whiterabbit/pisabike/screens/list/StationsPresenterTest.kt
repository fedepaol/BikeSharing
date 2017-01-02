package com.whiterabbit.pisabike.screens.list

import android.location.Location
import com.whiterabbit.pisabike.DataUtil
import com.whiterabbit.pisabike.FakeSchedulersProvider
import com.whiterabbit.pisabike.model.Station
import com.whiterabbit.pisabike.storage.BikesProvider
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito.*
import pl.charmas.android.reactivelocation.ReactiveLocationProvider
import rx.Observable


class StationsPresenterTest() {
    val mockLocationProvider = mock(ReactiveLocationProvider::class.java)
    val mockProvider = mock(BikesProvider::class.java)
    val mockView = mock(StationsListView::class.java)
    val dataUtil = DataUtil("pisa_to_parse.html")

    var presenter : StationsListPresenter? = null
    val location : Location = mock(Location::class.java)

    @Captor lateinit
    var stationsCaptor : ArgumentCaptor<List<Station>>

    @Before
    fun setup() {
        val scheduler = FakeSchedulersProvider()
        presenter = StationsListPresenterImpl(mockProvider, scheduler, mockLocationProvider)
    }

    @Test
    fun testSimpleAttach() {
        `when`(location.latitude).thenReturn(23.0)
        `when`(location.longitude).thenReturn(23.0)
        `when`(mockProvider.stationsObservables).thenReturn(Observable.just(dataUtil.testStations))
        `when`(mockLocationProvider.lastKnownLocation).thenReturn(Observable.just(location))

        presenter?.attachToView(mockView)


        val locationCaptor = ArgumentCaptor.forClass(Location::class.java)
        verify(mockView).displayStations(stationsCaptor.capture(), locationCaptor.capture())

        val l = stationsCaptor.value
        assertEquals(l[0].city, "Pisa")

    }

}
