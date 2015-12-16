package com.engineering.software.sapi.project.LoginRegister;

import android.app.Activity;


import android.app.Dialog;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.engineering.software.sapi.project.MainActivity;
import com.engineering.software.sapi.project.R;
import com.facebook.login.widget.LoginButton;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

import java.util.Arrays;
import java.util.List;


public class LoginActivity extends Activity {

    Button bLogin;
    EditText etUserName, etPassword;
    TextView tvForgPass, tvSignUp;
    CheckBox saveLoginCheckBox;
    private LoginButton fbButton;


    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;
    private TextInputLayout inputLayoutUser,inputLayoutPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        bLogin = (Button) findViewById(R.id.loginButton);
        etUserName = (EditText) findViewById(R.id.etUserName);
        etPassword = (EditText) findViewById(R.id.etPassword);
        tvForgPass = (TextView) findViewById(R.id.tvForgotPass);
        tvSignUp = (TextView) findViewById(R.id.tvSignUp);
        saveLoginCheckBox = (CheckBox) findViewById(R.id.cbRememberMe);
        fbButton = (LoginButton) findViewById(R.id.fbButton);
        inputLayoutUser = (TextInputLayout) findViewById(R.id.input_layout_username);
        inputLayoutPass = (TextInputLayout) findViewById(R.id.input_layout_password);

        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        saveLogin = loginPreferences.getBoolean("saveLogin", false);
        if (saveLogin == true) {
            etUserName.setText(loginPreferences.getString("username", ""));
            etPassword.setText(loginPreferences.getString("password", ""));
            saveLoginCheckBox.setChecked(true);
        }


        etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == R.id.edittext_action_login ||
                        actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    login();
                    return true;
                }
                return false;
            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrate(v);
            }
        });

        bLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                login();
            }
        });

        tvForgPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotPass(v);
            }
        });

        fbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fbSignUp();
            }

        });

    }


    private void registrate(View v) {
        Intent i = new Intent(this, SignUpActivity.class);
        startActivity(i);
    }

    private void fbSignUp(){
        List<String> permissions = Arrays.asList("public_profile");
        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (user == null) {
                    Toast.makeText(LoginActivity.this, "The user cancelled the Facebook login.", Toast.LENGTH_LONG).show();

                } else if (user.isNew()) {
                    Toast.makeText(LoginActivity.this, "The user signed up and logged in through Facebook!", Toast.LENGTH_LONG).show();
                    GetUserDetails getUserDetails = new GetUserDetails();
                    getUserDetails.makeMeRequest();
                    showMainActivity();
                } else {
                    Toast.makeText(LoginActivity.this, "The user logged in through Facebook!", Toast.LENGTH_LONG).show();
                    GetUserDetails getUserDetails = new GetUserDetails();
                    getUserDetails.makeMeRequest();
                    showMainActivity();
                }
            }
        });
    }

    private void showMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void forgotPass(View v) {
        final Dialog forgotPassDialog = new Dialog(LoginActivity.this);
        forgotPassDialog.setContentView(R.layout.forgotpass_dialog);
        forgotPassDialog.setTitle("Forgot your password?");


        tvForgPass.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                final Button sendMail = (Button) forgotPassDialog.findViewById(R.id.sendButton);

                forgotPassDialog.show();

                sendMail.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                         EditText enterMail = (EditText) forgotPassDialog.findViewById(R.id.etEnterAddress);
                        final String to = enterMail.getText().toString();
                        ParseUser.requestPasswordResetInBackground(to,
                                new RequestPasswordResetCallback() {
                                    @Override
                                    public void done(com.parse.ParseException e) {
                                        if (e == null) {
                                            Toast.makeText(LoginActivity.this, "An email was successfully sent with reset instructions."+to, Toast.LENGTH_LONG).show();
                                            forgotPassDialog.dismiss();
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Something went wrong." + to, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                    }
                });

            }
        });

    }

    private void login() {
        final String username = etUserName.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();

        // Validate the log in data
        boolean validationError = false;
        StringBuilder validationErrorMessage = new StringBuilder(getString(R.string.error_intro));
        if (username.length() == 0) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_username));
        }
        if (password.length() == 0) {
            if (validationError) {
                validationErrorMessage.append(getString(R.string.error_join));
            }
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_password));
        }
        validationErrorMessage.append(getString(R.string.error_end));

        // If there is a validation error, display the error
        if (validationError) {
            Toast.makeText(LoginActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG)
                    .show();
            return;
        }


        // Set up a progress dialog
        final ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
        dialog.setMessage(getString(R.string.progress_login));
        dialog.show();
        // Call the Parse login method

        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                dialog.dismiss();
                if (e != null) {
                    // Show the error message
                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    // Start an intent for the MainActivity
                    if (saveLoginCheckBox.isChecked()) {
                        loginPrefsEditor.putBoolean("saveLogin", true);
                        loginPrefsEditor.putString("username", username);
                        loginPrefsEditor.putString("password", password);
                        loginPrefsEditor.commit();
                    } else {
                        loginPrefsEditor.clear();
                        loginPrefsEditor.commit();
                    }
                    Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("username", username);
                    finish();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

}




