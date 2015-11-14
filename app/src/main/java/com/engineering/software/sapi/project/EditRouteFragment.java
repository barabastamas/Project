package com.engineering.software.sapi.project;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditRouteFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private List<String> passengers;

    private void addPassangers() {
        passengers = new ArrayList<>();
        passengers.add("Biro Zsolt");
        passengers.add("Gabor Ata");
        passengers.add("Nagy Norbi");
        passengers.add("Zold Attila");
        passengers.add("Barabas Tamas");
    }

    private RecyclerView recyclerView;
    private SwitchCompat showPassengers;

    public EditRouteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_edit_route, container, false);

        /*
         * Sets up the switch compat
         * If it is checked the recylce view, that holds the passengers, is shown, otherwise the
         * recycle view's visibility is set to GONE (default value).
         */
        showPassengers = (SwitchCompat) view.findViewById(R.id.switch_show_passengers);
        showPassengers.setOnCheckedChangeListener(this);
        showPassengers.setChecked(false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycle_view_passenger);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        addPassangers();

        RecycleViewAdapter adapter = new RecycleViewAdapter(passengers);
        recyclerView.setAdapter(adapter);

        // Inflate the layout for this fragment
        return view;
    }

    private void setupRecycleView(RecyclerView recyclerView){

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.switch_show_passengers) {
            if (isChecked) {
                recyclerView.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.GONE);
            }
        }
    }
}
