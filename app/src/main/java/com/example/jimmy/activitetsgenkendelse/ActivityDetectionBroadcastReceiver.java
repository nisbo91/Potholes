package com.example.jimmy.activitetsgenkendelse;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.Date;

/**
 * Created by Jimmy on 11-03-2016.
 */
public class ActivityDetectionBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (ActivityRecognitionResult.hasResult(intent)) {

            ActivityRecognitionResult res = ActivityRecognitionResult.extractResult(intent);

            //DetectedActivity akt = res.getMostProbableActivity();
            //String aktivitetsnavn = getBeskrivelse(akt.getType());

            //String tekst = aktivitetsnavn+" med "+ akt.getConfidence() +" procents sandsynlighed";
            //TekstTilTale.instans(context).tal(tekst);

            if (MainActivity.instans != null) {
                MainActivity.instans.DetectedActivity.append("\n" + new Date() + "\n");
                for (DetectedActivity a : res.getProbableActivities()) {
                    String log = a.getType() + " " + getBeskrivelse(a.getType()) + " " + +a.getConfidence() + "%\n";
                    MainActivity.instans.DetectedActivity.append(log);
                }
            }
        }
    }

    private String getBeskrivelse(int activityType) {
        switch (activityType) {
            case DetectedActivity.IN_VEHICLE:
                return "Du kører bil";
            case DetectedActivity.ON_BICYCLE:
                return "Du cykler";
            case DetectedActivity.ON_FOOT:
                return "Du er til fods";
            case DetectedActivity.WALKING:
                return "Du går";
            case DetectedActivity.STILL:
                return "Telefonen ligger stille";
            case DetectedActivity.TILTING:
                return "Du holder telefonen";
            case DetectedActivity.UNKNOWN:
        }
        return "Ukendt aktivitet";
    }
}
