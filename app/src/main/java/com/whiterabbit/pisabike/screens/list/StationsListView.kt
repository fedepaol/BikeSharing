package com.whiterabbit.pisabike.screens.list

import android.location.Location
import com.whiterabbit.pisabike.model.Station
import rx.Observable


interface StationsListView {
    fun displayStations(l : List<Station>, location : Location?)
    fun toggleLoading(loading : Boolean)
    fun displayUpdateError()
    fun getStationSelectedObservable() : Observable<Station>
}