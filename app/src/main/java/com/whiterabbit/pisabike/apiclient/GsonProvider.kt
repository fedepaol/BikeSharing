package com.whiterabbit.pisabike.apiclient

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.whiterabbit.pisabike.model.BikesNetwork
import com.whiterabbit.pisabike.model.Network

fun getGson() : Gson {
    val builder = GsonBuilder()
    return builder.registerTypeAdapter(Network::class.java, BikeDeserializer())
            .registerTypeAdapter(List::class.java, NetworkListDeserializer())
            .disableHtmlEscaping().create()
}
