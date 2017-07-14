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
        val sub = allStationsObservable().subscribe { d -> data = d
                                                      onStationsUpdate(d.list) }

        subscription.add(sub)

        val sub1 = v.getStationSelectedObservable().subscribeOn(schedulers.provideMainThreadScheduler())
                .observeOn(schedulers.provideMainThreadScheduler())
                .subscribe { s ->  view?.displayStationOnMap(s)}
        subscription.add(sub1)

        val sub2 = v.preferredToggledObservable()
                .subscribeOn(schedulers.provideBackgroundScheduler())
                .flatMap { station -> provider.changePreferredStatus(station.name, !station.isFavourite) }
                .observeOn(schedulers.provideBackgroundScheduler())
                .subscribe({} ,
                           {_ : Throwable -> run {} })

        subscription.add(sub2)
    }

    fun filterStation(s : Station, t : String) : Boolean {
        if (t.equals("")) {
            return true
        }

        val lowerName = s.name.toLowerCase()
        val lowerAddress = s.name.toLowerCase()
        val lowerText = t.toLowerCase()
        return lowerName.contains(lowerText) || lowerAddress.contains(lowerText)
    }

    private fun allStationsObservable() : Observable<ListData> {
        val stationsObservable = provider.stationsObservables
        val textObservable = Observable.just("")
                             .concatWith(view?.searchStationObservable()?.debounce(400, TimeUnit.MILLISECONDS))
        val filteredStations : Observable<List<Station>>

        if (textObservable != null) {
            filteredStations = Observable.combineLatest(stationsObservable,
                                                        textObservable,
                                                        { s: List<Station>, t: String -> Pair(s, t) })
                                .map { (first, second) -> first.filter { s -> filterStation(s, second) } }

        } else {
            filteredStations = Observable.just(emptyList())
        }

        return Observable.combineLatest(locationProvider.lastKnownLocation.take(1),
                                  filteredStations,
                                                    { l: Location, stations: List<Station> ->
                                                        ListData(stations, l)
                                                    })
    }

    override fun detachFromView() {
        subscription.unsubscribe()
        view = null
    }

    fun onStationsUpdate(stations : List<Station>) {
        val toDisplay = stations.sortedBy { it.getDistanceFrom(data?.location) }
        view?.displayStations(toDisplay, data?.location)
    }

    override fun onSearchButtonPressed() {
        view?.displaySearchBar(true)
        view?.displaySearchFab(false)
    }

    override fun onSearchEnabled(enabled: Boolean) {
        if (!enabled) {
            view?.displaySearchBar(false)
            view?.displaySearchFab(true)
        }
    }
}


