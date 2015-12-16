package com.engineering.software.sapi.project;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.engineering.software.sapi.project.LoginRegister.LoginActivity;
import com.parse.ParseUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class LogOutFragment extends Fragment {


    public LogOutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        logout();
        return inflater.inflate(R.layout.fragment_log_in, container, false);
    }

    private void logout(){
        ParseUser.logOut();
        startActivity(new Intent(getActivity(), LoginActivity.class));
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.remove("username");
        getActivity().finish();
    }


}
