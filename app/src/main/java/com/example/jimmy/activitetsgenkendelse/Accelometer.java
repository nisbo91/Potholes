package com.example.jimmy.activitetsgenkendelse;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;

/**
 * Created by Jimmy on 15-04-2016.
 */
public class Accelometer implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mLinearAccelerometer;
    float[] linear_acceleration = new float[]{0,0,0};
    private String lastDataAccelometer;
    private ArrayList<String> dataAccelometer;

    public void AccelometerInit(Context context){
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mLinearAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        lastDataAccelometer = new String();
        dataAccelometer = new ArrayList<String>();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        linear_acceleration[0] = event.values[SensorManager.DATA_X];
        linear_acceleration[1] = event.values[SensorManager.DATA_Y];
        linear_acceleration[2] = event.values[SensorManager.DATA_Z];
        //System.out.println("\nX: " + linear_acceleration[0] + "\nY: " + linear_acceleration[1] + "\nZ: " + linear_acceleration[2] + "\n");
        //Log.i("plottegraf", "X:" + linear_acceleration[0] + " Y:" + linear_acceleration[1] + " Z:" + linear_acceleration[2]);
        lastDataAccelometer =(linear_acceleration[0] + "," + linear_acceleration[1] + "," + linear_acceleration[2]);
        dataAccelometer.add(linear_acceleration[0] + "," + linear_acceleration[1] + "," + linear_acceleration[2]);
    }

    /*public String returnLastData(){
        if(lastDataAccelometer != null){
            return lastDataAccelometer;
        }
        else{
            System.out.println("no data");
            return null;
        }
    }*/

    public ArrayList<String> returnData(){
        if(lastDataAccelometer != null){
            return dataAccelometer;
        }
        else{
            System.out.println("no data");
            return null;
        }
    }
    public void clearData(){
        dataAccelometer.removeAll(dataAccelometer);

    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        switch (accuracy) {
            case 0:
                System.out.println("Accuracy changed: Unreliable");
                Functionality.langToast("Accuracy changed: Unreliable");
                MapFragment.accelometerAccuracyIndicator = 0;
                break;
            case 1:
                System.out.println("Accuracy changed: Low Accuracy");
                Functionality.langToast("Accuracy changed: Low Accuracy");
                MapFragment.accelometerAccuracyIndicator = 1;
                break;
            case 2:
                System.out.println("Accuracy changed: Medium Accuracy");
                Functionality.langToast("Accuracy changed: Medium Accuracy");
                MapFragment.accelometerAccuracyIndicator = 2;
                break;
            case 3:
                System.out.println("Accuracy changed: High Accuracy");
                Functionality.langToast("Accuracy changed: High Accuracy");
                MapFragment.accelometerAccuracyIndicator = 3;
                break;
        }
    }

    public void pauseAccelemeter(){
        mSensorManager.unregisterListener(this);
    }

    public void resumeAccelemeter(){
        mSensorManager.registerListener(this, mLinearAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }
}
