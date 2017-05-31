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


public class PrefsStorage {
    private static final String LAST_UPDATE_ID = "com.whiterabbit.lastupdate";
    private static final String ADDRESS_ID = "com.whiterabbit.address";


    private SharedPreferences mPreferences;

    public PrefsStorage(Context c) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(c);
    }

    public void setLastUpdate(long lastUpdate) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putLong(LAST_UPDATE_ID, lastUpdate);
        editor.apply();
    }

    public long getLastUpdate() {
        return mPreferences.getLong(LAST_UPDATE_ID, 0);
    }

    private String getLatLonKey(double lat, double lon) {
        return ADDRESS_ID + String.format("%f-%f", lat, lon);
    }

    public void setAddressForLocation(double lat, double lon, String address) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(getLatLonKey(lat, lon), address);
        editor.apply();
    }

    public String getAddressForLocation(double lat, double lon) {
        return mPreferences.getString(getLatLonKey(lat, lon), "");
    }

}

