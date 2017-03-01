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

package com.whiterabbit.pisabike.screens.map;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.whiterabbit.pisabike.model.Station;

import java.util.List;

public interface MapView {
    void centerMapToLocation(LatLng l);
    void drawStationsOnMap(List<Station> stations);
    void displayStationDetail(Station detail, Location current);
    void hideStationDetail();
    void stopUpdating();
    void startUpdating();
    void getMap();
    void centerCity(double lat, double lon);
    void enableMyLocation();
    void highLightStation(Station s);
    void unHighLightStation(Station s);
    void stopUpdatingError();
    void navigateTo(Station s);
    void onStationCenterRequested(Station toCenter);
    void checkAndShowInterstitial();
}
