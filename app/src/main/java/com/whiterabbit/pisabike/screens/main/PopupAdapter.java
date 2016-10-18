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

package com.whiterabbit.pisabike.screens.main;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.whiterabbit.pisabike.R;
import com.whiterabbit.pisabike.model.Station;

import java.util.Map;

class PopupAdapter implements GoogleMap.InfoWindowAdapter {
    private View popup=null;
    private LayoutInflater inflater=null;
    private final Map<String, Station> mStationsMap;

    PopupAdapter(LayoutInflater inflater, Map<String, Station> stationsMap) {
        this.inflater=inflater;
        mStationsMap = stationsMap;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return(null);
    }

    @SuppressLint("InflateParams")
    @Override
    public View getInfoContents(Marker marker) {
        if (popup == null) {
            popup=inflater.inflate(R.layout.main_map_popup, null);
        }

        Station s = mStationsMap.get(marker.getId());
        if (s == null) {
            return popup;
        }

        TextView name = (TextView)popup.findViewById(R.id.main_popup_name);
        name.setText(s.getName());

        TextView address = (TextView)popup.findViewById(R.id.main_popup_address);
        address.setText(s.getAddress());

        TextView bikes = (TextView)popup.findViewById(R.id.main_popup_bikes);
        String bikesText = String.format(inflater.getContext().getString(R.string.main_popup_bikes_message),
        s.getAvailable(), s.getAvailable() + s.getFree());
        bikes.setText(bikesText);

        return(popup);
    }
}
