package com.engineering.software.sapi.project;


import android.app.DatePickerDialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


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
    private EditText editTextPrice;
    private TextView textViewDate;

    private String starting;
    private String destination;
    private String date;
    private String price;

    private SimpleDateFormat dateFormat;
    private DatePickerDialog datePickerDialog;

    private boolean isEditEnabled = false;

    ArrayList<LatLng> coordinatesStarting;
    ArrayList<LatLng> coordinatesDestination;
    LatLng latLngStarting;
    LatLng latLngDestination;
    MarkerOptions markerOptionStarting;
    MarkerOptions markerOptionDestination;
    Marker markerStart;
    Marker markerDestination;

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

        /*
         * Setup date picker dialog
         */
        setupDatePickerDialog();

        // On click listener of the floating action button
        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditEnabled) {
                    // edit disabled
                    isEditEnabled = false;

                    // Set focus of all edit text and text view to false
                    makeAllEditTextNotEditable();
                    makeTextViewNotFocusable(textViewDate);
                    textViewDate.setOnClickListener(null);

                    // Get the newly entered text
                    //getTextFromEditTextAndTextView();
                    String newStart = editTextFrom.getText().toString();
                    String newDestination = editTextDestination.getText().toString();

                    /*
                     * Refresh map with new coordinates
                     */
                    if (!newStart.equals(starting)) {
                        // Get starting location coordinates
                        getCoordinatesStarting(newStart);
                        // Convert starting location coordinates to latitude and longitude
                        getLatLngStarting();
                        // Add marker to starting location
                        addMarkersToStart(latLngStarting);
                        // Move camera to starting location
                        moveToCoordinates(latLngStarting);
                    }
                    if (!newDestination.equals(destination)) {

                        // Get destination coordinates
                        getCoordinatesDestination(newDestination);
                        // Convert destination coordinates to latitude and longitude
                        getLatLngDestination();
                        // Add markers to destination
                        addMarkersToDestination(latLngDestination);
                    }

                    // Set map and recycler view visible
                    mapView.setVisibility(View.VISIBLE);
                    textViewPassengers.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    // edit enabled
                    isEditEnabled = true;

                    // Enable focuse of all edit text and text view
                    makeAllEditTextEditable();
                    makeTextViewFocusable(textViewDate);

                    // set date on click
                    textViewDate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            datePickerDialog.show();
                        }
                    });

                    // set map and recycler view visibility gone
                    mapView.setVisibility(View.GONE);
                    textViewPassengers.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                }
            }
        });

        return view;
    }

    private void makeTextViewNotFocusable(TextView textView) {
        textView.setFocusable(false);
    }

    private void makeTextViewFocusable(TextView textView) {
        textView.setFocusable(true);
    }


    private void setupDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar date = Calendar.getInstance();
                date.set(year, monthOfYear, dayOfMonth);
                textViewDate.setText(dateFormat.format(date.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    /*
     * Make an edit text not editable
     */
    private void makeEditTextNotEditable(EditText editText) {
        editText.setFocusable(false);
    }

    /*
     * Make a non editable edit text editable
     */
    private void makeEditTextEditable(EditText editText) {
        editText.setFocusableInTouchMode(true);
    }

    private void makeAllEditTextNotEditable() {
        makeEditTextNotEditable(editTextFrom);
        makeEditTextNotEditable(editTextDestination);
        makeEditTextNotEditable(editTextPrice);
    }

    private void makeAllEditTextEditable() {
        makeEditTextEditable(editTextFrom);
        makeEditTextEditable(editTextDestination);
        makeEditTextEditable(editTextPrice);
    }

    /*
     * Add markers to starting location and to destination
     */
    private void addMarkersToStart(LatLng start) {
        if (markerStart != null) {
            markerStart.remove();
        }
        markerOptionStarting = new MarkerOptions().position(start).title(starting);
        markerStart = map.addMarker(markerOptionStarting);
    }

    private void addMarkersToDestination(LatLng dest) {
        if (markerDestination != null) {
            markerDestination.remove();
        }
        markerOptionDestination = new MarkerOptions().position(dest).title(destination);
        markerDestination = map.addMarker(markerOptionDestination);
    }

    /*
     * Get coordinates of starting location and destination
     */
    private void getCoordinatesStarting(String s) {
        coordinatesStarting = getCoordinatesFromLocation(s);
    }

    private void getCoordinatesDestination(String d) {
        coordinatesDestination = getCoordinatesFromLocation(d);
    }

    /*
     * Get latitude and longitude of coordinates
     */
    private void getLatLngStarting() {
        if (coordinatesStarting != null) {
            latLngStarting = new LatLng(
                    coordinatesStarting.get(0).latitude,
                    coordinatesStarting.get(0).longitude);
        }
    }

    private void getLatLngDestination() {
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
                // Get starting location coordinates
                getCoordinatesStarting(starting);
                // Convert starting location coordinates to latitude and longitude
                getLatLngStarting();

                // Get destination coordinates
                getCoordinatesDestination(destination);
                // Convert destination coordinates to latitude and longitude
                getLatLngDestination();

                // Add markers to starting location and destination
                addMarkersToStart(latLngStarting);
                addMarkersToDestination(latLngDestination);

                // Move camera to startin location
                moveToCoordinates(latLngStarting);

                /*PolylineOptions polylineOptions = new PolylineOptions();
                *//*polylineOptions.geodesic(true);*//*
                polylineOptions.add(latLngStarting, latLngDestination);
                map.addPolyline(polylineOptions);*/
            }
        }
    }

    private void moveToCoordinates(LatLng latLngStarting) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngStarting, (float) 13.0));
    }

    /*
     * Sets up the recycle view
     */
    private void setupRecycleView() {
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        /*addPassengers();*/


        getRoutePassengers();


    }

    /*
     * Initialize text views
     */
    private void initialize(View view) {
        // set date format
        dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycle_view_passenger);
        mapView = (MapView) view.findViewById(R.id.map);

        fabEdit = (FloatingActionButton) view.findViewById(R.id.fabEdit);
        textViewPassengers = (TextView) view.findViewById(R.id.text_view_passengers);

        editTextFrom = (EditText) view.findViewById(R.id.edit_text_from);
        editTextDestination = (EditText) view.findViewById(R.id.edit_text_destination);
        editTextPrice = (EditText) view.findViewById(R.id.edit_text_price);
        textViewDate = (TextView) view.findViewById(R.id.text_view_date);

        getTextFromEditTextAndTextView();
        makeAllEditTextNotEditable();
    }

    private void getTextFromEditTextAndTextView() {
        starting = editTextFrom.getText().toString();
        destination = editTextDestination.getText().toString();
        price = editTextPrice.getText().toString();
        date = textViewDate.getText().toString();
    }

    private ArrayList<LatLng> getCoordinatesFromLocation(String location) {
        if (Geocoder.isPresent()) {
            try {
                Geocoder gc = new Geocoder(getActivity().getApplicationContext());

                // get the found Address Objects
                List<Address> addresses = gc.getFromLocationName(location, 5);

                // A list to save the coordinates if they are available
                ArrayList<LatLng> latitudeLongitude = new ArrayList<>(addresses.size());
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

    /*
     * Get passengers of a route
     */
    private void getRoutePassengers() {
        passengers = new ArrayList<>();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Routes");
        /*query.whereEqualTo("Yht8tYYaHl", "objectID");*/
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject obj : objects) {
                        if (obj.getObjectId().equals("Yht8tYYaHl")) {
                            List<String> list = obj.getList("passengers");
                            for (String s : list) {
                                Log.d("PASSENGERS", s);
                                passengers.add(s);
                            }
                        }
                    }
                    RecycleViewAdapter adapter = new RecycleViewAdapter(getContext(), passengers, EditRouteFragment.this);
                    recyclerView.setAdapter(adapter);

                    Log.d("PASSENGERS", passengers.size() + "");
                } else {
                    e.printStackTrace();
                }
            }
        });
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
