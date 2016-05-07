package com.example.jimmy.activitetsgenkendelse;

import android.os.Handler;
import android.widget.Toast;

/**
 * Created by Jimmy on 10-04-2016.
 */
public class Functionality {

    public static Handler forgrundstråd = new Handler();

    public static void langToast(final String txt) {
        forgrundstråd.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Login.instans, txt, Toast.LENGTH_LONG).show();
            }
        });
    }
}
