package com.example.jimmy.activitetsgenkendelse;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
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


public class MapFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback, GoogleMap.OnMapClickListener {

    static MapFragment synligInstans;
    public static DetectedActivity MostProbableActivity;
    private static Location location;
    private FragmentActivityCommunication fragmentCommunication;
    public static int accelometerAccuracyIndicator = 0;  //0= Unreliable, 1=Low Accuracy, 2=Medium Accuracy, 3=High Accuracy
    private Button addPotholeButton;
    private ImageButton settingsButton;
    private GoogleApiClient mGoogleApiClient;
    MapView mapView;
    private GoogleMap mGoogleMap;
    private LocationManager locationManager;
    private String locationProvider;
    private List<String> providers; //[passive, gps, network]
    private TextView accelometerTextView;
    private TextView gpsTextView;
    private Accelometer accelometer;
    private ArrayList<String> dataAccelometer;
    private float locationAccuracy; // accuracy in meters, 0=no gps signal, else smaller equal better accuracy. We define accuracy as the radius of 68% confidence.
    private long potholeTimestamp;
    private Circle circle;
    private FragmentActivity context;
    private boolean soundplayed;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        synligInstans = this;

        Log.d("LoginFragment", "Fragment onCreate()");
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        addPotholeButton = (Button) v.findViewById(R.id.addPotholeButton);
        settingsButton = (ImageButton) v.findViewById(R.id.settingsButton);
        accelometerTextView = (TextView) v.findViewById(R.id.netTextview);
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
                // TODO: 03-05-2016 add some check for other markers at the position
                circle = mGoogleMap.addCircle(new CircleOptions()
                        .center(new LatLng(location.getLatitude(),location.getLongitude()))
                        .radius(locationAccuracy)
                        .strokeColor(Color.parseColor("#500084d3"))
                        .fillColor(Color.parseColor("#500084d3")));
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
        mGoogleMap.setOnMapClickListener(this);

        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        providers = locationManager.getAllProviders();
        //define the location manager criteria
        Criteria criteria = new Criteria();

        locationProvider = locationManager.getBestProvider(criteria, false);
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
        if (location != null) {
            MapFragment.location = location;
            locationAccuracy = location.getAccuracy();
            SetAccuracyIndicator(locationAccuracy,accelometerAccuracyIndicator);
            //System.out.println(locationProvider);
            //System.out.println(location);
            //initialize the location

            // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
            MapsInitializer.initialize(getActivity());

            //when the location changes, update the map by zooming to the location
            CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
            mGoogleMap.moveCamera(center);

            CameraUpdate zoom = CameraUpdateFactory.zoomTo(14);
            mGoogleMap.animateCamera(zoom);

        }
        else{
            System.out.println("location null: "+location);
        }

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                Log.i("called", "onLocationChanged");
                MapFragment.location = location;
                float[] distance = new float[10];
                if (circle!= null){
                    Location.distanceBetween(location.getLatitude(),location.getLongitude(),circle.getCenter().latitude,circle.getCenter().longitude,distance);

                    if( distance[0] > circle.getRadius() ){
                        System.out.println("udenfor ");
                        soundplayed = false;
                    } else {
                        System.out.println("indenfor ");
                        if (soundplayed == false){
                            checkAlert();
                            soundplayed = true;
                        }
                    }
                }
                else{
                    System.out.println("no circles");
                }


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
                locationAccuracy = location.getAccuracy();
                SetAccuracyIndicator(locationAccuracy, accelometerAccuracyIndicator);
                // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
                //MapsInitializer.initialize(getActivity());

                //when the location changes, update the map by zooming to the location
                CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
                mGoogleMap.moveCamera(center);

                CameraUpdate zoom = CameraUpdateFactory.zoomTo(14);
                mGoogleMap.animateCamera(zoom);
                //System.out.println(MostProbableActivity.getType());
                //System.out.println(MostProbableActivity.IN_VEHICLE);
                try{
                    if (MostProbableActivity.getType() == MostProbableActivity.IN_VEHICLE){ //||MostProbableActivity.getType() == MostProbableActivity.STILL){
                        dataAccelometer = accelometer.returnData();
                        boolean pothole=detectedPothole(dataAccelometer);
                        accelometer.clearData();
                        if(pothole== true){
                            // send data to database (pothole, potholetimestamp, location, accuracy, Car speed, Car Stering, car throttle)
                            circle = mGoogleMap.addCircle(new CircleOptions()
                                    .center(new LatLng(location.getLatitude(),location.getLongitude()))
                                    .radius(locationAccuracy)
                                    .strokeColor(Color.parseColor("#500084d3"))
                                    .fillColor(Color.parseColor("#500084d3")));
                        }
                        else{

                        }
                    }
                    else{
                        accelometer.clearData();
                    }
                }
                catch(Exception e){
                    Log.e("error", String.valueOf(e));
                }
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
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);
    }

    private void checkAlert() {
        Boolean check = fragmentCommunication.getDataCheck();
        if (check == true){
            playSound();
        }
        else{
            System.out.println("no bip");
        }
    }

    private void playSound() {
        // TODO: 03-05-2016 play a sound when position is inside circles.
        System.out.println("bip bip");
        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.alert);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
    }

    private void SetAccuracyIndicator(float locationAccuracy, int accelometerAccuracyIndicator) {
        if (locationAccuracy == 0){
            gpsTextView.setBackgroundResource(red);

            if (accelometerAccuracyIndicator == 0){
                accelometerTextView.setBackgroundResource(red);
            }
            if (accelometerAccuracyIndicator == 1 || accelometerAccuracyIndicator == 2){
                accelometerTextView.setBackgroundResource(yellow);
            }
            if (accelometerAccuracyIndicator == 3){
                accelometerTextView.setBackgroundResource(green);
            }
        }
        if (locationAccuracy>0 && locationAccuracy<=25){
            gpsTextView.setBackgroundResource(green);

            if (accelometerAccuracyIndicator == 0){
                accelometerTextView.setBackgroundResource(red);
            }
            if (accelometerAccuracyIndicator == 1 || accelometerAccuracyIndicator == 2){
                accelometerTextView.setBackgroundResource(yellow);
            }
            if (accelometerAccuracyIndicator == 3){
                accelometerTextView.setBackgroundResource(green);
            }
        }
        if (locationAccuracy>25){
            gpsTextView.setBackgroundResource(yellow);

            if (accelometerAccuracyIndicator == 0){
                accelometerTextView.setBackgroundResource(red);
            }
            if (accelometerAccuracyIndicator == 1 || accelometerAccuracyIndicator == 2){
                accelometerTextView.setBackgroundResource(yellow);
            }
            if (accelometerAccuracyIndicator == 3){
                accelometerTextView.setBackgroundResource(green);
            }
        }
    }

    public boolean detectedPothole(ArrayList data) {
        int count = data.size();
        //System.out.println("detected:  " + data);
        int i;
        boolean pothole = false;
        double yaxis;
        long timestamp;
        int valueChange = 1;
        yaxis = 0;

        for (i = 0; i < count - 1; i++) {
            if(yaxis==0){
                String alldata = (String) data.get(i);
                //System.out.println("alldata:  "+alldata);
                String[] split = alldata.split(",");
                //System.out.println("split:  "+ Arrays.toString(split));
                yaxis = Double.parseDouble(split[1]);
                //System.out.println(yaxis);
                timestamp = Long.parseLong(split[3]);
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
                timestamp = Long.parseLong(split[3]);
                if (abs(yaxis)-(abs(oldyAxis))>valueChange){
                    System.out.println(" ");
                    System.out.println("pothole detected");
                    System.out.println(" ");
                    //Functionality.langToast("Pothole detected");
                    String method = "pothole";
                    String latitude = String.valueOf(location.getLatitude());
                    String longitude = String.valueOf(location.getLongitude());
                    String mobile_accelerometer_data = String.valueOf(yaxis);
                    BluetoothData bluetoothData = new BluetoothData();
                    String OBD_car_speed = bluetoothData.speed;
                    String OBD_steering_wheel_pos = bluetoothData.steering;
                    String OBD_throttle = bluetoothData.throttle;
                    String OBD_odometer = "";
                    BackbroundTask backgroundTask = new BackbroundTask(MainActivity.instans);
                    backgroundTask.execute(method,latitude,longitude,mobile_accelerometer_data,OBD_car_speed,OBD_throttle,OBD_steering_wheel_pos,OBD_odometer);
                    pothole = true;
                    potholeTimestamp = timestamp;
                }
            }
        }
        return pothole;
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

    @Override
    public void onMapClick(LatLng latLng) {
        circle = mGoogleMap.addCircle(new CircleOptions()
                .center(new LatLng(latLng.latitude,latLng.longitude))
                .radius(locationAccuracy)
                .strokeColor(Color.parseColor("#500084d3"))
                .fillColor(Color.parseColor("#500084d3")));
    }
    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        context = getActivity();
        fragmentCommunication =(FragmentActivityCommunication)context;
    }
}
