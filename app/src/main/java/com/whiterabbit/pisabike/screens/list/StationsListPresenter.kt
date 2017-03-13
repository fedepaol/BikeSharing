package com.whiterabbit.pisabike.screens.list


interface StationsListPresenter {
    fun attachToView(v : StationsListView)
    fun detachFromView()
    fun onUpdateRequested()
    fun onSearchButtonPressed()
    fun onSearchEnabled(enabled : Boolean)
}