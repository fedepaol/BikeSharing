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
    lateinit var subscription : CompositeSubscription
    var view : StationsListView? = null

    data class ListData(var list : List<Station>,
                   var location : Location)

    override fun attachToView(v: StationsListView) {
        view = v
        subscription = CompositeSubscription()
        val sub =
                Observable.zip(locationProvider.lastKnownLocation.take(1),
                               provider.stationsObservables,
                               {l : Location, stations : List<Station> ->
                                                   ListData(stations, l)})

                                    .subscribeOn(schedulers.provideBackgroundScheduler())
                                    .observeOn(schedulers.provideMainThreadScheduler())


                                    .subscribe { d -> data = d
                                                      onStationsUpdate(d.list) }

        subscription.add(sub)

        val sub1 = v.getStationSelectedObservable().subscribeOn(schedulers.provideMainThreadScheduler())
                .observeOn(schedulers.provideMainThreadScheduler())
                .subscribe { s ->  view?.displayStationOnMap(s)}
        subscription.add(sub1)
    }

    override fun detachFromView() {
        subscription.unsubscribe()
    }

    fun onStationsUpdate(stations : List<Station>) {
        val toDisplay = stations.sortedWith(compareBy { it.getDistanceFrom(data?.location) })
        view?.displayStations(toDisplay, data?.location)
    }

    override fun onUpdateRequested() {
        view?.toggleLoading(true)
        val sub = provider.updateBikes().subscribeOn(schedulers.provideBackgroundScheduler())
                              .observeOn(schedulers.provideMainThreadScheduler())
                              .subscribe({ s -> },
                                         { view?.displayUpdateError() },
                                         { view?.toggleLoading(false) })
        subscription.add(sub)
    }
}


