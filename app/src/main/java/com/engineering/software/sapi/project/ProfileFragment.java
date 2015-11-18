package com.engineering.software.sapi.project;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


public class ProfileFragment extends Fragment {

    ImageView imageView;
    Button pBtn;
    EditText name,phone,email;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v;
        v = inflater.inflate(R.layout.fragment_profile, null);

        imageView = (ImageView)v.findViewById(R.id.profile_picture);
        pBtn = (Button)v.findViewById(R.id.profile_button);
        name = (EditText)v.findViewById(R.id.name);
        phone = (EditText)v.findViewById(R.id.phone);
        email = (EditText)v.findViewById(R.id.email);

        pBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String btnText;
                btnText = pBtn.getText().toString();
                if (btnText.equals("Edit")){
                    name.setFocusableInTouchMode(true);
                    phone.setFocusableInTouchMode(true);
                    email.setFocusableInTouchMode(true);
                    pBtn.setText("Save");
                }else if (btnText.equals("Save")){
                    name.setFocusable(false);
                    phone.setFocusable(false);
                    email.setFocusable(false);
                    pBtn.setText("Edit");
                }
            }
        });
        return v;
    }


}
