package com.engineering.software.sapi.project;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class SendEmailFragment extends Fragment {

    private static final String EMAIL_RECIEVER = "tamas_27@yahoo.co.uk";
    /*private TextInputLayout layoutSender;*/
    private TextInputLayout layoutSubject;

    /*private EditText editTextSender;*/
    private EditText editTextSubject;
    private EditText editTextEmailText;

    private FloatingActionButton fabSendEmail;


    public SendEmailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_send_email, container, false);

        initialize(view);

        fabSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });


        return view;
    }

    private void initialize(View view) {
        /*layoutSender = (TextInputLayout) view.findViewById(R.id.layout_email_sender);*/
        layoutSubject = (TextInputLayout) view.findViewById(R.id.layout_email_subject);

        /*editTextSender = (EditText) view.findViewById(R.id.edit_text_email_sender);*/
        editTextSubject = (EditText) view.findViewById(R.id.edit_text_email_subject);
        editTextEmailText = (EditText) view.findViewById(R.id.edit_text_email_text);

        fabSendEmail = (FloatingActionButton) view.findViewById(R.id.fab_send_email);

    }

    private void sendEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");

        emailIntent.putExtra(Intent.EXTRA_EMAIL, EMAIL_RECIEVER);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, editTextSubject.getText().toString());
        emailIntent.putExtra(Intent.EXTRA_TEXT, editTextEmailText.getText().toString());

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail with..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

}
