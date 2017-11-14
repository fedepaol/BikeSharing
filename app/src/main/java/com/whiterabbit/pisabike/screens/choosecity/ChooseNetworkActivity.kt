package com.whiterabbit.pisabike.screens.choosecity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.whiterabbit.pisabike.PisaBikeApplication
import com.whiterabbit.pisabike.R
import com.whiterabbit.pisabike.model.BikesNetwork
import com.whiterabbit.pisabike.model.Coordinates
import com.whiterabbit.pisabike.screens.list.DaggerChooseNetworkComponent
import javax.inject.Inject
import com.google.maps.android.clustering.ClusterManager
import com.whiterabbit.pisabike.screens.main.MainActivity
import org.jetbrains.anko.startActivity


/**
 * Created by fedepaol on 10/10/17.
 */

class ChooseNetworkActivity : AppCompatActivity(), ChooseContract.View, OnMapReadyCallback, GoogleMap.OnCameraIdleListener, ClusterManager.OnClusterItemClickListener<NetworksMapItem> {

    @Inject
    lateinit var presenter: ChooseContract.Presenter

    var mClusterManager: ClusterManager<NetworksMapItem>? = null
    var map : GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_network)

        val mapFragment = fragmentManager.findFragmentById(R.id.choose_network_map) as MapFragment

        if (savedInstanceState == null) {
            mapFragment.getMapAsync(this)
        } else {
            map?.let { onMapReady(it) }
        }

        val app = application as PisaBikeApplication

        DaggerChooseNetworkComponent.builder()
                .applicationComponent(app.component)
                .chooseNetworkModule(ChooseNetworkModule())
                .build().inject(this)
    }

    override fun onPause() {
        super.onPause()
        presenter.onViewDetached()
    }

    override fun onResume() {
        super.onResume()
        map?.let { presenter.onViewAttached(this)}
    }

    override fun startApplication(network: BikesNetwork) {
        startActivity<MainActivity>()
    }

    override fun displayNetworks(networks: List<BikesNetwork>) {
        mClusterManager?.addItems(networks.map { n -> NetworksMapItem(n) })
    }

    override fun centerMap(toCenter: Coordinates) {
        val center = CameraUpdateFactory.newLatLng(LatLng(toCenter.latitude, toCenter.longitude))
        map?.animateCamera(center)
    }

    override fun onMapReady(gMap: GoogleMap) {
        map = gMap
        presenter.onViewAttached(this)

        if (mClusterManager == null) {
            mClusterManager = ClusterManager(this, map)

            mClusterManager?.let {
                val renderer = ChooseNetworkItemRenderer(this,
                                gMap,
                                it,
                                this)
                it.renderer = renderer
            }

            map?.setOnCameraIdleListener(mClusterManager)
            map?.setOnMarkerClickListener(mClusterManager)
            mClusterManager?.setOnClusterItemClickListener(this)
        }
    }

    override fun onClusterItemClick(mapItem: NetworksMapItem): Boolean {
        presenter.onNetworkChoosen(mapItem.network)
        return true
    }

    override fun onCameraIdle() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun displayCityDetail(city: BikesNetwork) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}