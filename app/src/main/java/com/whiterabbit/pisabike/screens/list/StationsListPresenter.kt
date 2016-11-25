package com.whiterabbit.pisabike.screens.list

import com.whiterabbit.pisabike.model.Station


interface StationsListPresenter {
    fun attachToView(v : StationsListView)
    fun detachFromView()
    fun onUpdateRequested()
    fun onStationSelected(s : Station)
}