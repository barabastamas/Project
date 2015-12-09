package com.engineering.software.sapi.project;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailRouteFragment extends Fragment {

    // Saved Instance State
    private Bundle bundle;

    private ParseUser currentUser;

    private MapView mapView;
    private GoogleMap map;

    private RecyclerView recyclerView;
    private FloatingActionButton fabSubscribe;

    private TextView textViewName;
    private TextView textViewStart;
    private TextView textViewDestination;
    private TextView textViewDate;
    private TextView textViewPrice;
    private TextView textViewPassengersList;
    private ImageView imageViewProfileImage;

    private Button buttonCall;
    private Button buttonSendEmail;

    private ParseUser routeOwner;
    private ParseObject route;
    private Bitmap img;

    private List<Pair<ParseUser, Bitmap>> passengers;

    private String ownerName;
    private String routeObjectId;
    private String starting;
    private String destination;
    private String date;
    private String price;

    private ArrayList<LatLng> coordinatesStarting;
    private ArrayList<LatLng> coordinatesDestination;
    private LatLng latLngStarting;
    private LatLng latLngDestination;
    private MarkerOptions markerOptionStarting;
    private MarkerOptions markerOptionDestination;
    private Marker markerStart;
    private Marker markerDestination;

    private Bundle arg;
    private ParseUser user;


    public DetailRouteFragment() {
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
        View view = inflater.inflate(R.layout.fragment_detail_route, container, false);

        /*
         * Get data from caller fragment ( SearchRoutes )
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
         * Get route and the user who published it
         */
        getRoute();
//        getData();

        Log.d("LOC", starting + " " + destination);

        /*
         * Populate recycler view with passengers
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

        /*fabSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                route.put("passengers", currentUser.getObjectId());
                route.saveEventually();
            }
        });*/


        return view;
    }

    /*
     * Sets up the recycle view
     */
    private void setupRecycleView() {
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        getRoutePassengers();
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
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject obj : objects) {
                        if (obj.getObjectId().equals(routeObjectId)) {
                            List<String> list = obj.getList("passengers");
                            if (list != null) {
                                textViewPassengersList.setText(R.string.passengers);
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

                                        /*profileImage.getDataInBackground(new GetDataCallback() {
                                            @Override
                                            public void done(byte[] data, ParseException e) {
                                                if (e == null) {
                                                    img = BitmapFactory.decodeByteArray(
                                                            data,
                                                            0,
                                                            data.length
                                                    );
                                                    if (img != null) {
                                                        Log.d("IMAGE", "Image from parse decoded!");
                                                    }
                                                } else {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });*/
                                    } else {
                                        Log.d("IMAGE", "No image. Decoding default image");
                                        img = BitmapFactory.decodeResource(
                                                getResources(),
                                                R.drawable.ic_profile_black);
                                    }

                                    passengers.add(Pair.create(user, img));
                                }
                            } else {
                                textViewPassengersList.setText(R.string.no_passengers);
                            }
                        }
                    }

                    Log.d("PASSENGERS", passengers.size() + "");
                } else {
                    e.printStackTrace();
                }
                if (passengers != null) {
                    PassengersAdapter adapter = new PassengersAdapter(
                            getContext(),
                            passengers,
                            DetailRouteFragment.this
                    );
                    recyclerView.setAdapter(adapter);
                }
            }
        });
    }

    private void getRoute() {
        ParseQuery<ParseObject> routes = ParseQuery.getQuery("Routes");
        try {
            route = routes.get(routeObjectId);
            Log.d("OWNER", route.get("routeOwner").toString());

            ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
            try {
                routeOwner = userQuery.get(route.get("routeOwner").toString());
            } catch (ParseException e1) {
                e1.printStackTrace();
            }

            Log.d("OWNER", routeOwner.get("name").toString());

            textViewName.setText(routeOwner.get("name").toString());
            textViewStart.setText(route.get("from").toString());
            textViewDestination.setText(route.get("destination").toString());
            textViewDate.setText(route.get("date").toString());
            textViewPrice.setText(route.get("price").toString());

            getData();

            ParseFile profileImage = (ParseFile) currentUser.get("profilePic");
            if (profileImage != null) {
                Log.d("IMAGE", "Image");
                profileImage.getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] data, ParseException e) {
                        if (e == null) {
                            Bitmap img = BitmapFactory.decodeByteArray(data, 0, data.length);
                            if (img != null) {
                                imageViewProfileImage.setImageBitmap(img);
                            }
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                Log.d("IMAGE", "No image. Loading default profile image");
                imageViewProfileImage.setImageBitmap(BitmapFactory.decodeResource(
                                getResources(),
                                R.drawable.profile_image)
                );
            }

            buttonCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentCall = new Intent(Intent.ACTION_CALL);
                    intentCall.setData(Uri.parse("tel:" + routeOwner.get("phoneNumber")));
                    startActivity(intentCall);
                }
            });

            buttonSendEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentSendEmail = new Intent(Intent.ACTION_SEND);
                    intentSendEmail.setType("message/rfc822");
                    intentSendEmail.putExtra(Intent.EXTRA_EMAIL, new String[]{"tamas_27@yahoo.co.uk"});
                    try {
                        startActivity(Intent.createChooser(intentSendEmail, "Send email with..."));
                    } catch (android.content.ActivityNotFoundException e) {
                        Toast.makeText(
                                getContext(),
                                "There are no email clients installed.",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            });
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void initialize(View view) {
        currentUser = ParseUser.getCurrentUser();

        recyclerView = (RecyclerView) view.findViewById(R.id.recycle_view_passenger);
        fabSubscribe = (FloatingActionButton) view.findViewById(R.id.fab_subscribe);

        mapView = (MapView) view.findViewById(R.id.map);

        textViewName = (TextView) view.findViewById(R.id.text_view_name);
        textViewStart = (TextView) view.findViewById(R.id.text_view_from);
        textViewDestination = (TextView) view.findViewById(R.id.text_view_destination);
        textViewDate = (TextView) view.findViewById(R.id.text_view_date);
        textViewPrice = (TextView) view.findViewById(R.id.text_view_price);
        imageViewProfileImage = (ImageView) view.findViewById(R.id.detail_route_profile_image);
        textViewPassengersList = (TextView) view.findViewById(R.id.text_view_passengers_list);

        buttonCall = (Button) view.findViewById(R.id.button_call);
        buttonSendEmail = (Button) view.findViewById(R.id.button_send_email);
    }

    private void getData() {
        starting = textViewStart.getText().toString();
        destination = textViewDestination.getText().toString();
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
                moveToCoordinates(latLngStarting, latLngDestination);
            }
        }
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
                        latitudeLongitude.add(
                                new LatLng(address.getLatitude(), address.getLongitude()));
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
