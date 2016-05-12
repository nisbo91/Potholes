package com.example.jimmy.activitetsgenkendelse;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Login extends AppCompatActivity implements View.OnClickListener {

    static Login instans;
    Button bLogin;
    EditText etUsername, etPassword;
    TextView tvRegisterLInk;
    String user_Name, user_Password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        instans = this;

        etUsername = (EditText)findViewById(R.id.etUsername);
        etPassword = (EditText)findViewById(R.id.etPassword);
        bLogin = (Button)findViewById(R.id.bLogin);
        tvRegisterLInk=(TextView)findViewById(R.id.tvRegisterLInk);
        bLogin.setOnClickListener(this);
        tvRegisterLInk.setOnClickListener(this);

    }
    @Override
    public void onClick(View v){
        switch (v.getId()){

            case R.id.tvRegisterLInk:

                startActivity(new Intent(this,Register.class));

                break;
            case R.id.bLogin:
                user_Name = etUsername.getText().toString();
                user_Password = etPassword.getText().toString();
                String method = "login";
                BackbroundTask backgroundTask = new BackbroundTask(this);
                backgroundTask.execute(method,user_Name,user_Password);
                break;
        }
    }

}
