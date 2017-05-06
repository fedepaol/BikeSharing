package com.whiterabbit.pisabike.screens.list


interface StationsFavsPresenter {
    fun attachToView(v : StationsFavsView)
    fun detachFromView()
    fun onUpdateRequested()
}