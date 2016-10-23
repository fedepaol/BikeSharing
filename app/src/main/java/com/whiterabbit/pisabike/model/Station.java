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

package com.whiterabbit.pisabike.model;


import android.content.ContentValues;
import android.database.Cursor;

import com.whiterabbit.pisabike.storage.PisaBikeDbHelper;

import java.util.Date;

public class Station {
    String name;
    String city;
    double latitude;
    double longitude;
    String address;
    long available;
    long free;
    long broken;
    boolean favourite;
    Date lastUpdate;

    public Station(String name,
                   String city,
                   double latitude,
                   double longitude,
                   String address,
                   long available,
                   long free,
                   long broken,
                   boolean favourite) {
        this.name = name;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.available = available;
        this.free = free;
        this.broken = broken;
        this.favourite = favourite;
    }

    public Station(Cursor c) {
        this.name = c.getString(PisaBikeDbHelper.STATION_NAME_COLUMN_POSITION);
        this.city = c.getString(PisaBikeDbHelper.STATION_CITY_COLUMN_POSITION);
        this.latitude = c.getDouble(PisaBikeDbHelper.STATION_LATITUDE_COLUMN_POSITION);
        this.longitude = c.getDouble(PisaBikeDbHelper.STATION_LONGITUDE_COLUMN_POSITION);
        this.address = c.getString(PisaBikeDbHelper.STATION_ADDRESS_COLUMN_POSITION);
        this.available = c.getLong(PisaBikeDbHelper.STATION_AVAILABLE_COLUMN_POSITION);
        this.free = c.getLong(PisaBikeDbHelper.STATION_FREE_COLUMN_POSITION);
        this.broken = c.getLong(PisaBikeDbHelper.STATION_BROKEN_COLUMN_POSITION);
        this.favourite = c.getInt(PisaBikeDbHelper.STATION_FAVORITE_COLUMN_POSITION) != 0;
        this.lastUpdate = new Date(c.getLong(PisaBikeDbHelper.STATION_LASTUPDATE_COLUMN_POSITION));
    }

    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PisaBikeDbHelper.STATION_NAME_COLUMN, name);
        contentValues.put(PisaBikeDbHelper.STATION_CITY_COLUMN, city);
        contentValues.put(PisaBikeDbHelper.STATION_LATITUDE_COLUMN, latitude);
        contentValues.put(PisaBikeDbHelper.STATION_LONGITUDE_COLUMN, longitude);
        contentValues.put(PisaBikeDbHelper.STATION_ADDRESS_COLUMN, address);
        contentValues.put(PisaBikeDbHelper.STATION_AVAILABLE_COLUMN, available);
        contentValues.put(PisaBikeDbHelper.STATION_FREE_COLUMN, free);
        contentValues.put(PisaBikeDbHelper.STATION_BROKEN_COLUMN, broken);
        contentValues.put(PisaBikeDbHelper.STATION_LASTUPDATE_COLUMN, 0); // lastUpdate.getTime());
        contentValues.put(PisaBikeDbHelper.STATION_FAVORITE_COLUMN, favourite);
        return contentValues;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getAvailable() {
        return available;
    }

    public void setAvailable(long available) {
        this.available = available;
    }

    public long getFree() {
        return free;
    }

    public void setFree(long free) {
        this.free = free;
    }

    public long getBroken() {
        return broken;
    }

    public void setBroken(long broken) {
        this.broken = broken;
    }

    public long getSpaces() {
        return available + free;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Station && ((Station) object).getName().equals(getName())) {
            return true;
        } else {
            return false;
        }
    }
}
