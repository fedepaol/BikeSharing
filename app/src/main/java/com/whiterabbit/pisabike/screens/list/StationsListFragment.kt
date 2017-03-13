package com.whiterabbit.pisabike.screens.list

import android.location.Location
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import butterknife.Bind
import butterknife.ButterKnife
import butterknife.OnClick
import com.mancj.materialsearchbar.MaterialSearchBar
import com.whiterabbit.pisabike.PisaBikeApplication
import com.whiterabbit.pisabike.R
import com.whiterabbit.pisabike.model.Station
import com.whiterabbit.pisabike.screens.main.MainActivity
import rx.Observable
import javax.inject.Inject

class StationsListFragment : Fragment(), StationsListView, MaterialSearchBar.OnSearchActionListener {
    @Bind(R.id.stations_list_view)
    lateinit var stations : RecyclerView

    @Bind(R.id.fragment_list_swipe)
    lateinit var swipeLayout : SwipeRefreshLayout

    @Bind(R.id.stations_list_search)
    lateinit var searchBar : MaterialSearchBar

    @Bind(R.id.list_search_fab)
    lateinit var searchFab : FloatingActionButton

    @Inject
    lateinit var presenter : StationsListPresenter

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

        searchBar.setOnSearchActionListener(this)
        return res
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app : PisaBikeApplication = activity.application as PisaBikeApplication
        DaggerStationsListComponent.builder()
                .applicationComponent(app.component)
                .stationsListModule(app.listModule)
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

    override fun toggleLoading(loading: Boolean) {
        swipeLayout.post { swipeLayout.isRefreshing = loading }
    }

    override fun displayUpdateError() {
        Toast.makeText(activity, getString(R.string.update_error), Toast.LENGTH_SHORT).show()
    }

    override fun getStationSelectedObservable(): Observable<Station> {
        return adapter.stationSelected
    }

    override fun displayStationOnMap(s: Station) {
        (activity as MainActivity).onDisplayStationRequested(s)
    }

    @OnClick(R.id.list_search_fab)
    fun onSearchFabPressed() {
        presenter.onSearchButtonPressed()
    }

    override fun displaySearchBar(visible: Boolean) {
        searchBar.visibility =
                when (visible) {
                    true -> View.VISIBLE
                    false -> View.GONE
                }
        searchBar.performClick()
    }

    override fun displaySearchFab(visible: Boolean) {
        searchFab.visibility =
                when (visible) {
                    true -> View.VISIBLE
                    false -> View.GONE
                }
    }

    override fun onButtonClicked(buttonCode: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSearchStateChanged(enabled: Boolean) {
        presenter.onSearchEnabled(enabled)
    }

    override fun onSearchConfirmed(text: CharSequence?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}


