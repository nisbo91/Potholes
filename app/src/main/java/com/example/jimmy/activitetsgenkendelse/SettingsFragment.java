package com.example.jimmy.activitetsgenkendelse;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Jimmy on 10-04-2016.
 */
public class SettingsFragment extends Fragment{

    static SettingsFragment synligInstans;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        synligInstans = this;

        Log.d("LoginFragment", "Fragment onCreate()");
        View map = inflater.inflate(R.layout.fragment_settings, container, false);

        return map;
    }
}
