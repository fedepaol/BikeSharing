package com.whiterabbit.pisabike.screens.list

import android.location.Location
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.Bind
import butterknife.ButterKnife
import com.whiterabbit.pisabike.R
import com.whiterabbit.pisabike.model.Station

class StationsAdapter(stations : List<Station>,
                      val myPos : Location,
                      var listener : StationsListener) : RecyclerView.Adapter<StationsAdapter.ViewHolder>() {

    interface StationsListener {
        fun onStationClicked(s : Station?)
    }


    inner class ViewHolder(v : View,
                     var listener : StationsListener) : RecyclerView.ViewHolder(v) {
        @Bind(R.id.main_detail_name)
        var name:TextView? = null
        @Bind(R.id.main_detail_address)
        var address:TextView? = null
        @Bind(R.id.main_detail_bikes)
        var bikes:TextView? = null
        @Bind(R.id.main_detail_bikes_empty)
        var bikesEmpty:TextView? = null
        @Bind(R.id.main_detail_distance)
        var distance:TextView? = null

        var id : Int = 0

        init {
            ButterKnife.bind(this, v)
            v.setOnClickListener {
                listener.onStationClicked(data?.get(id))}
        }
    }

    var data : List<Station>? = null

    init {
        data = stations
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.main_bottom_sheet, parent, false)
        return ViewHolder(v, listener)
    }

    override fun getItemCount() = data?.count() ?: 0

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val s = data?.get(position) ?: return

        holder?.name?.text = s.city
        holder?.address?.text= s.address
        holder?.bikes?.text = s.available.toString()
        holder?.bikesEmpty?.text = s.free.toString()
        holder?.distance?.text = s.getDistanceFrom(myPos).toString()
        holder?.id = position
    }
}


