package com.example.jimmy.activitetsgenkendelse;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

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
        //IP når der simuleres på simu mobil Wampserver
        //      String reg_url = "http://10.0.2.2/potholeappdb/register.php";
        //      String login_url = "http://10.0.2.2/potholeappdb/login.php";
        // IP udefra
        //  String reg_url = "http://80.62.116.149/potholeappdb/register.php";
        //  String login_url = "http://80.62.116.149/potholeappdb/login.php";
        // IP lokalt
        //  String reg_url = "http://192.168.0.201/potholeappdb/register.php";
        //   String login_url = "http://192.168.0.201/potholeappdb/login.php";
        //IP der bruges Jespers Lamp server fra app
        String reg_url = "http://87.72.39.104/potholeappdb/register.php";
        String login_url = "http://87.72.39.104/potholeappdb/login.php";
        String uploadData_url = "http://87.72.39.104/potholeappdb/uploadData.php";
        String getData_url = "http://87.72.39.104/potholeappdb/getData.php";
        String detect_order_info_set_url = "http://87.72.39.104/potholeappdb/detect_order_info_set.php";
        String detect_order_info_Get_url = "http://87.72.39.104/potholeappdb/detect_order_info_Get.php";




        String method = params[0];
        if (method.equals("register")) {
            String name = params[1];
            String user_name = params[2];
            String user_password = params[3];
            String age = params[4];
            String email =params[5];
            String IMEI_number =params[6];
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
                        URLEncoder.encode("age", "UTF-8") + "=" + URLEncoder.encode(age, "UTF-8")+ "&" +
                        URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8")+ "&" +
                        URLEncoder.encode("IMEI_number", "UTF-8") + "=" + URLEncoder.encode(IMEI_number, "UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();
                InputStream IS = httpURLConnection.getInputStream();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IS, "iso-8859-1"));
                String respons = "";
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    respons += line;
                }
                bufferedReader.close();
                IS.close();
                httpURLConnection.disconnect();
                return respons;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
            if (method.equals("login")) {
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

            if (method.equals("uploadData")) {
                String upload_langitude = params[1];
                String upload_longtitude = params[2];
                String upload_accelerometer_data = params[3];
                String upload_car_speed = params[4];
                String upload_throttle = params[5];
                String upload_steering_wheel = params[6];
                String upload_odometer = params[7];


                try {
                    URL url = new URL(uploadData_url);
                    try {
                        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.setDoOutput(true);
                        httpURLConnection.setDoInput(true);
                        OutputStream outputStream = httpURLConnection.getOutputStream();

                        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                        String data = URLEncoder.encode("latitude", "UTF-8") + "=" + URLEncoder.encode(upload_langitude, "UTF-8") + "&" +
                                URLEncoder.encode("longitude", "UTF-8") + "=" + URLEncoder.encode(upload_longtitude, "UTF-8") + "&" +
                                URLEncoder.encode("mobile_accelerometer_data", "UTF-8") + "=" + URLEncoder.encode(upload_accelerometer_data, "UTF-8") + "&" +
                                URLEncoder.encode("OBD_car_speed", "UTF-8") + "=" + URLEncoder.encode(upload_car_speed, "UTF-8") + "&" +
                                URLEncoder.encode("OBD_throttle", "UTF-8") + "=" + URLEncoder.encode(upload_throttle, "UTF-8") + "&" +
                                URLEncoder.encode("OBD_sterring_wheel", "UTF-8") + "=" + URLEncoder.encode(upload_steering_wheel, "UTF-8") + "&" +
                                URLEncoder.encode("OBD_odometer", "UTF-8") + "=" + URLEncoder.encode(upload_odometer, "UTF-8");


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


            if (method.equals("getData")) { ///TODO
                String upload_langitude = params[1];
                String upload_longtitude = params[2];

                try {
                    URL url = new URL(getData_url);
                    try {
                        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.setDoOutput(true);
                        httpURLConnection.setDoInput(true);
                        OutputStream outputStream = httpURLConnection.getOutputStream();
                        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                        String data = URLEncoder.encode("latitude", "UTF-8") + "=" + URLEncoder.encode(upload_langitude, "UTF-8") + "&" +
                                URLEncoder.encode("longitude", "UTF-8") + "=" + URLEncoder.encode(upload_longtitude, "UTF-8");
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


            if (method.equals("detect_order_info_Set")) {
                String set_pothole_nr = params[1];
                String set_user_id = params[2];
                URL url = null;
                try {
                    url = new URL(detect_order_info_set_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    String data = URLEncoder.encode("user", "UTF-8") + "=" + URLEncoder.encode(set_pothole_nr, "UTF-8") + "&" +
                            URLEncoder.encode("user_name", "UTF-8") + "=" + URLEncoder.encode(set_user_id, "UTF-8");
                    ;
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

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    return null;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }


            }

            if (method.equals("detect_order_info_Get")) {
                String upload_pothole_nr = params[1];

                try {
                    URL url = new URL(detect_order_info_Get_url);
                    try {
                        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.setDoOutput(true);
                        httpURLConnection.setDoInput(true);
                        OutputStream outputStream = httpURLConnection.getOutputStream();
                        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                        String data = URLEncoder.encode("pothole_nr", "UTF-8") + "=" + URLEncoder.encode(upload_pothole_nr, "UTF-8");
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
            }else{
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
        try{
            switch (result) {
                case "Data Insertion Succes...":
                    Functionality.langToast("Register confirmed");
                    ((Activity)ctx).finish();
                    break;

                case "Login Success...":
                    Functionality.langToast(result);
                    Intent intent = new Intent(ctx, MainActivity.class);
                    ctx.startActivity(intent);
                    ((Activity)ctx).finish();
                    break;

                case "Uploading data was a Success...":
                    Functionality.langToast(result);
                    break;

                case "pothole_nr already found, it's number is :":
                    Functionality.langToast(result);
                    break;

                case "Data Insertion Succes":
                    Functionality.langToast("Pothole detected");
                    break;

                case "Detect_order is number: ":
                    Functionality.langToast(result);
                    break;
                default:
                    alertDialog.setMessage(result);
                    alertDialog.show();
                    break;

            /*if (result.equals("Registration Success...")) // viser at registering af data lykkes
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
                alertDialog.setMessage(result);
                alertDialog.show();
            }*/
            }
        }
        catch (Exception e){
            Log.e("error",e.toString());
        }
    }
}
