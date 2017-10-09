package com.whiterabbit.pisabike.screens.list

import android.location.Location
import com.whiterabbit.pisabike.model.Station
import com.whiterabbit.pisabike.schedule.SchedulersProvider
import com.whiterabbit.pisabike.storage.BikesRepository
import com.whiterabbit.pisabike.storage.PrefsStorage
import pl.charmas.android.reactivelocation.ReactiveLocationProvider
import rx.Observable
import rx.subscriptions.CompositeSubscription

class StationsFavsPresenterImpl(val repository: BikesRepository,
                                val schedulers : SchedulersProvider,
                                val locationProvider : ReactiveLocationProvider,
                                val storage: PrefsStorage) : StationsFavsPresenter {

    var data : ListData? = null
    lateinit var subscription : CompositeSubscription
    var view : StationsFavsView? = null

    data class ListData(var list : List<Station>,
                   var location : Location)

    override fun attachToView(v: StationsFavsView) {
        view = v
        subscription = CompositeSubscription()

        val favsStations = repository.stationsObservables.map({ stations ->
            stations.filter { s -> s.favourite }})

        val sub = Observable.combineLatest(locationProvider.lastKnownLocation.take(1),
                                            favsStations,
                                            { l: Location, stations: List<Station> ->
                                                ListData(stations, l)
                                            }).subscribeOn(schedulers.provideBackgroundScheduler())
                                              .observeOn(schedulers.provideMainThreadScheduler())
                                              .subscribe { d -> data = d
                                                           updateStations(d.list, d.location)
                                                         }

        subscription.add(sub)

        val sub1 = v.getStationSelectedObservable().subscribeOn(schedulers.provideMainThreadScheduler())
                .observeOn(schedulers.provideMainThreadScheduler())
                .subscribe { s ->  view?.displayStationOnMap(s)}
        subscription.add(sub1)

        val sub2 = v.preferredToggledObservable()
                .subscribeOn(schedulers.provideBackgroundScheduler())
                .flatMap { station -> repository.changePreferredStatus(station.name,
                                                                       storage.currentNetwork.network,
                                                                       !station.favourite) }
                .observeOn(schedulers.provideBackgroundScheduler())
                .subscribe({} ,
                        {_ : Throwable -> run {} })

        subscription.add(sub2)
    }

    fun updateStations(stations : List<Station>, location : Location) {
        view?.displayStations(stations, location)
        if (stations.isNotEmpty()) {
            view?.toggleListVisibility(true)
            view?.toggleEmptyListVisibility(false)
        } else {
            view?.toggleListVisibility(false)
            view?.toggleEmptyListVisibility(true)
        }
    }

    override fun detachFromView() {
        subscription.unsubscribe()
        view = null
    }
}


