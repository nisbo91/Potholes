package com.example.jimmy.activitetsgenkendelse;

import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status>, SensorEventListener {

    private GoogleApiClient mGoogleApiClient;
    public static Handler forgrundstråd = new Handler();

    static MainActivity instans;
    TextView mDetectedActivityTextView;
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

    private void findBluetooth() {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals("OBDLink MX")) {
                    mDevice = device;
                    break;
                }
            }
        }
        System.out.println("Bluetooth Device Found");
    }

    /***
     * open bluetooth, connect to socket and send setup data
     * @throws IOException
     */
    private void openBluetooth() throws IOException {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
        mSocket = mDevice.createRfcommSocketToServiceRecord(uuid);
        mSocket.connect();
        mOutputStream = mSocket.getOutputStream();
        mInputStream = mSocket.getInputStream();

        // prepare to read setup file
        final Scanner s = new Scanner(getResources().openRawResource(R.raw.canbussetup)).useDelimiter("\r");
        String word;
        // send line-by-line
        try {
            while (s.hasNext()) {
                word = s.next();
                sendData(word);
                SystemClock.sleep(200);
            }
        } finally {
            System.out.println("finally.....");
            s.close();
        }

        beginListenForData();
        System.out.println("Bluetooth Opened");
    }

    /***
     * handle incomming strings
     * @param data String to be sent
     */
    private void handleData(String data){
        if(!(data.equals("\r")||data.equals("?")||data.isEmpty())) {
            // TODO: 08-04-2016 detect/select pid

            // TODO: 08-04-2016 stuff
            counter++;
            System.out.println(counter);
        }
    }

    /***
     * listen to the inputsocket
     */
    private void beginListenForData() {
        final Handler handler = new Handler();
        final byte delimiter = 13;              //This is the ASCII code for carriage return

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                    try {
                        int bytesAvailable = mInputStream.available();
                        if (bytesAvailable > 0) {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mInputStream.read(packetBytes);
                            for (int i = 0; i < bytesAvailable; i++) {
                                byte b = packetBytes[i];
                                if (b == delimiter) {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;
                                    // handle incomming data
                                    handler.post(new Runnable() {
                                        public void run() {
                                            //"412 74 0F 00 36 7A 92 00 10"; // hastighed og odometer
                                            //"210 00 00 4F 41 00 00 00 00"; // throttle
                                            //"236 00";                      // steering wheel
                                            handleData(data);
                                        }
                                    });
                                } else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    } catch (IOException ex) {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }

    /***
     * send data-string
     * @param string String to be sent
     * @throws IOException
     */
    private void sendData(String string) throws IOException{
        String msg = string + "\r";
        mOutputStream.write(msg.getBytes());
    }

    /***
     * close bluetooth connection
     * @throws IOException
     */
    private void closeBluetooth() throws IOException {
        stopWorker = true;
        mOutputStream.close();
        mInputStream.close();
        mSocket.close();
        System.out.println("Socket Closed!");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        instans = this;
        i = 0;
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mLinearAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        mDetectedActivityTextView = (TextView) findViewById(R.id.detected_activities_textview);
        tableLayout = (TableLayout) findViewById(R.id.tl);
        startKnap = (Button) findViewById(R.id.startButton);
        stopKnap = (Button) findViewById(R.id.stopButton);
        scrollView = (ScrollView) findViewById(R.id.sv);

        startKnap.setOnClickListener(this);
        stopKnap.setOnClickListener(this);

        data = new ArrayList<>();
        File dir = new File(path);
        dir.mkdirs();


        Intent intent = new Intent(this, ActivityDetectionBroadcastReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(ActivityRecognition.API)
                .addApi(AppIndex.API).build();
        mGoogleApiClient.connect();

        mDetectedActivityTextView.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                // you can add a toast or whatever you want here
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                //override stub
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                //override stub
            }
        });

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(mBluetoothAdapter == null){
            Toast.makeText(this, "Bluetooth Not Supported", Toast.LENGTH_SHORT);
        }else{
            if(!mBluetoothAdapter.isEnabled()){
                // TODO: 08-04-2016 enable stuff

            }else{
                // TODO: 08-04-2016 connect to device
                try {
                    findBluetooth();
                    openBluetooth();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        try {
            if (v == startKnap) {
                ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(
                        mGoogleApiClient, 5000 , pendingIntent)
                        .setResultCallback(this);
                mDetectedActivityTextView.append("\nStart Genkendelse\n");
            } else if (v == stopKnap) {
                ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(
                        mGoogleApiClient, pendingIntent);
                mDetectedActivityTextView.append("\nStop Genkendelse\n");
            }
        } catch (Throwable t) {
            t.printStackTrace();
            langToast("Fejl: "+t);
        }
    }

    public static void langToast(final String txt) {
        forgrundstråd.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(instans, txt, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instans = null;
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.jimmy.activitetsgenkendelse/http/host/path")
        );
        AppIndex.AppIndexApi.start(mGoogleApiClient, viewAction);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.jimmy.activitetsgenkendelse/http/host/path")
        );
        AppIndex.AppIndexApi.end(mGoogleApiClient, viewAction);
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        String filename = "hej";
        writeToFile(data);
        System.out.println("Data saved.");
        /*try {
            createFile(filename,data);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        //generateNoteOnSD(getApplicationContext(), filename, data);
        //File file = new File (path + "/File.txt");
        Toast.makeText(getApplicationContext(), "Saved ,"+data.size()+", "+filename, Toast.LENGTH_LONG).show();
        System.out.println(filename);
        //Save(file,data);
        mSensorManager.unregisterListener(this);
    }

    /*private void Save(File file, ArrayList<String> data) {
        FileOutputStream fos = null;
        try
        {
            fos = new FileOutputStream(file);
        }
        catch (FileNotFoundException e) {e.printStackTrace();}
        try
        {
            try
            {
                System.out.println(data.size());
                for (int i = 0; i<data.size(); i++)
                {
                    fos.write(data.get(i).getBytes());
                    System.out.println(data.get(i).getBytes());
                    if (i < data.size()-1)
                    {
                        fos.write("\n".getBytes());
                    }
                }
            }
            catch (IOException e) {e.printStackTrace();}
        }
        finally
        {
            try
            {
                System.out.println("close");
                fos.close();
            }
            catch (IOException e) {e.printStackTrace();}
        }
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mLinearAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onConnected(Bundle bundle) {
        mDetectedActivityTextView.append("\nConnected\n");
        Log.i(TAG, "Connected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        mDetectedActivityTextView.append("\nIkke forbundet\n");
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        mDetectedActivityTextView.append("\nIkke forbundet\n");
        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    @Override
    public void onResult(Status status) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        linear_acceleration[0] = event.values[SensorManager.DATA_X];
        linear_acceleration[1] = event.values[SensorManager.DATA_Y];
        linear_acceleration[2] = event.values[SensorManager.DATA_Z];
        mDetectedActivityTextView.append("\nX: " + linear_acceleration[0] + "\nY: " + linear_acceleration[1] + "\nZ: " + linear_acceleration[2] + "\n");
        Log.i("plottegraf", "X:" + linear_acceleration[0] + " Y:" + linear_acceleration[1] + " Z:" + linear_acceleration[2]);
        data.add(linear_acceleration[0] + "," + linear_acceleration[1] + "," + linear_acceleration[2]);
        //writeStringAsFile(+linear_acceleration[0] + "," + linear_acceleration[1] + "," + linear_acceleration[2],"test");
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        mDetectedActivityTextView.append("Accuracy changed: " + accuracy + "\n");
    }

    private void writeToFile(ArrayList<String> data) {
        Context context = getApplicationContext();
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(String.valueOf(data));
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
    /*public void writeStringAsFile(final ArrayList<String> fileContents, String fileName) {
        try {
            FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(fileContents.indexOf(1));
            System.out.println(fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}
