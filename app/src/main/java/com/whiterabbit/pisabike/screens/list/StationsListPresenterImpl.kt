package com.whiterabbit.pisabike.screens.list

import com.whiterabbit.pisabike.model.Station
import com.whiterabbit.pisabike.schedule.SchedulersProvider
import com.whiterabbit.pisabike.storage.BikesProvider
import com.whiterabbit.pisabike.storage.PrefsStorage
import rx.subscriptions.CompositeSubscription

class StationsListPresenterImpl(val provider : BikesProvider,
                                val schedulers : SchedulersProvider,
                                val prefs : PrefsStorage) : StationsListPresenter {
    var subscription : CompositeSubscription? = null
    var view : StationsListView? = null

    override fun attachToView(v: StationsListView) {
        view = v
        subscription = CompositeSubscription()
        val sub = provider.stationsObservables.subscribeOn(schedulers.provideBackgroundScheduler())
                                    .observeOn(schedulers.provideMainThreadScheduler())
                                    .subscribe { s -> onStationsUpdate(s) }

        subscription?.add(sub)
    }

    override fun detachFromView() {
        subscription?.unsubscribe()
    }

    fun onStationsUpdate(stations : List<Station>) {

    }

    override fun onUpdateRequested() {
        val sub = provider.updateBikes().subscribeOn(schedulers.provideBackgroundScheduler())
                              .observeOn(schedulers.provideMainThreadScheduler())
                              .subscribe({ s -> {}},
                                         { view?.displayUpdateError() },
                                         { view?.stopUpdating() })
        subscription?.add(sub)
    }
}


