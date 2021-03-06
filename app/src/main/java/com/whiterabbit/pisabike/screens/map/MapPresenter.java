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

import android.os.Bundle;

public interface MapPresenter {
    void onViewDetached();
    void onViewAttached(MapView view, String stationToCenter);
    void onMapReady();
    void onStationClicked(String stationName);

    boolean hasLocationPermission();
    void onCameraMoved();

    void onCenterLocationClicked();

    void onReloadAsked();

    void onNavigateClicked();

    void onCameraIdle();

    boolean onBackPressed();

    void onSaveInstanceState(Bundle outState);

    void onStateRestored(Bundle savedInstanceState);

    void onPreferredToggled(boolean isPreferred);
}
