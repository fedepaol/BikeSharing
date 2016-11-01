package com.whiterabbit.pisabike.screens.maincontainer

import android.os.Bundle

class MainContainerPresenterImpl() : MainContainerPresenter {
    var STATE_KEY = "ContainerState"
    var view : MainContainerView? = null

    enum class State {
        MAP, LIST
    }

    var state = State.MAP

    override fun onViewAttached(mainView: MainContainerView?) {
        view = mainView
        when (state) {
            State.MAP -> view?.displayMap()
            State.LIST -> view?.displayList()

        }
    }

    override fun onViewDetached() {
        view = null
    }

    override fun onMapSelectedFromMenu() {
        view?.displayMap()
        state = State.MAP
    }

    override fun onBackPressed() {
        if (state == State.MAP) {
            view?.sendBackPressedToMap()
        }
    }

    override fun onListSelectedFromMenu() {
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
}

