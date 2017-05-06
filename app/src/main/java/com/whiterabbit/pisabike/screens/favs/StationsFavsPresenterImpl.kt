package com.whiterabbit.pisabike.screens.list

import android.location.Location
import com.whiterabbit.pisabike.model.Station
import com.whiterabbit.pisabike.schedule.SchedulersProvider
import com.whiterabbit.pisabike.storage.BikesProvider
import pl.charmas.android.reactivelocation.ReactiveLocationProvider
import rx.Observable
import rx.subscriptions.CompositeSubscription
import java.util.concurrent.TimeUnit
import kotlin.comparisons.compareBy

class StationsFavsPresenterImpl(val provider : BikesProvider,
                                val schedulers : SchedulersProvider,
                                val locationProvider : ReactiveLocationProvider) : StationsFavsPresenter {

    var data : ListData? = null
    lateinit var subscription : CompositeSubscription
    var view : StationsFavsView? = null

    data class ListData(var list : List<Station>,
                   var location : Location)

    override fun attachToView(v: StationsFavsView) {
        view = v
        subscription = CompositeSubscription()

        val favsStations = provider.stationsObservables.map({stations ->
            stations.filter { s -> s.isFavourite }})

        val sub = Observable.combineLatest(locationProvider.lastKnownLocation.take(1),
                                            favsStations,
                                            { l: Location, stations: List<Station> ->
                                                ListData(stations, l)
                                            }).subscribe { d -> data = d
                                                          view?.displayStations(d.list, d.location) }

        subscription.add(sub)

        val sub2 = v.preferredToggledObservable()
                .subscribeOn(schedulers.provideBackgroundScheduler())
                .flatMap { station -> provider.changePreferredStatus(station.name, !station.isFavourite) }
                .observeOn(schedulers.provideBackgroundScheduler())
                .subscribe({} ,
                        {_ : Throwable -> run {} })

        subscription.add(sub2)
    }

    override fun detachFromView() {
        subscription.unsubscribe()
        view = null
    }

    override fun onUpdateRequested() {
        val sub = provider.updateBikes().subscribeOn(schedulers.provideBackgroundScheduler())
                              .observeOn(schedulers.provideMainThreadScheduler())
                              .subscribe()
        subscription.add(sub)
    }
}


