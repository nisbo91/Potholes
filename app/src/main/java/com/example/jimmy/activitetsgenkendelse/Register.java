package com.example.jimmy.activitetsgenkendelse;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Register extends AppCompatActivity implements View.OnClickListener {


    Button bRegister;
    EditText etName, etAge, etUsername, etEmail, etPassword;
    String name, user_Age, user_Name,user_Email, user_Password;
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

                name=etName.getText().toString();
                user_Name=etUsername.getText().toString();
                user_Email=etEmail.getText().toString();
                user_Age=etAge.getText().toString();
                user_Password=etPassword.getText().toString();
                String method= "register";
                BackbroundTask backbroundTask= new BackbroundTask(this); //opretter ny
                backbroundTask.execute(method, name,user_Name,user_Password,user_Age,user_Email);


                break;

        }

    }
    public void processValue(String result)
    {
        //handle value
        System.out.println(result.toString());
        //Update GUI, show toast, etc..
        Functionality.langToast("Registration Success");
        finish();
    }
/*
public void userReg(View View)
{
    name=etName.getText().toString();
    user_Name=etUsername.getText().toString();
    user_Password=etPassword.getText().toString();
    user_Age=etAge.getText().toString();
    String method= "register";
    BackbroundTask backbroundTask= new BackbroundTask(this); //opretter ny
    backbroundTask.execute(method, name,user_Name,user_Password,user_Age);
    finish();

}
*/
    /*public void userLogin(View view)
    {
        user_Name = etUsername.getText().toString();
        user_Password = etPassword.getText().toString();
        String method = "login";
        BackbroundTask backgroundTask = new BackbroundTask(this);
        backgroundTask.execute(method,user_Name,user_Password);
    }*/

}