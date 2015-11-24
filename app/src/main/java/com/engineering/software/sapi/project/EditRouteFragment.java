package com.engineering.software.sapi.project;


import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.KeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.engineering.software.sapi.project.Profile.ProfileFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditRouteFragment extends Fragment {


    private List<String> passengers;

    private CoordinatorLayout coordinatorLayout;
    private RecyclerView recyclerView;
    private MapView mapView;
    private GoogleMap map;

    // Saved Instance State
    private Bundle bundle;

    private TextView textViewPassengers;
    private FloatingActionButton fabEdit;

    private EditText editTextFrom;
    private EditText editTextDestination;
    private EditText editTextDate;
    private EditText editTextPrice;

    private String starting;
    private String destination;
    private String date;
    private String price;

    private boolean isEditEnabled = false;

    ArrayList<LatLng> coordinatesStarting;
    ArrayList<LatLng> coordinatesDestination;
    LatLng latLngStarting;
    LatLng latLngDestination;

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
        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.coordinatorLayout);

        /*
         * Initialize text views
         */
        initialize(view);

        /*
         * Setup the recycle view
         */
        setupRecycleView();

        /*
         * Initialize Map.
         */
        MapsInitializer.initialize(getActivity().getApplicationContext());

        /*
         * Setup map
         */
        setupMap();


        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Snackbar.make(coordinatorLayout, "fabEdit", Snackbar.LENGTH_LONG).show();*/
                if (isEditEnabled) {
                    // edit disabled
                    isEditEnabled = false;

                    makeAllEditTextNotEditable();
                    getTextFromAllEditText();

                    map.clear();
                    getCoordinates();
                    getLatLng();
                    addMarkersToMap(latLngStarting, latLngDestination);


                    // set map and recycler view visible
                    mapView.setVisibility(View.VISIBLE);
                    textViewPassengers.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    // edit enabled
                    isEditEnabled = true;

                    makeAllEditTextEditable();

                    // set map and recycler view visibility gone
                    mapView.setVisibility(View.GONE);
                    textViewPassengers.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                }
            }
        });

        return view;
    }

    /*
     * Make an edit text not editable
     */
    private void makeEditTextNotEditable(EditText editText) {
        editText.setFocusable(false);
        /*editText.setFocusableInTouchMode(false);*/
    }

    /*
     * Make a non editable edit text editable
     */
    private void makeEditTextEditable(EditText editText) {
        editText.setFocusableInTouchMode(true);
        /*editText.setFocusableInTouchMode(true);*/
    }

    private void makeAllEditTextNotEditable() {
        makeEditTextNotEditable(editTextFrom);
        makeEditTextNotEditable(editTextDestination);
        makeEditTextNotEditable(editTextDate);
        makeEditTextNotEditable(editTextPrice);
    }

    private void makeAllEditTextEditable() {
        makeEditTextEditable(editTextFrom);
        makeEditTextEditable(editTextDestination);
        makeEditTextEditable(editTextDate);
        makeEditTextEditable(editTextPrice);
    }

    /*
     * Add markers to starting location and to destination
     */
    private void addMarkersToMap(LatLng start, LatLng dest) {
        map.addMarker(new MarkerOptions().position(start).title(starting));
        map.addMarker(new MarkerOptions().position(dest).title(destination));

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(start, (float) 13.0));
    }

    /*
     * Get coordinates of starting location and destination
     */
    private void getCoordinates() {
        coordinatesStarting = getCoordinatesFromLocation(starting);
        coordinatesDestination = getCoordinatesFromLocation(destination);
    }

    /*
     * Get latitude and longitude of coordinates
     */
    private void getLatLng() {
        if (coordinatesStarting != null) {
            latLngStarting = new LatLng(
                    coordinatesStarting.get(0).latitude,
                    coordinatesStarting.get(0).longitude);
        }

        if (coordinatesDestination != null) {
            latLngDestination = new LatLng(
                    coordinatesDestination.get(0).latitude,
                    coordinatesDestination.get(0).longitude);
        }
    }

    /*
     * Sets up the Map
     */
    private void setupMap() {
        mapView.onCreate(bundle);
        if (map == null) {
            map = mapView.getMap();
            if (map != null) {
                getCoordinates();
                getLatLng();
                addMarkersToMap(latLngStarting, latLngDestination);
            }
        }
    }

    /*
     * Sets up the recycle view
     */
    private void setupRecycleView() {
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        addPassengers();

        RecycleViewAdapter adapter = new RecycleViewAdapter(getContext(), passengers, EditRouteFragment.this);
        recyclerView.setAdapter(adapter);
    }

    /*
     * Initialize text views
     */
    private void initialize(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recycle_view_passenger);
        mapView = (MapView) view.findViewById(R.id.map);

        fabEdit = (FloatingActionButton) view.findViewById(R.id.fabEdit);
        textViewPassengers = (TextView) view.findViewById(R.id.text_view_passengers);

        editTextFrom = (EditText) view.findViewById(R.id.edit_text_from);
        editTextDestination = (EditText) view.findViewById(R.id.edit_text_destination);
        editTextDate = (EditText) view.findViewById(R.id.edit_text_date);
        editTextPrice = (EditText) view.findViewById(R.id.edit_text_price);

        getTextFromAllEditText();
        makeAllEditTextNotEditable();
    }

    private void getTextFromAllEditText() {
        starting = editTextFrom.getText().toString();
        destination = editTextDestination.getText().toString();
        date = editTextDate.getText().toString();
        price = editTextPrice.getText().toString();
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

    private void addPassengers() {
        passengers = new ArrayList<>();
        passengers.add("Biro Zsolt");
        passengers.add("Gabor Ata");
        passengers.add("Nagy Norbi");
        passengers.add("Zold Attila");
        passengers.add("Barabas Tamas");
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
