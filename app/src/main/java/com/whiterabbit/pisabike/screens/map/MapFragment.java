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

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterManager;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.whiterabbit.androidutils.InAppPurchaseHelper;
import com.whiterabbit.helper.InterstitialHelper;
import com.whiterabbit.pisabike.PisaBikeApplication;
import com.whiterabbit.pisabike.R;
import com.whiterabbit.pisabike.model.Station;
import com.whiterabbit.pisabike.storage.AddressJobKt;
import com.whiterabbit.pisabike.ui.MapMarkerFactory;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MapFragment extends Fragment implements MapView, OnMapReadyCallback,
                                                      GoogleMap.OnCameraMoveStartedListener,
                                                      GoogleMap.OnCameraIdleListener,
                                                      ClusterManager.OnClusterItemClickListener<MapItem>, OnLikeListener {

    @Inject
    MapPresenter mPresenter;
    @Inject
    Context mContext;
    @Inject
    MapMarkerFactory markerFactory;
    @Inject
    InterstitialHelper mInterstitialHelper;

    @Bind(R.id.main_map)
    com.google.android.gms.maps.MapView mMapView;

    @Bind(R.id.main_detail_sheet)
    View mBottomSheet;

    @Bind(R.id.station_detail_name)
    TextView mDetailName;

    @Bind(R.id.station_detail_address)
    TextView mAddress;

    @Bind(R.id.station_detail_distance)
    TextView mDistance;

    @Bind(R.id.station_detail_free_bikes)
    TextView mBikes;

    @Bind(R.id.station_detail_parks)
    TextView mEmptyBikes;

    @Bind(R.id.station_detail_star)
    LikeButton mPreferredStar;

    @Bind(R.id.fab)
    FloatingActionButton mFab;

    @Bind(R.id.main_directions_fab)
    FloatingActionButton mDirectionsFab;

    @Bind(R.id.main_progress)
    ProgressView mProgress;

    @Bind(R.id.map_coordinator)
    CoordinatorLayout mMapCoordinator;

    BottomSheetBehavior mBottomSheetBehavior;


    private GoogleMap mGoogleMap;
    private String mStationToCenter = "";

    private ClusterManager<MapItem> mClusterManager;

    public static MapFragment createInstance() {
        return new MapFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        PisaBikeApplication app = (PisaBikeApplication) getActivity().getApplication();
        DaggerMapComponent.builder()
                .applicationComponent(app.getComponent())
                .mapModule(app.getMapModule(this))
                .build().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View res = inflater.inflate(R.layout.main_fragment, container, false);
        ButterKnife.bind(this, res);
        return res;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Bundle mapViewSavedInstanceState = savedInstanceState != null ?
                                                    savedInstanceState.getBundle("mapViewSaveState") : null;

        mMapView.onCreate(mapViewSavedInstanceState);

        mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheet);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        mDirectionsFab.setScaleX(0);
        mDirectionsFab.setScaleY(0);
        mDirectionsFab.setEnabled(false);

        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (BottomSheetBehavior.STATE_EXPANDED == newState) {
                    mFab.setEnabled(false);
                    mDirectionsFab.setEnabled(true);
                    mDirectionsFab.animate().scaleX(1).scaleY(1).setDuration(300).start();
                } else if (BottomSheetBehavior.STATE_HIDDEN == newState) {
                    mFab.animate().scaleX(1).scaleY(1).setDuration(300).start();
                    mFab.setEnabled(true);
                    mDirectionsFab.setEnabled(false);
                } else if (BottomSheetBehavior.STATE_SETTLING == newState) {
                    mDirectionsFab.animate().scaleX(0).scaleY(0).setDuration(200).start();
                    mFab.animate().scaleX(0).scaleY(0).setDuration(200).start();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        mPreferredStar.setOnLikeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        mPresenter.onViewAttached(this, mStationToCenter);
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.onViewDetached();
        mMapView.onPause();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.main_action_reload) {
            mPresenter.onReloadAsked();
            return true;
        }
        return false;
    }

    @Override
    public void centerMapToLocation(LatLng l) {
        CameraUpdate center =
                CameraUpdateFactory.newLatLng(l);
        mGoogleMap.animateCamera(center);
    }

    @Override
    public void drawStationsOnMap(List<Station> stations) {
        for (Station s : stations) {
            addMarker(s);
        }
        mClusterManager.cluster();
    }

    private void addMarker(Station s) {
        mClusterManager.addItem(new MapItem(s));
    }

    @Override
    public void displayStationDetail(Station detail, Location current) {
        mDetailName.setText(detail.getName());
        mAddress.setText(detail.getAddress());
        mDistance.setText(String.format(getString(R.string.distance), detail.getDistanceFrom(current)));
        mBikes.setText(String.valueOf(detail.getAvailable()));
        mEmptyBikes.setText(String.valueOf(detail.getFree()));
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        mPreferredStar.setLiked(detail.isFavourite());
    }

    @Override
    public void highLightStation(Station s) {
        MapItem m = new MapItem(s);
        m.setSelected(true);
        mClusterManager.addItem(m);
        mClusterManager.cluster();
    }

    @Override
    public void unHighLightStation(Station s) {
        MapItem m = new MapItem(s);
        m.setSelected(false);
        mClusterManager.addItem(m);
        mClusterManager.cluster();
    }

    @Override
    public void hideStationDetail() {
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    @Override
    public void stopUpdating() {
        mProgress.setDone(getString(R.string.main_done));
    }

    @Override

    public void startUpdating() {
        mProgress.setUpdating(getString(R.string.main_updating));
    }

    @Override
    public void getMap() {
        mMapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mPresenter.onMapReady(); // centering only if the view is recreated, otherwise leave where it was
        mGoogleMap.setOnCameraMoveStartedListener(this);
        if (mClusterManager == null) {
            mClusterManager = new ClusterManager<>(getActivity(), mGoogleMap);
            CameraIdleRenderer renderer = new CameraIdleRenderer(getActivity(), mGoogleMap, mClusterManager, this);
            mClusterManager.setRenderer(renderer);
            mGoogleMap.setOnCameraIdleListener(mClusterManager);
            mGoogleMap.setOnMarkerClickListener(mClusterManager);
            mClusterManager.setOnClusterItemClickListener(this);
        }
    }

    public void displayLoadingWarning() {
        Snackbar mySnackbar = Snackbar.make(mMapCoordinator, R.string.loading_address_message, Snackbar.LENGTH_LONG);
        mySnackbar.show();
    }

    @Override
    public void centerCity(double lat, double lon) {
        CameraUpdate center =
                CameraUpdateFactory.newLatLngZoom(new LatLng(lat,
                                                         lon), 15);
        mGoogleMap.animateCamera(center);
    }

    @Override
    public void enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(mContext, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
             return;
        }
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
    }

    @Override
    public void stopUpdatingError() {
        mProgress.setError(getString(R.string.main_update_error));
    }

    @Override
    public void navigateTo(Station s) {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(
                                                            Locale.US,
                                                            "google.navigation:q=%f,%f&mode=w",
                s.getLatitude(),
                s.getLongitude())));
        startActivity(i);
    }

    @OnClick(R.id.fab)
    public void onCenterLocationClicked() {
        mPresenter.onCenterLocationClicked();
    }

    @OnClick(R.id.main_directions_fab)
    public void onNavigateClicked() {
        mPresenter.onNavigateClicked();
    }

    public boolean onBackPressed() {
        return mPresenter.onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        final Bundle mapViewSaveState = new Bundle(outState);
        mMapView.onSaveInstanceState(mapViewSaveState);
        outState.putBundle("mapViewSaveState", mapViewSaveState);
        mPresenter.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        mPresenter.onStateRestored(savedInstanceState);
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onCameraMoveStarted(int i) {
        mPresenter.onCameraMoved();
    }

    @Override
    public void onCameraIdle() {
        mPresenter.onCameraIdle();
    }

    @Override
    public void onStationCenterRequested(Station toCenter) {
        mPresenter.onStationClicked(toCenter.getName());
    }

    @Override
    public void checkAndShowInterstitial() {
        if (InAppPurchaseHelper.isAdsUnlocked(getActivity()) == InAppPurchaseHelper.AdsUnlocked.LOCKED) {
            mInterstitialHelper.checkAndShowInterstitial();
        }
    }

    @Override
    public boolean onClusterItemClick(MapItem mapItem) {
        mPresenter.onStationClicked(mapItem.getStation().getName());
        return true;
    }

    @Override
    public void liked(LikeButton likeButton) {
        mPresenter.onPreferredToggled(true);
    }

    @Override
    public void unLiked(LikeButton likeButton) {
        mPresenter.onPreferredToggled(false);
    }
}
