package com.whiterabbit.pisabike.screens.list

import com.whiterabbit.pisabike.model.Station
import com.whiterabbit.pisabike.schedule.SchedulersProvider
import com.whiterabbit.pisabike.storage.BikesProvider
import com.whiterabbit.pisabike.storage.PrefsStorage
import rx.subscriptions.CompositeSubscription

class StationsListPresenterImpl(val provider : BikesProvider,
                                val schedulers : SchedulersProvider,
                                val prefs : PrefsStorage) : StationsListPresenter {
    val subscription : CompositeSubscription

    init {
        subscription = CompositeSubscription()
    }

    override fun attachToView(v: StationsListView) {
        provider.stationsObservables.subscribeOn(schedulers.provideBackgroundScheduler())
                                    .observeOn(schedulers.provideMainThreadScheduler())
                                    .subscribe { s -> onStationsUpdate(s) }

    }

    override fun detachFromView() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun onStationsUpdate(stations : List<Station>) {

    }

    fun checkAndUpdate() {
        if (prefs.)
    }
}


