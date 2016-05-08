package com.example.jimmy.activitetsgenkendelse;

/**
 * Created by Jesper de Fries on 29-04-2016.
 */
public class BluetoothData {

    public String speed;
    public String speedTs;
    public String steering;
    public String steeringTs;
    public String throttle;
    public String throttleTs;

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

}
