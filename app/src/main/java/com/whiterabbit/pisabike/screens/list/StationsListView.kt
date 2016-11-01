package com.whiterabbit.pisabike.screens.list

import com.whiterabbit.pisabike.model.Station


interface StationsListView {
    fun displayStations(l : List<Station>)
    fun toggleLoading(loading : Boolean)
}