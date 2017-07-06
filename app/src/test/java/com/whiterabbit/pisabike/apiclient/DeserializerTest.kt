/*
 * Copyright (c) 2016 Federico Paolinelli.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.whiterabbit.pisabike.apiclient

import com.google.gson.Gson
import com.whiterabbit.pisabike.*
import com.whiterabbit.pisabike.model.Network
import org.junit.Before
import org.junit.Test


/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
class DeserializerTest {
    lateinit var deserializer: Gson

    @Before
    fun setup() {
        deserializer = getGson()
    }

    @Test
    @Throws(Exception::class)
    fun deserializeWorks() {
        val json = getStringFromFile(this, "ciclopi.json")
        val res = deserializer.fromJson(json, Network::class.java)

    }

    @Test
    @Throws(Exception::class)
    fun deserializeWorksBikeMi() {
        val json = getStringFromFile(this, "bikemi.json")
        val stations = deserializer.fromJson(json, Network::class.java)
        val stations1 = stations.stations.filter { it.name.contains("Medaglie") }
    }
}