package com.whiterabbit.pisabike.apiclient

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.whiterabbit.pisabike.model.Network
import com.whiterabbit.pisabike.model.RestData
import com.whiterabbit.pisabike.model.Station

public fun getGson() : Gson {
    val builder = GsonBuilder()
    return builder.registerTypeAdapter(Network::class.java, BikeDeserializer())
            .disableHtmlEscaping().create()
}
