package com.whiterabbit.pisabike.apiclient

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.whiterabbit.pisabike.model.Network
import com.whiterabbit.pisabike.model.RestData
import com.whiterabbit.pisabike.model.Station

fun getGson() : Gson {
    val builder = GsonBuilder()
    builder.registerTypeAdapter(Network::class.java, BikeDeserializer())
    return builder.create()
}
