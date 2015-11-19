package com.engineering.software.sapi.project;


import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tagmanager.Container;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditRouteFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private void addPassengers() {
        passengers = new ArrayList<>();
        passengers.add("Biro Zsolt");
        passengers.add("Gabor Ata");
        passengers.add("Nagy Norbi");
        passengers.add("Zold Attila");
        passengers.add("Barabas Tamas");
    }

    private List<String> passengers;

    private CoordinatorLayout coordinatorLayout;
    private RecyclerView recyclerView;
    private SwitchCompat showPassengers;
    private MapView mapView;
    private GoogleMap map;

    // Saved Instance State
    private Bundle bundle;

    // List to store location coordinates
    private ArrayList<LatLng> coordinatesFrom;
    private ArrayList<LatLng> coordinatesDestination;

    private TextView textViewFrom;
    private TextView textViewDestination;
    private TextView textViewDate;
    private TextView textViewPrice;

    private String from;
    private String destination;
    private String date;
    private String price;

    public EditRouteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = savedInstanceState;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_route, container, false);
        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.coordonatorLayout);

        /*
         * Initialize text views
         */
        initializeTextViews(view);

        /*
         * Setup the recycle view
         */
        setupRecycleView(view);

        /*
         * Setup switch button
         * Show or hide passengers
         */
        setupSwitch(view);

        /*
         * Initialize Map.
         */
        MapsInitializer.initialize(getActivity().getApplicationContext());

        /*
         * Setup map
         */
        setupMap(view);

        FloatingActionButton fabEdit = (FloatingActionButton) view.findViewById(R.id.fabEdit);
        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(coordinatorLayout, "fabEdit", Snackbar.LENGTH_LONG).show();
            }
        });

        return view;
    }

    /*
     * Hide or show navigation drawer
     */
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

    /*
     * Sets up the switch compat
     * If it is checked the recylce view, that holds the passengers, is shown, otherwise the
     * recycle view's visibility is set to GONE (default value).
     */
    private void setupSwitch(View view) {
        showPassengers = (SwitchCompat) view.findViewById(R.id.switch_show_passengers);
        showPassengers.setOnCheckedChangeListener(this);
        showPassengers.setChecked(false);
    }


    /*
     * Sets up the Map
     */
    private void setupMap(View view) {
        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(bundle);
        if (map == null) {
            map = mapView.getMap();
            if (map != null) {
                //map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Hello!"));

                /*
                 * Get coordinates for starting location and destination.
                 */
                coordinatesFrom = getCoordinatesFromLocation(from);
                coordinatesDestination = getCoordinatesFromLocation(destination);

                LatLng latLngFrom = new LatLng(
                        coordinatesFrom.get(0).latitude,
                        coordinatesFrom.get(0).longitude);
                LatLng latLngDestination = new LatLng(
                        coordinatesDestination.get(0).latitude,
                        coordinatesDestination.get(0).longitude);

                map.addMarker(new MarkerOptions().position(latLngFrom).title(from));
                map.addMarker(new MarkerOptions().position(latLngDestination).title(destination));

                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngFrom, (float) 13.0));
            }


        }
    }

    /*
     * Sets up the recycle view
     */
    private void setupRecycleView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recycle_view_passenger);
        recyclerView.setVisibility(View.GONE);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        addPassengers();

        RecycleViewAdapter adapter = new RecycleViewAdapter(passengers);
        recyclerView.setAdapter(adapter);
    }

    /*
     * Initialize text views
     */
    private void initializeTextViews(View view) {
        textViewFrom = (TextView) view.findViewById(R.id.text_view_from);
        textViewDestination = (TextView) view.findViewById(R.id.text_view_destination);
        textViewDate = (TextView) view.findViewById(R.id.text_view_date);
        textViewPrice = (TextView) view.findViewById(R.id.text_view_price);

        from = textViewFrom.getText().toString();
        destination = textViewDestination.getText().toString();
        date = textViewDate.getText().toString();
        price = textViewPrice.getText().toString();
    }

    private ArrayList<LatLng> getCoordinatesFromLocation(String location) {
        if (Geocoder.isPresent()) {
            try {
                Geocoder gc = new Geocoder(getActivity().getApplicationContext());

                // get the found Address Objects
                List<Address> addresses = gc.getFromLocationName(location, 5);

                // A list to save the coordinates if they are available
                ArrayList<LatLng> latitudeLongitude = new ArrayList<LatLng>(addresses.size());
                for (Address address : addresses) {
                    if (address.hasLatitude() && address.hasLongitude()) {
                        latitudeLongitude.add(new LatLng(address.getLatitude(), address.getLongitude()));
                    }
                }
                return latitudeLongitude;
            } catch (IOException e) {
                // handle the exception
                e.printStackTrace();
            }
        }
        return null;
    }
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}
