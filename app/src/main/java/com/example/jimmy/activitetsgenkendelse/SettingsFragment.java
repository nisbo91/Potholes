package com.example.jimmy.activitetsgenkendelse;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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
public class SettingsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener{

    static SettingsFragment synligInstans;
    private TextView iMEITextView;
    private TextView carModelTextView;
    private FragmentActivityCommunication fragmentCommunication;
    private static Switch potholeAlertSwitch;
    private TextView alertRadiusTextView;
    private TextView kmTextView;
    private SeekBar kmSeekBar;
    private FragmentActivity context;


    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        context = getActivity();
        fragmentCommunication =(FragmentActivityCommunication)context;
    }
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

        setiMEI();
        setCarModel();
        setPotholeAlert();
        setAlertRadius();

        potholeAlertSwitch.setOnCheckedChangeListener(this);

        return v;
    }

    private void setAlertRadius() {
        MainActivity activity = (MainActivity) getActivity();
        //kmTextView.setText(activity.getAlertRadius());
    }

    private void setPotholeAlert() {
        MainActivity activity = (MainActivity) getActivity();
        potholeAlertSwitch.setChecked(activity.getPotholeAlert());
    }

    private void setCarModel() {
        MainActivity activity = (MainActivity) getActivity();
        carModelTextView.setText("Car Model: "+activity.getCarModel());
    }

    private void setiMEI() {
        MainActivity activity = (MainActivity) getActivity();
        iMEITextView.setText("IMEI: "+activity.getiMEI());
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.potholeAlertSwitch:
                System.out.println(potholeAlertSwitch.isChecked());
                fragmentCommunication.setData(potholeAlertSwitch.isChecked(), String.valueOf(kmTextView));
                break;
        }
    }

}
