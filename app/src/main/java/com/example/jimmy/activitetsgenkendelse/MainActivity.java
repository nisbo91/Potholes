package com.example.jimmy.activitetsgenkendelse;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;

public class MainActivity extends AppCompatActivity implements ResultCallback<Status>, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener { //implements View.OnClickListener

    private GoogleApiClient mGoogleApiClient;

    static MainActivity instans;
    private PendingIntent pendingIntent;
    private String TAG;
    private ActivityDetectionBroadcastReceiver mBroadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().add(R.id.Startfragment, new MapFragment()).commit();
        }

        instans = this;

        // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(ActivityRecognition.API)
                .addApi(AppIndex.API).build();
        mGoogleApiClient.connect();

        mBroadcastReceiver = new ActivityDetectionBroadcastReceiver();

        Intent intent = new Intent(this, ActivityDetectionBroadcastReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
    }
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        registerReceiver(mBroadcastReceiver, intentFilter);
        Log.i("called", "Activity --> onResume (Main)");
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mBroadcastReceiver);
        Log.i("called", "Activity --> onPause (Main)");
    }

    @Override
    public void onResult(Status status) {

    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Connected");
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(
                mGoogleApiClient, 5000 , pendingIntent)
                .setResultCallback(this); // 60000 = 1 min
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());
    }
}
