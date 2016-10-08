package com.whiterabbit.pisabike.screens.main;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.whiterabbit.pisabike.PisaBikeApplication;
import com.whiterabbit.pisabike.R;
import com.whiterabbit.pisabike.model.Station;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainFragment extends Fragment implements MainView, OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraMoveListener {

    @Inject
    MainPresenter mPresenter;
    @Inject
    Context mContext;

    @Bind(R.id.main_map)
    MapView mMapView;

    @Bind(R.id.main_detail_sheet)
    View mBottomSheet;

    @Bind(R.id.main_detail_name)
    TextView mDetailName;

    @Bind(R.id.main_detail_address)
    TextView mAddress;

    @Bind(R.id.main_detail_distance)
    TextView mDistance;

    @Bind(R.id.main_detail_bikes)
    TextView mBikes;

    @Bind(R.id.main_detail_bikes_empty)
    TextView mEmptyBikes;

    @Bind(R.id.fab)
    FloatingActionButton mFab;

    @Bind(R.id.main_directions_fab)
    FloatingActionButton mDirectionsFab;

    @Bind(R.id.main_progress)
    ProgressView mProgress;

    BottomSheetBehavior bottomSheetBehavior;

    private GoogleMap mGoogleMap;
    private HashMap<String, Station> stationsMap = new HashMap<String, Station>();
    private HashMap<String, Marker> markerMap = new HashMap<>();

    public static MainFragment createInstance() {
        MainFragment res = new MainFragment();
        return res;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View res = inflater.inflate(R.layout.main_fragment, container, false);
        ButterKnife.bind(this, res);
        return res;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final Bundle mapViewSavedInstanceState = savedInstanceState != null ?
                                                    savedInstanceState.getBundle("mapViewSaveState") : null;
        mMapView.onCreate(mapViewSavedInstanceState);
        super.onViewCreated(view, savedInstanceState);


        PisaBikeApplication app = (PisaBikeApplication) getActivity().getApplication();
        DaggerMainComponent.builder()
                .applicationComponent(app.getComponent())
                .mainModule(app.getMainModule(this))
                .build().inject(this);

        bottomSheetBehavior = BottomSheetBehavior.from(mBottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        mDirectionsFab.setScaleX(0);
        mDirectionsFab.setScaleY(0);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (BottomSheetBehavior.STATE_EXPANDED == newState) {
                    mFab.setEnabled(false);
                    mDirectionsFab.animate().scaleX(1).scaleY(1).setDuration(300).start();
                } else if (BottomSheetBehavior.STATE_HIDDEN == newState) {
                    mFab.animate().scaleX(1).scaleY(1).setDuration(300).start();
                    mFab.setEnabled(true);
                } else if (BottomSheetBehavior.STATE_SETTLING == newState) {
                    mDirectionsFab.animate().scaleX(0).scaleY(0).setDuration(200).start();
                    mFab.animate().scaleX(0).scaleY(0).setDuration(200).start();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        mPresenter.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.onPause();
        mMapView.onPause();
    }

    @Override
    public void centerMapToLocation(LatLng l) {
        CameraUpdate center =
                CameraUpdateFactory.newLatLng(l);
        mGoogleMap.animateCamera(center);
    }

    @Override
    public void drawStationsOnMap(List<Station> stations) {
        stationsMap.clear();

        for (Station s : stations) {
            addMarker(mGoogleMap, s.getLatitude(), s.getLongitude(), s);
        }
    }

    private void addMarker(GoogleMap map, double lat, double lon,
                           Station s) {
        Bitmap b = MapMarkerFactory.getNotSelectedMapMarker(s.getAvailable(), s.getSpaces(), mContext);

        Marker m = markerMap.get(s.getName());
        LatLng pos = new LatLng(lat, lon);
        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(b);
        if (m == null) {
            m = map.addMarker(new MarkerOptions().position(pos).icon(icon));
            markerMap.put(s.getName(), m);
        } else {
            m.setIcon(icon);
        }
        stationsMap.put(m.getId(), s);
    }

    @Override
    public void displayStationList(List<Station> stations, Location current) {

    }

    @Override
    public void hideStationList() {

    }

    @Override
    public void displayStationDetail(Station detail, Location current) {
        mDetailName.setText(detail.getName());
        mAddress.setText(detail.getAddress());
        mDistance.setText("250 m");
        mBikes.setText(String.valueOf(detail.getAvailable()));
        mEmptyBikes.setText(String.valueOf(detail.getFree()));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void highLightStation(Station s) {
        Marker m = markerMap.get(s.getName());
        Bitmap b = MapMarkerFactory.getSelectedMapMarker(s.getAvailable(), s.getSpaces(), mContext);
        m.setIcon(BitmapDescriptorFactory.fromBitmap(b));
    }

    @Override
    public void unHighLightStation(Station s) {
        Marker m = markerMap.get(s.getName());
        Bitmap b = MapMarkerFactory.getNotSelectedMapMarker(s.getAvailable(), s.getSpaces(), mContext);
        m.setIcon(BitmapDescriptorFactory.fromBitmap(b));

    }

    @Override
    public void hideStationDetail() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
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
        if (mGoogleMap != null) {
            mPresenter.onMapReady();
        } else {
            mMapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mPresenter.onMapReady();
        mGoogleMap.setOnMarkerClickListener(this);
        mGoogleMap.setOnCameraMoveListener(this);
    }

    @Override
    public void centerCity() {
        CameraUpdate center =
                CameraUpdateFactory.newLatLng(new LatLng(43.7228,
                        10.4017));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);

        mGoogleMap.moveCamera(center);
        mGoogleMap.animateCamera(zoom);
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        Station s = stationsMap.get(marker.getId());
        mPresenter.onStationClicked(s);
        return true;
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
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        final Bundle mapViewSaveState = new Bundle(outState);
        mMapView.onSaveInstanceState(mapViewSaveState);
        outState.putBundle("mapViewSaveState", mapViewSaveState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onCameraMove() {
        mPresenter.onCameraMoved();
    }

    @OnClick(R.id.fab)
    public void onCenterLocationClicked() {
        mPresenter.onCenterLocationClicked();
    }
}
