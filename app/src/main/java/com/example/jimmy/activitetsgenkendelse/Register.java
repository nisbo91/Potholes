package com.example.jimmy.activitetsgenkendelse;

import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class Register extends AppCompatActivity implements View.OnClickListener {

    String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z]+\\.+[a-z]+";
    String username = "[a-zA-Z0-9]{5,15}$";
    String password = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,20})";

    Button bRegister;
    EditText etName, etAge, etUsername, etEmail, etPassword;
    String name, user_Age, user_Name,user_Email, user_Password, user_IMEI;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        etName = (EditText) findViewById(R.id.etName);
        etAge = (EditText) findViewById(R.id.etAge);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etEmail =(EditText)findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        bRegister = (Button) findViewById(R.id.bRegister);

        bRegister.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bRegister:
                if(etUsername.getText().toString().matches(username)){
                    if(etEmail.getText().toString().matches(emailPattern)){
                        if(etPassword.getText().toString().matches(password)){
                            name=etName.getText().toString();
                            user_Name=etUsername.getText().toString();
                            user_Email=etEmail.getText().toString();
                            user_Age=etAge.getText().toString();
                            user_Password=etPassword.getText().toString();
                            user_IMEI = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                            String method= "register";
                            BackbroundTask backbroundTask= new BackbroundTask(this); //opretter ny
                            backbroundTask.execute(method, name,user_Name,user_Password,user_Age,user_Email,user_IMEI);
                        }
                        else{
                            Functionality.langToast("Password isn't valid\n" +
                                    "password most contain: 6-20 characters and small and big letters");
                        }
                    }
                    else{
                        Functionality.langToast("Invalid email");
                    }
                }
                else{
                    Functionality.langToast("Invalid username \nmust contain 5-15 letters or numbers");
                }
                break;
        }

    }

}
