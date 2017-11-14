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

package com.whiterabbit.pisabike.storage;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.whiterabbit.pisabike.model.BikesNetwork;
import com.whiterabbit.pisabike.model.Coordinates;


public class PrefsStorage {
    private static final String LAST_UPDATE_ID = "com.whiterabbit.lastupdate";
    private static final String ADDRESS_ID = "com.whiterabbit.address";
    private static final String LOADED_ID = "com.whiterabbit.loaded";
    private static final String CITY_ID = "com.whiterabbit.city";
    private static final String NETWORK_ID = "com.whiterabbit.network";
    private static final String LATITUDE_ID = "com.whiterabbit.lat";
    private static final String LONGITUDE_ID = "com.whiterabbit.lon";


    private SharedPreferences mPreferences;

    public PrefsStorage(Context c) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(c);
    }

    private void setLastUpdate(long lastUpdate, boolean isNetwork) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putLong(LAST_UPDATE_ID + isNetwork, lastUpdate);
        editor.apply();
    }

    private long getLastUpdate(boolean isNetwork) {
        return mPreferences.getLong(LAST_UPDATE_ID + isNetwork, 0);
    }

    public void setLastUpdate(long lastUpdate) {
        setLastUpdate(lastUpdate, false);
    }

    public long getLastUpdate() {
        return getLastUpdate(false);
    }


    public void setLastNetworkUpdate(long lastUpdate) {
        setLastUpdate(lastUpdate, true);
    }

    public long getLastNetworkUpdate() {
        return getLastUpdate(true);
    }

    private String getLatLonKey(double lat, double lon) {
        return ADDRESS_ID + String.format("%f-%f", lat, lon);
    }

    public void setAddressForLocation(double lat, double lon, String address) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(getLatLonKey(lat, lon), address);
        editor.apply();
    }

    public boolean wasWarningShown() {
        boolean res = mPreferences.getBoolean(LOADED_ID, false);
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(LOADED_ID, true);
        editor.apply();
        return res;
    }

    public void setCurrentNetwork(BikesNetwork network) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(CITY_ID, network.getCity());
        editor.putString(NETWORK_ID, network.getNetwork());
        editor.putFloat(LATITUDE_ID, (float) network.getCoordinates().getLatitude());
        editor.putFloat(LATITUDE_ID, (float) network.getCoordinates().getLatitude());
        editor.apply();
    }

    public BikesNetwork getCurrentNetwork() {
        String city = mPreferences.getString(CITY_ID, "*");
        String network = mPreferences.getString(NETWORK_ID, "");
        float latitude = mPreferences.getFloat(LATITUDE_ID, 0);
        float longitude = mPreferences.getFloat(LONGITUDE_ID, 0);

        if (city.equals("*")) return null;

        return new BikesNetwork(city, network, new Coordinates(latitude, longitude));
    }



}

