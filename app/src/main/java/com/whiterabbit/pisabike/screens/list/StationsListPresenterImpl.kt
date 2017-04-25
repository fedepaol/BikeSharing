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
    }

    fun filterStation(s : Station, t : String) : Boolean {
        if (t == "") {
            return true
        }

        val lowerName = s.name.toLowerCase()
        val lowerAddress = s.name.toLowerCase()
        val lowerText = t.toLowerCase()
        return lowerName.contains(lowerText) || lowerAddress.contains(lowerText)
    }

    private fun allStationsObservable() : Observable<ListData> {
        val stationsObservable = provider.stationsObservables
        val textObservable = view?.searchStationObservable()?.debounce(2, TimeUnit.SECONDS)
        val filteredStations : Observable<List<Station>>

        if (textObservable != null) {
            filteredStations = Observable.zip(stationsObservable, textObservable,
                    { s: List<Station>, t: String -> Pair(s, t) })
                    .flatMap { p -> Observable.from(p.first).filter { s -> filterStation(s, p.second) } }
                    .toList()
        } else {
            filteredStations = Observable.just(emptyList())
        }

        return Observable.zip(locationProvider.lastKnownLocation.take(1),
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
        val toDisplay = stations.sortedWith(compareBy { it.getDistanceFrom(data?.location) })
        view?.displayStations(toDisplay, data?.location)
    }

    override fun onUpdateRequested() {
        view?.toggleLoading(true)
        val sub = provider.updateBikes().subscribeOn(schedulers.provideBackgroundScheduler())
                              .observeOn(schedulers.provideMainThreadScheduler())
                              .subscribe({ },
                                         { view?.displayUpdateError() },
                                         { view?.toggleLoading(false) })
        subscription.add(sub)
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


