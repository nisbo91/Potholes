package com.example.jimmy.activitetsgenkendelse;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import static com.example.jimmy.activitetsgenkendelse.R.color.green;
import static com.example.jimmy.activitetsgenkendelse.R.color.red;
import static com.example.jimmy.activitetsgenkendelse.R.color.yellow;
import static java.lang.Math.abs;

/**
 * Created by Jimmy on 10-04-2016.
 */


public class MapFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback {

    static MapFragment synligInstans;
    private Button addPotholeButton;
    private ImageButton settingsButton;
    private GoogleApiClient mGoogleApiClient;
    MapView mapView;
    private GoogleMap mGoogleMap;
    private LocationManager locationManager;
    private String locationProvider;
    public static final int OUT_OF_SERVICE = 0;
    public static final int TEMPORARILY_UNAVAILABLE = 1;
    public static final int AVAILABLE = 2;
    private List<String> providers; //[passive, gps, network]
    private TextView netTextView;
    private TextView gpsTextView;
    private GpsStatus gpsStatus;
    private Accelometer accelometer;
    private ArrayList<String> dataAccelometer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        synligInstans = this;

        Log.d("LoginFragment", "Fragment onCreate()");
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        addPotholeButton = (Button) v.findViewById(R.id.addPotholeButton);
        settingsButton = (ImageButton) v.findViewById(R.id.settingsButton);
        netTextView = (TextView) v.findViewById(R.id.netTextview);
        gpsTextView = (TextView) v.findViewById(R.id.gpsTextview);

        mapView = (MapView) v.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        settingsButton.setOnClickListener(this);
        addPotholeButton.setOnClickListener(this);

        accelometer = new Accelometer();
        accelometer.AccelometerInit(getContext());

        dataAccelometer = new ArrayList<String>();

        return v;
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.addPotholeButton:

                break;
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

        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        providers = locationManager.getAllProviders();
        //define the location manager criteria
        Criteria criteria = new Criteria();

        locationProvider = locationManager.getBestProvider(criteria, false);
        gpsStatus = locationManager.getGpsStatus(null);
        System.out.println(""+gpsStatus);
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
        Location location = locationManager.getLastKnownLocation(locationProvider);
        System.out.println(locationProvider);
        System.out.println(location);
        //initialize the location
        if (location != null) {

            // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
            MapsInitializer.initialize(getActivity());

            //when the location changes, update the map by zooming to the location
            CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
            mGoogleMap.moveCamera(center);

            CameraUpdate zoom = CameraUpdateFactory.zoomTo(14);
            mGoogleMap.animateCamera(zoom);

        }

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                Log.i("called", "onLocationChanged");
                //define the location manager criteria
                Criteria criteria = new Criteria();

                locationProvider = locationManager.getBestProvider(criteria, false);
                try{
                    if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                    }
                }
                catch (Exception e){
                    Log.e("error", String.valueOf(e));
                }

                location = locationManager.getLastKnownLocation(locationProvider);

                // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
                //MapsInitializer.initialize(getActivity());

                //when the location changes, update the map by zooming to the location
                CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
                mGoogleMap.moveCamera(center);

                CameraUpdate zoom = CameraUpdateFactory.zoomTo(14);
                mGoogleMap.animateCamera(zoom);

                dataAccelometer = accelometer.returnData();
                boolean pothole=detectedPothole(dataAccelometer);
                accelometer.clearData();
                if(pothole== true){

                }
                else{

                }
            }

            public boolean detectedPothole(ArrayList data) {
                int count = data.size();
                //System.out.println("detected:  " + data);
                int i;
                boolean pothole = false;
                double yaxis;
                yaxis = 0;

                for (i = 0; i < count - 1; i++) {
                    if(yaxis==0){
                        String alldata = (String) data.get(i);
                        //System.out.println("alldata:  "+alldata);
                        String[] split = alldata.split(",");
                        //System.out.println("split:  "+ Arrays.toString(split));
                        yaxis = Double.parseDouble(split[1]);
                        //System.out.println(yaxis);
                    }
                    else{
                        double oldyAxis = yaxis;
                        String alldata = (String) data.get(i);
                        //System.out.println("alldata:  "+alldata);
                        String[] split = alldata.split(",");
                        //System.out.println("split:  "+ Arrays.toString(split));
                        yaxis = Double.parseDouble(split[1]);
                        //System.out.println("yaxis:  "+yaxis);
                        //System.out.println("old:  "+oldyAxis);
                        //System.out.println((abs(yaxis))-(abs(oldyAxis)));
                        int five = 1;
                        if (abs(yaxis)-(abs(oldyAxis))>five){
                            System.out.println(" ");
                            System.out.println("pothole detected");
                            System.out.println(" ");
                            Functionality.langToast("Pothole detected");
                            pothole = true;
                        }
                    }
                }
               return pothole;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                if (provider == providers.get(0)){

                }
                if (provider == providers.get(1)){
                    if (status==OUT_OF_SERVICE){
                        gpsTextView.setBackgroundResource(red);
                    }
                    if (status==TEMPORARILY_UNAVAILABLE){
                        gpsTextView.setBackgroundResource(yellow);
                    }
                    if (status==AVAILABLE){
                        gpsTextView.setBackgroundResource(green);
                    }
                }
                if(provider == providers.get(2)){
                    if (status==OUT_OF_SERVICE){
                        netTextView.setBackgroundResource(red);
                    }
                    if (status==TEMPORARILY_UNAVAILABLE){
                        gpsTextView.setBackgroundResource(yellow);
                    }
                    if (status==AVAILABLE){
                        netTextView.setBackgroundResource(green);
                    }
                }
                else{

                }
            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        accelometer.resumeAccelemeter();
        Log.i("called", "Activity --> onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        accelometer.pauseAccelemeter();
        Log.i("called", "Activity --> onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        Log.i("called", "Activity --> onDestroy");
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
        Log.i("called", "Activity --> onLowMemory");
    }
}
