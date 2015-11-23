package com.engineering.software.sapi.project.Profile;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.parse.ParseUser;

/**
 * Created by Zsolt on 2015. 11. 22..
 */
public class EditProfile implements View.OnClickListener {

    private Button btn;
    private EditText name, phone, email;
    private ParseUser currentUser;
    private ImageView profilePic;

    public EditProfile(Button b, EditText name, EditText phone, EditText email, ImageView profilePic, ParseUser currentUser) {
        this.btn = b;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.profilePic = profilePic;
        this.currentUser = currentUser;
    }

    @Override
    public void onClick(View view) {
        String btnText;
        btnText = btn.getText().toString();

        if (btn.isPressed() && btnText.equals("Edit")) {
            name.setEnabled(true);
            phone.setEnabled(true);
            email.setEnabled(true);
            profilePic.setClickable(true);
            profilePic.setFocusable(true);
            btn.setText("Save");
        }
        if (btn.isPressed() && btnText.equals("Save")) {
            name.setEnabled(false);
            phone.setEnabled(false);
            email.setEnabled(false);
            profilePic.setClickable(false);
            profilePic.setFocusable(false);
            currentUser.put("name", name.getText().toString().trim());
            currentUser.setEmail(email.getText().toString().trim());
            currentUser.put("phoneNumber", phone.getText().toString().trim());
            currentUser.saveInBackground();
            btn.setText("Edit");
        }
    }
}
