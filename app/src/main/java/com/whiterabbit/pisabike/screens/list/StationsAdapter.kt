package com.whiterabbit.pisabike.screens.list

import android.location.Location
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.Bind
import butterknife.ButterKnife
import com.jakewharton.rxrelay.BehaviorRelay
import com.whiterabbit.pisabike.R
import com.whiterabbit.pisabike.model.Station
import rx.Observable


class StationsAdapter : RecyclerView.Adapter<StationsAdapter.ViewHolder>() {

    private val relay : BehaviorRelay<Station> = BehaviorRelay.create()

    val stationSelected : Observable<Station>
        get() = relay

    private class StationsDiffCallback(val old : List<Station>?, val new : List<Station>) : DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldStation = old?.get(oldItemPosition)
            val newStation = new[newItemPosition]
            return oldStation?.name.equals(newStation.name)
        }

        override fun getOldListSize(): Int {
            return old?.size ?: 0
        }

        override fun getNewListSize(): Int {
            return new.size
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) : Boolean {
            if (old?.get(oldItemPosition)?.free != new[newItemPosition].free) {
                return false
            }
            if (old?.get(oldItemPosition)?.available != new[newItemPosition].available) {
                return false
            }
            if (old?.get(oldItemPosition)?.broken != new[newItemPosition].broken) {
                return false
            }
            return true
        }
    }

    inner class ViewHolder(v : View) : RecyclerView.ViewHolder(v) {
        @Bind(R.id.main_detail_name)
        lateinit var name:TextView
        @Bind(R.id.main_detail_address)
        lateinit var address:TextView
        @Bind(R.id.main_detail_bikes)
        lateinit var bikes:TextView
        @Bind(R.id.main_detail_bikes_empty)
        lateinit var bikesEmpty:TextView
        @Bind(R.id.main_detail_distance)
        lateinit var distance:TextView

        var id : Int = 0

        init {
            ButterKnife.bind(this, v)
            v.setOnClickListener {
                relay.call(data?.get(id))
            }
        }
    }

    var data : List<Station>? = null
    var myPosition : Location? = null

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.main_bottom_sheet, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount() = data?.count() ?: 0

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val s = data?.get(position) ?: return

        holder?.name?.text = s.city
        holder?.address?.text= s.address
        holder?.bikes?.text = s.available.toString()
        holder?.bikesEmpty?.text = s.free.toString()
        holder?.distance?.text = s.getDistanceFrom(myPosition).toString()
        holder?.id = position
    }

    fun updateList(newList: List<Station>, position: Location) {
        myPosition = position
        if (data == null) {
            data = newList
            notifyDataSetChanged()
        } else {
            val diffResult = DiffUtil.calculateDiff(StationsDiffCallback(data, newList))
            data = newList
            diffResult.dispatchUpdatesTo(this)
        }
    }
}


