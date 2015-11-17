package com.engineering.software.sapi.project.LoginRegister;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.engineering.software.sapi.project.R;

public class Registration extends Activity implements View.OnClickListener {

    Button bSignUp,fbButton;
    EditText etEnterName, etEnterPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        bSignUp = (Button) findViewById(R.id.signUpButton);
        etEnterName = (EditText) findViewById(R.id.etEnterName);
        etEnterPass = (EditText) findViewById(R.id.etEnterPassword);

        bSignUp.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.signUpButton:

                String email = etEnterName.getText().toString();
                String password = etEnterPass.getText().toString();

                User registeredData = new User(email,password);

                break;
        }
    }
}
