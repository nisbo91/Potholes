package com.example.jimmy.activitetsgenkendelse;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by Jimmy on 10-04-2016.
 */


public class MapFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback, android.location.LocationListener {

    static MapFragment synligInstans;
    private Button addPotholeButton;
    private Button settingsButton;
    private GoogleApiClient mGoogleApiClient;
    MapView mapView;
    private GoogleMap mGoogleMap;
    private LocationManager locationManager;
    private Marker myLocationMarker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        synligInstans = this;

        Log.d("LoginFragment", "Fragment onCreate()");
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        addPotholeButton = (Button) v.findViewById(R.id.addPotholeButton);
        settingsButton = (Button) v.findViewById(R.id.settingsButton);

        mapView = (MapView) v.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        settingsButton.setOnClickListener(this);
        addPotholeButton.setOnClickListener(this);

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        return v;
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.addPotholeButton:

            case R.id.settingsButton:
                android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.Startfragment, new SettingsFragment()).addToBackStack(null).commit();
                break;
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setAllGesturesEnabled(true);
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);

        //myLocationMarker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(mGoogleMap.getMyLocation().getLatitude(),mGoogleMap.getMyLocation().getLongitude())).icon());
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()), 14));
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
