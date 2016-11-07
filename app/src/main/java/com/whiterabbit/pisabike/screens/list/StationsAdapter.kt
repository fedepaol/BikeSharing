package com.whiterabbit.pisabike.screens.list

import android.support.v7.widget.RecyclerView
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.Bind
import butterknife.ButterKnife
import com.google.android.gms.maps.model.LatLng
import com.whiterabbit.pisabike.R
import com.whiterabbit.pisabike.model.Station

class StationsAdapter(stations : List<Station>,
                      val myPos : LatLng) : RecyclerView.Adapter<StationsAdapter.ViewHolder>() {
    class ViewHolder(v : View) : RecyclerView.ViewHolder(v) {
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

        init {
            ButterKnife.bind(this, v)
        }
    }

    var data : List<Station>? = null

    init {
        data = stations
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.main_bottom_sheet, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount() = data?.count() ?: 0

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val s = data?.get(position) ?: return

        holder?.name?.setText(s.city)
        holder?.address?.setText(s.address)
        holder?.bikes?.setText(s.available.toString())
        holder?.bikesEmpty?.setText(s.free.toString())
        // TODO holder?.distance?.setText(s.)
    }
}


