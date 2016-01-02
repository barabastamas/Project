package com.engineering.software.sapi.project;


import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

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

    private List<Pair<ParseUser, Bitmap>> passengers;

    private CoordinatorLayout coordinatorLayout;
    private RecyclerView recyclerView;
    private MapView mapView;
    private GoogleMap map;

    // Saved Instance State
    private Bundle bundle;

    private FloatingActionButton fabEdit;

    private EditText editTextFrom;
    private EditText editTextDestination;
    private EditText editTextPrice;
    private TextView textViewDate;

    private String routeObjectId;
    private String starting;
    private String destination;
    private Bitmap img;

    private SimpleDateFormat dateFormat;
    private DatePickerDialog datePickerDialog;

    private ParseObject route;
    private ParseUser user;

    private boolean isEditEnabled = false;

    private ArrayList<LatLng> coordinatesStarting;
    private ArrayList<LatLng> coordinatesDestination;
    private LatLng latLngStarting;
    private LatLng latLngDestination;
    private MarkerOptions markerOptionStarting;
    private MarkerOptions markerOptionDestination;
    private Marker markerStart;
    private Marker markerDestination;

    private Bundle arg;

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
        getActivity().setTitle("Edit route");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_route, container, false);
        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.coordinatorLayout);

        /*
         * Get data from caller fragment ( OwnRoutes )
         */
        arg = getArguments();

        if (arg != null) {
            routeObjectId = getArguments().getString("Object_ID");
            Log.d("ARGUMENTS", "Arguments not null " + routeObjectId);
        } else {
            Log.d("ARGUMENTS", "Arguments null");
        }

        /*
         * Initialization
         */
        initialize(view);

        /*
         * Setup the recycle view
         */
        try {
            setupRecycleView();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            getRoute();
        } catch (ParseException e) {
            e.printStackTrace();
        }

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
                    //getData();
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
                        moveToCoordinates(latLngStarting, latLngDestination);
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
                    recyclerView.setVisibility(View.GONE);
                }
            }
        });

        return view;
    }

    private void getRoute() throws ParseException {
        ParseQuery<ParseObject> routes = ParseQuery.getQuery("Routes");
        try {
            route = routes.get(routeObjectId);
            editTextFrom.setText(route.get("from").toString());
            editTextDestination.setText(route.get("destination").toString());
            textViewDate.setText(route.get("date").toString());
            editTextPrice.setText(route.get("price").toString());

            getData();

        } catch (Exception e) {
            e.printStackTrace();
        }
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

                // Move camera to starting location
                moveToCoordinates(latLngStarting, latLngDestination);

                /*PolylineOptions polylineOptions = new PolylineOptions();
                *//*polylineOptions.geodesic(true);*//*
                polylineOptions.add(latLngStarting, latLngDestination);
                map.addPolyline(polylineOptions);*/
            }
        }
    }


    /*
     * Move map to the {latLngStarting} coordinates
     */
    private void moveToCoordinates(final LatLng start, final LatLng dest) {
        map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(start);
                builder.include(dest);

                LatLngBounds bounds = builder.build();

                // offset from edges of the map in pixels
                int padding = 100;


                map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
            }
        });
    }


    /*
     * Sets up the recycle view
     */
    private void setupRecycleView() throws ParseException {
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        /*addPassengers();*/

        getRoutePassengers();
    }

    /*
     * Initialization
     */
    private void initialize(View view) {
        // set date format
        dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycle_view_passenger);
        mapView = (MapView) view.findViewById(R.id.map);

        fabEdit = (FloatingActionButton) view.findViewById(R.id.fabEdit);

        editTextFrom = (EditText) view.findViewById(R.id.edit_text_from);
        editTextDestination = (EditText) view.findViewById(R.id.edit_text_destination);
        editTextPrice = (EditText) view.findViewById(R.id.edit_text_price);
        textViewDate = (TextView) view.findViewById(R.id.text_view_date);


        makeAllEditTextNotEditable();
    }

    private void getData() {
        starting = editTextFrom.getText().toString();
        destination = editTextDestination.getText().toString();
    }

    /*
     * Get location position on map (coordinates)
     */
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
     * Get user
     */
    private ParseUser getUser(String string) {
        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        try {
            Log.d("USER", "User found");
            return userQuery.get(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * Get passengers of a route
     */
    private void getRoutePassengers() {
        passengers = new ArrayList<>();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Routes");

        try {
            route = query.get(routeObjectId);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (route != null) {
            editTextFrom.setText(route.get("from").toString());
            editTextDestination.setText(route.get("destination").toString());
            editTextPrice.setText(route.get("price").toString());
            textViewDate.setText(route.get("date").toString());

            List<String> list = route.getList("passengers");
            if (list != null) {
                for (String s : list) {
                    Log.d("PASSENGERS", s);

                    // get route passenger
                    user = getUser(s);

                    // get profile image of the passenger
                    ParseFile profileImage = null;
                    if (user != null) {
                        profileImage = (ParseFile) user.get("profilePic");
                    }
                    if (profileImage != null) {
                        Log.d("IMAGE", "Image");

                        try {
                            byte[] data = profileImage.getData();
                            img = BitmapFactory.decodeByteArray(
                                    data,
                                    0,
                                    data.length
                            );
                            if (img != null) {
                                Log.d("IMAGE", "Image from parse decoded!");
                            }
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    } else {
                        Log.d("IMAGE", "No image");
                        img = BitmapFactory.decodeResource(getResources(), R.drawable.ic_profile_black);
                    }

                    passengers.add(Pair.create(user, img));
                }
            } else {
            }

            if (passengers != null) {
                PassengersAdapter adapter = new PassengersAdapter(getContext(), passengers, EditRouteFragment.this);
                recyclerView.setAdapter(adapter);
            }
        }
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
