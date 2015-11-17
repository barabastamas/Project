package com.engineering.software.sapi.project.LoginRegister;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.engineering.software.sapi.project.MainActivity;
import com.engineering.software.sapi.project.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button bLogin;
    EditText etUserName, etPassword;
    TextView tvForgPass,tvSignUp;

    UserLocalStore userLocalStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        bLogin = (Button) findViewById(R.id.loginButton);
        etUserName = (EditText) findViewById(R.id.etUserName);
        etPassword = (EditText) findViewById(R.id.etPassword);
        tvForgPass = (TextView) findViewById(R.id.tvForgotPass);
        tvSignUp = (TextView) findViewById(R.id.tvSignUp);

        bLogin.setOnClickListener(this);
        tvSignUp.setOnClickListener(this);

        userLocalStore = new UserLocalStore(this);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.loginButton:
                User user = new User(null,null);

                userLocalStore.storeUserData(user);
                userLocalStore.setUserLoggedIn(true);
                Intent main = new Intent(this, MainActivity.class);
                startActivity(main);
                finish();
                break;
            case R.id.tvSignUp:
                Intent intent = new Intent(this,Registration.class);
                startActivity(intent);
                break;
        }
    }
}

