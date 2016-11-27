package com.whiterabbit.pisabike.screens.list

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.Bind
import butterknife.ButterKnife
import com.whiterabbit.pisabike.R
import com.whiterabbit.pisabike.model.Station
import javax.inject.Inject

class StationsListFragment : Fragment(), StationsListView {
    @Bind(R.id.stations_list)
    val stations : RecyclerView? = null

    @Inject
    val presenter : StationsListPresenter? = null

    override fun onCreateView(inflater: LayoutInflater?,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val res = inflater?.inflate(R.layout.stations_list, null)
        ButterKnife.bind(res)
        return res
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        presenter?.attachToView(this)
    }

    override fun displayStations(l: List<Station>) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun toggleLoading(loading: Boolean) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun displayUpdateError() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}


