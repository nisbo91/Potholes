package com.example.jimmy.activitetsgenkendelse;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Jesper de Fries on 10-04-2016.
 */
public class BluetoothManager {


    Context context;
    Integer packageCounter = 0;

    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mSocket;
    BluetoothDevice mDevice;
    OutputStream mOutputStream;
    InputStream mInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;
    //TextView textView;
    Boolean finaly = false;
    boolean dataFound;
    String lookUpValue = "\n412";
    Integer lookUpPlace = 0;
    Integer restartCounter = 0;

    private String speed;
    private String speedTs;
    private String steering;
    private String steeringTs;
    private String throttle;
    private String throttleTs;

    void setSpeed(String d) {
        Long tsLong = System.currentTimeMillis() / 1000;
        this.speed = d;
        this.speedTs = tsLong.toString();
        System.out.println("data: " + d + " - " + speedTs);
    }

    void setSteering(String d) {
        Long tsLong = System.currentTimeMillis() / 1000;
        this.steering = d;
        this.steeringTs = tsLong.toString();
        System.out.println("data: " + d + " - " + steeringTs);
    }

    void setThrottle(String d) {
        Long tsLong = System.currentTimeMillis() / 1000;
        this.throttle = d;
        this.throttleTs = tsLong.toString();
        System.out.println("data: " + d + " - " + throttleTs);
    }

    public BluetoothManager(final Context context) {
        this.context = context;
        //textView = (TextView) ((Activity) context).findViewById(R.id.counter);


        // TODO: 10-04-2016 listen for changes on bluetooth
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        final BroadcastReceiver mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();

                if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                    final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                            BluetoothAdapter.ERROR);
                    switch (state) {
                        case BluetoothAdapter.STATE_OFF:
                            System.out.println("off");
                            closeApp();
                            break;
                        case BluetoothAdapter.STATE_ON:
                            System.out.println("on");
                            setUpBluetooth();
                            break;
                    }
                }
            }
        };
        // TODO: 10-04-2016 register bluetooth receiver
        context.registerReceiver(mReceiver, filter);

        // TODO: 10-04-2016 if device does not support bluetooth
        if (BluetoothAdapter.getDefaultAdapter() == null) {
            Toast.makeText(context, "Bluetooth Not Supported On Device", Toast.LENGTH_LONG);
            closeApp();
        } else {
            // TODO: 10-04-2016 test if bluetooth is enabled
            if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {

                // TODO: 10-04-2016 ask user to turn on bluetooth
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Bluetooth not enabled.");
                builder.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(context, "Turning On Bluetooth", Toast.LENGTH_SHORT).show();
                        turnOnBluetooth();
                    }
                });
                builder.setNegativeButton("Close App", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(context, "Closing....", Toast.LENGTH_SHORT).show();
                        closeApp();
                    }
                });
                builder.create().show();
            } else {
                // TODO: 10-04-2016 make connection
                System.out.println("Make Connection");
                setUpBluetooth();
            }

        }
    }

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
     *
     * @throws IOException
     */
    private void openBluetooth() {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
        try {
            mSocket = mDevice.createRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mSocket.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mOutputStream = mSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mInputStream = mSocket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // TODO: 10-04-2016 send data async
        new AsyncTask() {
            // prepare to read setup file
            final Scanner s = new Scanner(context.getResources().openRawResource(R.raw.canbussetup)).useDelimiter("\r");
            String word = null;

            @Override
            protected Object doInBackground(Object[] objects) {
                // TODO: 10-04-2016 send data line by line
                try {
                    while (s.hasNext()) {
                        word = s.next();            // set line from setup
                        try {
                            sendData(word);         // send setup line
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        SystemClock.sleep(200);     // sloppy coding
                    }
                } finally {
                    System.out.println("Setup Upload Complete");
                    finaly = true;
                    s.close();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object resultat) {
                super.onPostExecute(resultat);
                // TODO: 30-09-2015 update text på bottomframe
                beginListenForData();

            }

        }.execute();

        //beginListenForData();
        System.out.println("Bluetooth Opened");
    }

    /***
     * handle incomming strings
     *
     * @param data
     */
    public void handleData(String data) {
        if (!data.isEmpty() && data.startsWith(lookUpValue)) {
            packageCounter++;
            int nbThreads = Thread.getAllStackTraces().keySet().size();
            System.out.println("pc:" + packageCounter + "    threads: " + nbThreads);

            switch (lookUpPlace) {
                case 0:
                    setSpeed(data);
                    break;
                case 1:
                    setSteering(data);
                    break;
                case 2:
                    setThrottle(data);
                    break;
            }

            getNextPid();
            resetBluetooth();
            //Runtime.getRuntime().gc();
            //System.gc();
        } else {
            dataFound = false;
        }
    }

    void getNextPid() {
        switch (lookUpPlace) {
            case 0:
                lookUpValue = "\n412";
                lookUpPlace++;
                break;
            case 1:
                lookUpValue = "\n236";
                lookUpPlace++;
                break;
            case 2:
                lookUpValue = "\n210";
                lookUpPlace = 0;
                break;
        }
    }

    /***
     * get next PID
     */
    void resetBluetooth() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    //System.out.println("trying to reset bluetooth");
                    sendData("x");
                    SystemClock.sleep(200);
                    sendData(lookUpValue);
                    SystemClock.sleep(200);
                    sendData("atma");
                    SystemClock.sleep(200);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(e);
                }
                return null;
            }
        }.execute();
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
            public void run() { //!Thread.currentThread().isInterrupted() &&
                while (!stopWorker) {
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
                                    final String[] data = {new String(encodedBytes, "US-ASCII")};
                                    encodedBytes = null;
                                    readBufferPosition = 0;
                                    // handle incomming data
                                    handleData(data[0]);
                                    data[0] = null;
                                } else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                            packetBytes=null;
                        }else{
                            // TODO: 23-04-2016 if no bytes available....
                            /*
                            if (restartCounter > 20) {
                                restartCounter = 0;
                                resetBluetooth();
                            } else {
                                restartCounter++;
                            }
                            */
                        }
                    } catch (Exception ex) {
                        System.out.println("fejl:" + ex.toString());
                        stopWorker = true;
                    }
                }
            }
        });
        System.out.println("starting thread");
        workerThread.start();
    }


    /***
     * send data-string
     *
     * @param string
     * @throws IOException
     */
    private void sendData(String string) {
        String msg = string + "\r";
        try {
            mOutputStream.write(msg.getBytes());
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    /***
     * close bluetooth connection
     *
     * @throws IOException
     */
    private void closeBluetooth() throws IOException {
        stopWorker = true;
        mOutputStream.close();
        mInputStream.close();
        mSocket.close();
    }

    private void closeApp() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    System.exit(0);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        }, 2000);
    }

    private void turnOnBluetooth() {
        BluetoothAdapter.getDefaultAdapter().enable();
    }

    private void setUpBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        try {
            findBluetooth();
            openBluetooth();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}