package com.example.jimmy.activitetsgenkendelse;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by famtved1 on 10-04-2016.
 */
public class BackbroundTask extends AsyncTask<String,Void,String> {
    AlertDialog alertDialog;
    Context ctx;

    //constuktor
    BackbroundTask(Context ctx) {
       this.ctx = ctx;
    }

    @Override
    protected void onPreExecute() {
        alertDialog = new AlertDialog.Builder(ctx).create();
        alertDialog.setTitle("Login information");
    }

    @Override
    protected String doInBackground(String... params) {
        String reg_url = "http://87.72.39.104/potholeappdb/register.php";
        String login_url = "http://87.72.39.104/potholeappdb/register.php";
        //String reg_url = "http://10.0.2.2/potholeappdb/register.php";
       //String login_url = "http://10.0.2.2/potholeappdb/login.php";
      //  String reg_url = "http://80.62.116.107/potholeappdb/register.php";
      //  String login_url = "http://80.62.116.107/potholeappdb/login.php";

    //    String reg_url = "http://192.168.0.201/potholeappdb/register.php";
    //    String login_url = "http://192.168.0.201/potholeappdb/login.php";

        String method = params[0];
        if (method.equals("register")) {
            String name = params[1];
            String user_name = params[2];
            String user_password = params[3];
            String age = params[4];
            URL url = null;
            try {
                url = new URL(reg_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));
                String data = URLEncoder.encode("user", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8") + "&" +
                        URLEncoder.encode("user_name", "UTF-8") + "=" + URLEncoder.encode(user_name, "UTF-8") + "&" +
                        URLEncoder.encode("user_password", "UTF-8") + "=" + URLEncoder.encode(user_password, "UTF-8") + "&" +
                        URLEncoder.encode("age", "UTF-8") + "=" + URLEncoder.encode(age, "UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();
                InputStream IS = httpURLConnection.getInputStream();
                IS.close();
                return "Registration Success...";
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        } else if (method.equals("login")) {
            String login_name = params[1];
            String login_password = params[2];
            try {
                URL url = new URL(login_url);
                try {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    String data = URLEncoder.encode("user_name", "UTF-8") + "=" + URLEncoder.encode(login_name, "UTF-8") + "&" +
                            URLEncoder.encode("user_pass", "UTF-8") + "=" + URLEncoder.encode(login_password, "UTF-8");
                    bufferedWriter.write(data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();

                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                    String respons = "";
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        respons += line;
                    }
                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    return respons;

                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

        }
        else if (method.equals("Pothole")) {
            String latitude = params[1];
            String longitude = params[2];
            String mobile_accelerometer_data = params[3];
            String OBD_car_speed = params[4];
            String OBD_throttle = params[5];
            String OBD_steering_wheel_pos = params[6];
            String OBD_odometer = params[7];
            try {
                URL url = new URL(login_url);
                try {
                    url = new URL(reg_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoInput(true);
                    OutputStream OS = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));
                    String data = URLEncoder.encode("latitude", "UTF-8") + "=" + URLEncoder.encode(latitude, "UTF-8") + "&" +
                            URLEncoder.encode("longitude", "UTF-8") + "=" + URLEncoder.encode(longitude, "UTF-8") + "&" +
                            URLEncoder.encode("mobile_accelerometer_data", "UTF-8") + "=" + URLEncoder.encode(mobile_accelerometer_data, "UTF-8") + "&" +
                            URLEncoder.encode("OBD_car_speed", "UTF-8") + "=" + URLEncoder.encode(OBD_car_speed, "UTF-8") + "&" +
                            URLEncoder.encode("OBD_throttle", "UTF-8") + "=" + URLEncoder.encode(OBD_throttle, "UTF-8") + "&" +
                            URLEncoder.encode("OBD_steering_wheel_pos", "UTF-8") + "=" + URLEncoder.encode(OBD_steering_wheel_pos, "UTF-8") + "&" +
                            URLEncoder.encode("OBD_odometer", "UTF-8") + "=" + URLEncoder.encode(OBD_odometer, "UTF-8");
                    bufferedWriter.write(data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    OS.close();
                    InputStream IS = httpURLConnection.getInputStream();
                    IS.close();
                    return "Pothole Success...";

                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

        }
        else{
            return null;
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String result) {
        //  super.onPostExecute(aVoid);
        if (result != null){
            if (result.equals("Registration Success...")) // viser at registering af data lykkes
            {
                //Functionality.langToast(result);
                //Register register = new Register();
                //register.processValue(result);
                ((Activity)ctx).finish();

            }
            if (result.equals("Login Success...")) // viser at registering af data lykkes
            {
                //Functionality.langToast(result);
                Intent intent = new Intent(ctx, MainActivity.class);
                ctx.startActivity(intent);
                ((Activity)ctx).finish();
            }
            if (result.equals("Pothole Success...")) // viser at registering af data lykkes
            {
                Functionality.langToast(result);
                //Register register = new Register();
                //register.processValue(result);

            }
            else // viser respons fra database
            {
                System.out.println(result.toString());
                alertDialog.setMessage("Failed...Please try again!");
                alertDialog.show();
            }
        }
        else // viser respons fra database
        {
            alertDialog.setMessage("Failed...Please try again!");
            alertDialog.show();
        }

    }
}
