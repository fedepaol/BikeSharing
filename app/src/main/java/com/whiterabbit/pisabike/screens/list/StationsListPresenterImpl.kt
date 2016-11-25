package com.whiterabbit.pisabike.screens.list

import android.location.Location
import com.whiterabbit.pisabike.model.Station
import com.whiterabbit.pisabike.schedule.SchedulersProvider
import com.whiterabbit.pisabike.storage.BikesProvider
import pl.charmas.android.reactivelocation.ReactiveLocationProvider
import rx.Observable
import rx.subscriptions.CompositeSubscription
import kotlin.comparisons.compareBy

class StationsListPresenterImpl(val provider : BikesProvider,
                                val schedulers : SchedulersProvider,
                                val locationProvider : ReactiveLocationProvider) : StationsListPresenter {

    var data : ListData? = null

    data class ListData(var list : List<Station>,
                   var location : Location)

    var subscription : CompositeSubscription? = null
    var view : StationsListView? = null

    override fun attachToView(v: StationsListView) {
        view = v
        subscription = CompositeSubscription()
        val sub =
                Observable.zip(locationProvider.lastKnownLocation,
                               provider.stationsObservables,
                               {l : Location, stations : List<Station> ->
                                                   ListData(stations, l)})

                                    .subscribeOn(schedulers.provideBackgroundScheduler())
                                    .observeOn(schedulers.provideMainThreadScheduler())


                                    .subscribe { d -> data = d
                                                      onStationsUpdate(d.list) }

        subscription?.add(sub)
    }

    override fun detachFromView() {
        subscription?.unsubscribe()
    }

    fun onStationsUpdate(stations : List<Station>) {
        var toDisplay = stations.sortedWith(compareBy { it.getDistanceFrom(data?.location) })
        view?.displayStations(toDisplay)
    }

    override fun onUpdateRequested() {
        val sub = provider.updateBikes().subscribeOn(schedulers.provideBackgroundScheduler())
                              .observeOn(schedulers.provideMainThreadScheduler())
                              .subscribe({ s -> {}},
                                         { view?.displayUpdateError() },
                                         { view?.toggleLoading(false) })
        subscription?.add(sub)
    }

    override fun onStationSelected(s: Station) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}


