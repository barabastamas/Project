package com.engineering.software.sapi.project.Profile;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.engineering.software.sapi.project.LoginRegister.LoginActivity;
import com.engineering.software.sapi.project.R;
import com.parse.ParseUser;


public class ProfileFragment extends Fragment {

    private Button pBtn;
    private ImageView imageView;
    private EditText name, phone, email;
    private TextView rating;
    private String _name, _phone, _email, _rating;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v;
        v = inflater.inflate(R.layout.fragment_profile, null);

        imageView = (ImageView) v.findViewById(R.id.profile_picture);
        pBtn = (Button) v.findViewById(R.id.profile_button);
        name = (EditText) v.findViewById(R.id.name);
        phone = (EditText) v.findViewById(R.id.phone);
        email = (EditText) v.findViewById(R.id.email);
        rating = (TextView) v.findViewById(R.id.rating);

        name.setEnabled(false);
        phone.setEnabled(false);
        email.setEnabled(false);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            try {
                _name = currentUser.getUsername();
                _phone = currentUser.get("phoneNumber").toString();
                _email = currentUser.getEmail();
                _rating = currentUser.get("rating").toString();
            }catch (NullPointerException e){
                Log.d("Valami","Null");
            }
            name.setText(_name);
            phone.setText(_phone);
            email.setText(_email);
            rating.setText(_rating);

        } else {
            Toast.makeText(getContext(), "Session lost. Please log in again!", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getContext(), LoginActivity.class));
            getActivity().finish();
        }

        pBtn.setOnClickListener(new OnButtonClick(pBtn, name, phone, email, currentUser));
        return v;
    }

}
