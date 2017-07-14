package com.whiterabbit.pisabike.screens.list

import android.location.Location
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SimpleItemAnimator
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

class StationsListFragment : Fragment(), StationsListView, MaterialSearchBar.OnSearchActionListener {
    @Bind(R.id.stations_list_view)
    lateinit var stations : RecyclerView

    @Bind(R.id.stations_list_search)
    lateinit var searchBar : MaterialSearchBar

    @Bind(R.id.list_search_fab)
    lateinit var searchFab : FloatingActionButton

    @Inject
    lateinit var presenter : StationsListPresenter

    lateinit var adapter : StationsAdapter

    val searchTextListener : TextListener = TextListener()

    val subject : PublishRelay<String> = PublishRelay.create()

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
        (stations.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        stations.itemAnimator.changeDuration = 0

        searchBar.setOnSearchActionListener(this)
        searchBar.addTextChangeListener(searchTextListener)
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

    override fun onPause() {
        super.onPause()
        presenter.detachFromView()
    }

    override fun displayStations(l: List<Station>, location : Location?) {
        if (location != null) {
            adapter.updateList(l, location)
        }
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
        if (!visible) {
            searchBar.text = ""
            subject.call("")
        }
    }

    override fun displaySearchFab(visible: Boolean) {
        searchFab.visibility =
                when (visible) {
                    true -> View.VISIBLE
                    false -> View.GONE
                }
    }

    override fun onButtonClicked(buttonCode: Int) {
    }

    override fun onSearchStateChanged(enabled: Boolean) {
        presenter.onSearchEnabled(enabled)
    }

    override fun onSearchConfirmed(text: CharSequence?) {
    }

    inner class TextListener : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            subject.call(p0.toString())
        }
    }

    override fun searchStationObservable(): Observable<String> {
        return subject
    }

    override fun preferredToggledObservable(): Observable<Station> {
        return adapter.stationPreferred
    }
}


