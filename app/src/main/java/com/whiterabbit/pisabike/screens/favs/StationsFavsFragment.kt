package com.whiterabbit.pisabike.screens.list

import android.location.Location
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import butterknife.Bind
import butterknife.ButterKnife
import butterknife.OnClick
import com.jakewharton.rxrelay.PublishRelay
import com.mancj.materialsearchbar.MaterialSearchBar
import com.whiterabbit.pisabike.PisaBikeApplication
import com.whiterabbit.pisabike.R
import com.whiterabbit.pisabike.model.Station
import com.whiterabbit.pisabike.screens.main.MainActivity
import rx.Observable
import javax.inject.Inject

class StationsFavsFragment : Fragment(), StationsFavsView {
    @Bind(R.id.stations_list_view)
    lateinit var stations : RecyclerView

    @Bind(R.id.fragment_list_swipe)
    lateinit var swipeLayout : SwipeRefreshLayout

    @Inject
    lateinit var presenter : StationsFavsPresenter

    lateinit var adapter : StationsAdapter

    override fun onCreateView(inflater: LayoutInflater?,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val res = inflater?.inflate(R.layout.stations_list, container, false)
        ButterKnife.bind(this, res)

        stations.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(activity.applicationContext)
        stations.layoutManager = layoutManager

        adapter = StationsAdapter(activity.applicationContext)
        stations.adapter = adapter

        swipeLayout.isEnabled = true
        swipeLayout.setOnRefreshListener { presenter.onUpdateRequested() }
        return res
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app : PisaBikeApplication = activity.application as PisaBikeApplication
        DaggerStationsFavsComponent.builder()
                .applicationComponent(app.component)
                .stationsFavsModule(app.favsModule)
                .build().inject(this)
    }

    override fun onResume() {
        super.onResume()
        presenter.attachToView(this)
    }

    override fun displayStations(l: List<Station>, location : Location?) {
        if (location != null) {
            adapter.updateList(l, location)
        }
    }

    override fun getStationSelectedObservable(): Observable<Station> {
        return adapter.stationSelected
    }

    override fun displayStationOnMap(s: Station) {
        (activity as MainActivity).onDisplayStationRequested(s)
    }

    override fun preferredToggledObservable(): Observable<Station> {
        return adapter.stationPreferred
    }

    override fun toggleLoading(loading: Boolean) {
        swipeLayout.post { swipeLayout.isRefreshing = loading }
    }
}


