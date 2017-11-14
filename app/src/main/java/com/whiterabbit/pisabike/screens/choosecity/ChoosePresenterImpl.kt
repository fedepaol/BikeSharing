package com.whiterabbit.pisabike.screens.choosecity

import android.Manifest
import com.tbruyelle.rxpermissions.RxPermissions
import com.whiterabbit.pisabike.model.BikesNetwork
import com.whiterabbit.pisabike.model.Coordinates
import com.whiterabbit.pisabike.storage.BikesRepository
import com.whiterabbit.pisabike.storage.PrefsStorage
import pl.charmas.android.reactivelocation.ReactiveLocationProvider
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription

/**
 * Created by fedepaol on 10/15/17.
 */

class ChoosePresenterImpl(val prefsStorage: PrefsStorage,
                          val permission:RxPermissions,
                          val locationProvider: ReactiveLocationProvider,
                          val bikesRepo: BikesRepository): ChooseContract.Presenter {

    var view: ChooseContract.View? = null
    lateinit  var subscription: CompositeSubscription

    override fun onViewAttached(v: ChooseContract.View) {
        subscription = CompositeSubscription()
        view = v
        subscription.add(checkPermissionAndCenter())
        subscription.add(fetchCities())
        subscription.add(checkAndUpdateCities())
    }

    override fun onViewDetached() {
        view = null
        subscription.unsubscribe()
    }

    fun checkPermissionAndCenter() : Subscription {

        return permission.request(Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION)
                .flatMap { granted -> when {
                                            granted -> locationProvider.lastKnownLocation.map {l -> Coordinates(l.latitude, l.longitude) }
                                            else -> Observable.just(prefsStorage.currentNetwork.coordinates)
                    }
                 }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {c -> view?.centerMap(c)}

    }

    fun fetchCities() : Subscription {
        return bikesRepo.networksObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {n -> view?.displayNetworks(n)}
    }

    fun checkAndUpdateCities() : Subscription {
        return bikesRepo.updateNetworks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }

    override fun onNetworkChoosen(network: BikesNetwork) {
        prefsStorage.currentNetwork = network
        view?.startApplication(network)
    }
}