package com.whiterabbit.pisabike.apiclient


import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.whiterabbit.pisabike.model.Network
import com.whiterabbit.pisabike.model.RestData
import com.whiterabbit.pisabike.model.Station
import java.lang.reflect.Type

class BikeDeserializer : JsonDeserializer<Network> {

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Network {

        val network = json.asJsonObject.getAsJsonObject("network")
        val location = network.getAsJsonObject("location")

        val city = location.get("city").asString
        val array = network.getAsJsonArray("stations")

        var stations = mutableListOf<Station>()
        for (obj1 in array) {
            with (obj1.asJsonObject) {
                val name = obj1.asJsonObject.get("name").asString
                val latitude = get("latitude").asDouble
                val longitue = get("longitude").asDouble
                val freeBikes = get("free_bikes").asLong
                val emptySlots = get("empty_slots").asLong
                val address = getAsJsonObject("extra").get("description").asString
                stations.add(Station(name, city, latitude, longitue, address, freeBikes, emptySlots, 0, false))
            }
        }
        return Network(stations)
    }
}