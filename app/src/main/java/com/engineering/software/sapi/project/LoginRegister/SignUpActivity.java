package com.engineering.software.sapi.project.LoginRegister;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.engineering.software.sapi.project.MainActivity;
import com.facebook.FacebookSdk;
import com.engineering.software.sapi.project.R;


import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SignUpCallback;



public class SignUpActivity extends Activity {

    private Button bSignUp;
    private EditText etEnterName, etEnterPass, etPassAgain, etEnterEmail, etEnterUserName;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_signup);


        bSignUp = (Button) findViewById(R.id.signUpButton);
        etEnterUserName = (EditText) findViewById(R.id.etEnterUserName);
        etEnterName = (EditText) findViewById(R.id.etEnterName);
        etEnterPass = (EditText) findViewById(R.id.etEnterPassword);
        etPassAgain = (EditText) findViewById(R.id.etPassAgain);
        etEnterEmail = (EditText) findViewById(R.id.etEnterEmail);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if ((currentUser != null) && ParseFacebookUtils.isLinked(currentUser)) {
            // Go to the user info activity
            showMainActivity();
        }

        etPassAgain.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == R.id.edittext_action_signup ||
                        actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    signup();
                    return true;
                }
                return false;
            }
        });


        bSignUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                signup();
            }
        });

    }

    private void showMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void signup() {
        final String username = etEnterUserName.getText().toString().trim();
        String name = etEnterName.getText().toString().trim();
        String email = etEnterEmail.getText().toString().trim();
        final String password = etEnterPass.getText().toString().trim();
        String passwordAgain = etPassAgain.getText().toString().trim();

        // Validate the sign up data
        boolean validationError = false;
        StringBuilder validationErrorMessage = new StringBuilder(getString(R.string.error_intro));
        if (username.length() == 0) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_username));
        }
        if (name.length() == 0) {
            if (validationError) {
                validationErrorMessage.append(getString(R.string.error_join));
            }
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_email));
        }
        if (email.length() == 0) {
            if (validationError) {
                validationErrorMessage.append(getString(R.string.error_join));
            }
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_email));
        }
        if (password.length() == 0) {
            if (validationError) {
                validationErrorMessage.append(getString(R.string.error_join));
            }
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_password));
        }
        if (!password.equals(passwordAgain)) {
            if (validationError) {
                validationErrorMessage.append(getString(R.string.error_join));
            }
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_mismatched_passwords));
        }
        validationErrorMessage.append(getString(R.string.error_end));

        // If there is a validation error, display the error
        if (validationError) {
            Toast.makeText(SignUpActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG)
                    .show();
            return;
        }

        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);

        final ProgressDialog dialog = new ProgressDialog(SignUpActivity.this);
        dialog.setMessage("Signing up. Please wait.");
        dialog.show();

        ParseUser currentUser = ParseUser.getCurrentUser();
        currentUser.logOut();

        //creating a new user
        ParseUser user = new ParseUser();
        user.setUsername(etEnterUserName.getText().toString());
        user.setPassword(etEnterPass.getText().toString());
        user.setEmail(etEnterEmail.getText().toString());
        user.put("name",name);

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                dialog.dismiss();
                if (e != null) {
                    // Show the error message
                    Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    // Start an intent for the dispatch activity
                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });

    }

}
