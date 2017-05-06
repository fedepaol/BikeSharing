package com.whiterabbit.pisabike.screens.main

import android.os.Bundle
import com.whiterabbit.pisabike.model.Station

class MainPresenterImpl() : MainPresenter {
    var STATE_KEY = "ContainerState"
    var view : MainView? = null

    enum class State {
        MAP, LIST, FAVS
    }

    var state = State.MAP

    override fun onViewAttached(mainView: MainView?) {
        view = mainView
        when (state) {
            State.MAP -> view?.displayMap()
            State.LIST -> view?.displayList()
            State.FAVS -> view?.displayFavourites()
        }
    }

    override fun onViewDetached() {
        view = null
    }

    override fun onMapSelectedFromMenu() {
        if (state == State.MAP)
            return
        view?.displayMap()
        state = State.MAP
    }

    override fun onFavouritesSelectedFromMenu() {
        if (state == State.FAVS)
            return
        view?.displayFavourites()
        state = State.FAVS
    }

    override fun onBackPressed() : Boolean {
        if (state == State.MAP) {
            return view?.sendBackPressedToMap() ?: false
        }
        view?.exit()

        return true
    }

    override fun onListSelectedFromMenu() {
        if (state == State.LIST)
            return
        view?.displayList()
        state = State.LIST
    }

    override fun onSaveState(toSave: Bundle?) {
        toSave?.putInt(STATE_KEY, state.ordinal)
    }

    override fun onRestoreState(toRestore: Bundle?) {
        val pos = toRestore?.getInt(STATE_KEY, 0) ?: 0
        state = State.values()[pos]
    }

    override fun onDisplayStationRequested(s: Station?) {
        if (state == State.MAP)
            return
        view?.displayStationOnMap(s)
        state = State.MAP
    }
}

