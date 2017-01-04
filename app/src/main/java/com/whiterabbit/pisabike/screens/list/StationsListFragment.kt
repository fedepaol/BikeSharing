package com.whiterabbit.pisabike.screens.list

import android.location.Location
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import butterknife.Bind
import butterknife.ButterKnife
import com.whiterabbit.pisabike.PisaBikeApplication
import com.whiterabbit.pisabike.R
import com.whiterabbit.pisabike.model.Station
import rx.Observable
import javax.inject.Inject

class StationsListFragment : Fragment(), StationsListView {

    @Bind(R.id.stations_list_view)
    lateinit var stations : RecyclerView

    @Inject
    lateinit var presenter : StationsListPresenter

    var adapter : StationsAdapter = StationsAdapter()

    override fun onCreateView(inflater: LayoutInflater?,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val res = inflater?.inflate(R.layout.stations_list, container, false)
        ButterKnife.bind(this, res)

        stations.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(activity.applicationContext)
        stations.layoutManager = layoutManager

        adapter = StationsAdapter()
        stations.adapter = adapter
        return res
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app : PisaBikeApplication = activity.application as PisaBikeApplication
        DaggerStationsListComponent.builder()
                .applicationComponent(app.component)
                .stationsListModule(app.getListModule(this))
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
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun displayUpdateError() {
        Toast.makeText(activity, getString(R.string.update_error), Toast.LENGTH_SHORT).show()
    }

    override fun getStationSelectedObservable(): Observable<Station> {
        return adapter.stationSelected
    }
}


