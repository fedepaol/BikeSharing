package com.whiterabbit.pisabike.apiclient


import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.whiterabbit.pisabike.model.*
import java.lang.reflect.Type
import java.util.*

class BikeDeserializer : JsonDeserializer<Network> {

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Network {

        val network = json.asJsonObject.getAsJsonObject("network")
        val location = network.getAsJsonObject("location")

        val city = location.get("city").asString
        val array = network.getAsJsonArray("stations")

        val stations = mutableListOf<Station>()
        for (obj1 in array) {
            with (obj1.asJsonObject) {
                val name = get("name").asString.replace("\\u0027", "'")
                val latitude = get("latitude").asDouble
                val longitue = get("longitude").asDouble
                val freeBikes = get("free_bikes").asLong
                val emptySlots = get("empty_slots").asLong
                var address = ""
                if (has("extra") && getAsJsonObject("extra").has("description")) {
                    address = getAsJsonObject("extra").get("description").asString
                }
                stations.add(Station(name, city, latitude, longitue, address, freeBikes, emptySlots, 0, false, Date(0), false))
            }
        }
        return Network(stations)
    }
}

class NetworkListDeserializer: JsonDeserializer<Networks> {
    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Networks {
        val networks = json.asJsonObject.getAsJsonArray("networks")
        val res = mutableListOf<BikesNetwork>()
        for (network_obj in networks) {
            with (network_obj.asJsonObject) {
                val id = get("id").asString
                val locationObj = getAsJsonObject("location")
                val city = locationObj.get("city").asString
                val latitude = locationObj.get("latitude").asDouble
                val longitue = locationObj.get("longitude").asDouble
                res.add(BikesNetwork(city, id, Coordinates(latitude, longitue)))
            }
        }
        return Networks(res.toTypedArray())
    }

}