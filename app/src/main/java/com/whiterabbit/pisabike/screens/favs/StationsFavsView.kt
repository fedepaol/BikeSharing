package com.whiterabbit.pisabike.screens.list

import android.location.Location
import com.whiterabbit.pisabike.model.Station
import rx.Observable


interface StationsFavsView {
    fun displayStations(l : List<Station>, location : Location?)
    fun getStationSelectedObservable() : Observable<Station>
    fun displayStationOnMap(s : Station)
    fun preferredToggledObservable() : Observable<Station>
}