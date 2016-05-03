package com.example.jimmy.activitetsgenkendelse;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

/**
 * Created by Jimmy on 10-04-2016.
 */
public class SettingsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    static SettingsFragment synligInstans;
    private TextView iMEITextView;
    private TextView carModelTextView;
    private static Switch potholeAlertSwitch;
    private TextView alertRadiusTextView;
    private TextView kmTextView;
    private SeekBar kmSeekBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        synligInstans = this;

        Log.d("LoginFragment", "Fragment onCreate()");
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        iMEITextView =  (TextView) v.findViewById(R.id.IMEITextView);
        carModelTextView =  (TextView) v.findViewById(R.id.carModelTextView);
        potholeAlertSwitch =  (Switch) v.findViewById(R.id.potholeAlertSwitch);
        alertRadiusTextView =  (TextView) v.findViewById(R.id.alertRadiusTextView);
        kmTextView =  (TextView) v.findViewById(R.id.kmTextView);
        kmSeekBar =  (SeekBar) v.findViewById(R.id.kmSeekBar);

        potholeAlertSwitch.setOnCheckedChangeListener(this);

        return v;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.potholeAlertSwitch:
                if(potholeAlertSwitch.isChecked()){

                }
                else{
                    Log.i("potholeAlert","disabled");
                }
                break;
        }
    }

    public static boolean CheckAlert() {
        if (potholeAlertSwitch.isChecked()){
            return true;
        }
        return false;
    }
}
