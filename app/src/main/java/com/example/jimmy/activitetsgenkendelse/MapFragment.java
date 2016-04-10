package com.example.jimmy.activitetsgenkendelse;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Jimmy on 10-04-2016.
 */



public class MapFragment extends Fragment implements View.OnClickListener,OnMapReadyCallback {

    static MapFragment synligInstans;
    private Button addPotholeButton;
    private Button settingsButton;
    MapView mapView;
    private GoogleMap mGoogleMap;

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

        return v;
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        try {
            if (v == addPotholeButton) {

            } else if (v == settingsButton) {
                intent = new Intent(getActivity(), SettingsFragment.class);
                this.startActivity(intent);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            Functionality.langToast("Fejl: "+t);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(43.1, -87.9)));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(43.1, -87.9), 10));
    }
}
