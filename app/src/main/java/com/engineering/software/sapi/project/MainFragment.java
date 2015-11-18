package com.engineering.software.sapi.project;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        Button addNewButton = (Button) v.findViewById(R.id.addNewButton);
        addNewButton.setOnClickListener(this);
        View addRouteFragment = inflater.inflate(R.layout.fragment_add_route, container, false);
        View searchRoadFragment = inflater.inflate(R.layout.fragment_search_route, container, false);
        return v;

    }

    public MainFragment() {
        // Required empty public constructor

    }



        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.addNewButton:
                    AddRouteFragment f = new AddRouteFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content, f).commit();
                    break;
                case R.id.searchButton:
                    // TODO Auto-generated method stub
                    SearchRouteFragment s = new SearchRouteFragment();
                    FragmentManager fm = getFragmentManager();
                    fm.beginTransaction().replace(R.id.content, s).commit();
                    break;
                case View.NO_ID:
                default:
                    // TODO Auto-generated method stub
                    break;
            }
        }
}






