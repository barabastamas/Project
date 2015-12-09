package com.engineering.software.sapi.project;


import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {
    FragmentManager fragmentManager,fragmentManager1;
    Fragment fragment,fragment1;
    FragmentTransaction ft,ft1;
    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        Button button = (Button) v.findViewById(R.id.addNewButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Message", Toast.LENGTH_SHORT).show();
                Log.d("Log", "Button was pressed");
                fragmentManager = getFragmentManager();
                fragment = new AddRouteFragment();
                ft = fragmentManager.beginTransaction();
                ft.replace(R.id.content, fragment);
                ft.addToBackStack("AddRouteFragment");
                ft.commit();
            }
        });

        Button button1 = (Button) v.findViewById(R.id.searchButton);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Search", Toast.LENGTH_SHORT).show();
                Log.d("Log1", "Button was pressed");
                fragmentManager1 = getFragmentManager();
                fragment1 = new SearchRouteFragment();
                ft1 = fragmentManager1.beginTransaction();
                ft1.replace(R.id.content,fragment1);
                ft1.addToBackStack("SearchRouteFragment");
                ft1.commit();
            }
        });

        return v;
    }
}