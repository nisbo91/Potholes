package com.example.jimmy.activitetsgenkendelse;

import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TableLayout;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ResultCallback<Status>, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener { //implements View.OnClickListener

    private GoogleApiClient mGoogleApiClient;
    public static Handler forgrundstråd = new Handler();

    static MainActivity instans;
    //TextView DetectedActivity;
    private TableLayout tableLayout;
    private Button startKnap;
    private Button stopKnap;
    private PendingIntent pendingIntent;
    private String TAG;
    private ScrollView scrollView;
    private SensorManager mSensorManager;
    private Sensor mLinearAccelerometer;
    float[] linear_acceleration = new float[]{0,0,0};
    public String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/aatest";
    private int i;
    private ArrayList<String> data;

    // bluetooth stuff
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mSocket;
    BluetoothDevice mDevice;
    OutputStream mOutputStream;
    InputStream mInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
    }

    @Override
    public void onResult(Status status) {

    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Connected");
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(
                mGoogleApiClient, 60000 , pendingIntent)
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
