package com.engineering.software.sapi.project.Profile;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseUser;

/**
 * Created by Zsolt on 2015. 11. 22..
 */
public class OnButtonClick implements View.OnClickListener {

    private Button btn;
    private EditText name, phone, email;
    private ParseUser currentUser;

    public OnButtonClick(Button b, EditText name, EditText phone, EditText email, ParseUser currentUser) {
        this.btn = b;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.currentUser = currentUser;
    }

    @Override
    public void onClick(View view) {
        String btnText;
        btnText = btn.getText().toString();


        if (btnText.equals("Edit")) {
            name.setEnabled(true);
            phone.setEnabled(true);
            email.setEnabled(true);
            btn.setText("Save");
        }
        if (btnText.equals("Save")) {
            name.setEnabled(false);
            phone.setEnabled(false);
            email.setEnabled(false);
            currentUser.setUsername(name.getText().toString().trim());
            currentUser.setEmail(email.getText().toString().trim());
            currentUser.put("phoneNumber", phone.getText().toString().trim());
            currentUser.saveInBackground();
            btn.setText("Edit");
        }
    }
}
