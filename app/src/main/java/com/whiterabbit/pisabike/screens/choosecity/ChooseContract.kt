package com.whiterabbit.pisabike.screens.choosecity

import com.whiterabbit.pisabike.model.BikesNetwork
import com.whiterabbit.pisabike.model.Coordinates


/**
 * Created by fedepaol on 20/07/17.
 */

interface ChooseContract {

    interface View {
        fun displayNetworks(networks: List<BikesNetwork>)
        fun startApplication(network: BikesNetwork)
        fun centerMap(toCenter: Coordinates)
        fun displayCityDetail(city: BikesNetwork)
    }

    interface Presenter {
        fun onCityChoosen(city : String)
        fun onViewAttached(view: View)
        fun onViewDetached()
    }
}