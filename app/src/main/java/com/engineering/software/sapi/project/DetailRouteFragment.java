package com.engineering.software.sapi.project;


import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.engineering.software.sapi.project.Profile.ProfileFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailRouteFragment extends Fragment {

    // Saved Instance State
    private Bundle bundle;

    private ParseUser currentUser;
    private ParseUser user;

    private MapView mapView;
    private GoogleMap map;

    private CoordinatorLayout coordinatorLayout;
    private PassengersAdapter adapter;

    private RecyclerView recyclerView;
    private FloatingActionButton fabSubscribe;

    private TextView textViewName;
    private TextView textViewStart;
    private TextView textViewDestination;
    private TextView textViewDate;
    private TextView textViewPrice;
    private ImageView imageViewProfileImage;
    private LinearLayout linearLayoutOwner;

    private ImageButton buttonCall;
    private ImageButton buttonSendEmail;

    private ParseUser routeOwner;
    private ParseObject route;
    private Bitmap img;

    private List<Pair<ParseUser, Bitmap>> passengers;

    private String routeObjectId;
    private String starting;
    private String destination;

    private ArrayList<LatLng> coordinatesStarting;
    private ArrayList<LatLng> coordinatesDestination;
    private LatLng latLngStarting;
    private LatLng latLngDestination;
    private Marker markerStart;
    private Marker markerDestination;

    boolean mSubscribed;
    boolean mIsFreeSeat;
    boolean mIsRouteOwner;


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
        getActivity().setTitle("Route detail");

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_detail_route, container, false);

        /*
         * Get data from caller fragment ( SearchRoutes )
         */
        Bundle arg = getArguments();

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

        /*
         * Set up call and send email buttons
         */
        setupButtons();

        return view;
    }

    private void setupButtons() {
        /*
         * Set up call button
         */
        buttonCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCall = new Intent(Intent.ACTION_CALL);
                intentCall.setData(Uri.parse("tel:" + routeOwner.get("phoneNumber")));
                startActivity(intentCall);
            }
        });

        /*
         * Set up send email button
         */
        buttonSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
//                emailIntent.setType("message/rfc822");

//                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"router@yahoo.com"});
//                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Router. Subscription");

                emailIntent.setData(
                        Uri.parse("mailto:" + Uri.encode("router@yahoo.com") +
                                "?subject=" + Uri.encode("Route Subscription"))
                );

                try {
                    startActivity(Intent.createChooser(emailIntent, "Send mail with..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /*
         * Set up subscribe button
         */
        fabSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SUBSCRIBE", "fab clicked");
                /*
                 * Get route owner
                 * Verify if the user is already subscribed
                 * Verify if there is room to subscribe
                 * Set up the subscribe button (FAB)
                 */
                setupSubscription();

                if (mIsRouteOwner) {
                    Snackbar.make(
                            coordinatorLayout,
                            "Can't subscribe to own route",
                            Snackbar.LENGTH_LONG)
                            .show();
                } else {
                    if (mIsFreeSeat) {
                        if (!mSubscribed) {
                            addSubscriber();
                            disableSubscription(fabSubscribe);
                            mSubscribed = true;
                        } else {
                            Snackbar.make(
                                    coordinatorLayout,
                                    "You're already subscribed",
                                    Snackbar.LENGTH_LONG)
                                    .setAction("UNSUBSCRIBE", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            unsubscribe();
                                            mSubscribed = false;
                                        }
                                    })
                                    .show();
                        }
                    } else {
                        Log.d("SUBSCRIBE", "Maximum numbers of passengers reached");
                        Snackbar.make(coordinatorLayout, "Maximum numbers of passengers reached", Snackbar.LENGTH_LONG).show();
                    }

                }
            }
        });

        /*
         * On click on the owner
         */
        linearLayoutOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle arg = new Bundle();
                arg.putString("userObjectID", routeOwner.getObjectId());

                Fragment fragment = new ProfileFragment();
                fragment.setArguments(arg);

                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

                fragmentTransaction.hide(DetailRouteFragment.this);
                fragmentTransaction.add(R.id.content, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    private void setupSubscription() {
        /*
         * Verify route owner
         */
        mIsRouteOwner = isOwner(currentUser);
        if (mIsRouteOwner) {
            Log.d("SUBSCRIBE", "Can't subscribe to own route");
            disableSubscription(fabSubscribe);
        }

        /*
         * Verify if the user is already subscribed
         */
        mSubscribed = verifySubscription(currentUser);
        if (mSubscribed) {
            Log.d("SUBSCRIBE", "Already subscribed");
            disableSubscription(fabSubscribe);
        }

        /*
         * Verify if there is room to subscribe
         */
        mIsFreeSeat = isFreeSeat();
        if (!mIsFreeSeat) {
            Log.d("SUBSCRIBE", "Maximum numbers of passengers reached");
            disableSubscription(fabSubscribe);
        }
    }

    /*
     * Verify if the current user is the owner of the route or not
     */
    private boolean isOwner(ParseUser user) {
        return route.get("routeOwner").equals(user.getObjectId());
    }

    /*
     * Verify if there is free seat for the user to subscribe
     */
    private boolean isFreeSeat() {
        Integer numberOfPassenger = route.getInt("numberOfPassanger");

        Log.d("SUBSCRIBE", passengers.size() + " " + numberOfPassenger);

        return passengers.size() < numberOfPassenger;
    }

    /*
     * Verify if the user is already subscribed
     */

    private boolean verifySubscription(ParseUser currentUser) {
        /*// subscribedRoutes contains objectIDs of subscribed routes
        List<String> subscribedRoutes = currentUser.getList("subscribed");*/

        // subscribedPassengers contains objectIDs of passengers who have subscribed
        List<String> subscribedPassengers = route.getList("passengers");

        mSubscribed = false;

        if (subscribedPassengers != null) {
            for (String subscriber : subscribedPassengers) {
                if (subscriber.equals(currentUser.getObjectId())) {
                    mSubscribed = true;
                    break;
                }
            }
        }
        return mSubscribed;
    }

    /*
     * Disable subscription
     * Set color of the floating action button to red
     */
    private void disableSubscription(FloatingActionButton fabSubscribe) {
        fabSubscribe.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.red)));
    }

    /*
     * Enable subscription
     * Set color of the floating action button to default value (accent)
     */
    private void enableSubscription(FloatingActionButton fabSubscribe) {
        fabSubscribe.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.accent)));
    }

    /*
     * Unsubscribe from route
     */
    private void unsubscribe() {
        img = getProfileImage(currentUser);

        for (Pair<ParseUser, Bitmap> user : passengers) {
            if (user.first == currentUser) {
                passengers.remove(user);
            }
        }
        adapter.notifyDataSetChanged();

        route.removeAll("passengers", Collections.singleton(currentUser.getObjectId()));
        route.saveInBackground();

        currentUser.removeAll("subscribed", Collections.singleton(routeObjectId));
        currentUser.saveInBackground();

        enableSubscription(fabSubscribe);
    }

    /*
     * Add subscriber
     * User object id to routes
     * Route object id to user
     */
    private void addSubscriber() {
        if (routeOwner.getObjectId().equals(currentUser.getObjectId())) {
            Snackbar.make(
                    coordinatorLayout,
                    "Can't subscribe to own route",
                    Snackbar.LENGTH_LONG)
                    .show();
        } else {
            Log.d(
                    "SUBSCRIBE",
                    currentUser.get("name") + " subscribed for this route"
            );
            img = getProfileImage(currentUser);

            passengers.add(Pair.create(currentUser, img));
            adapter.notifyDataSetChanged();

            route.add("passengers", currentUser.getObjectId());
            route.saveInBackground();
            currentUser.add("subscribed", routeObjectId);
            currentUser.saveInBackground();
        }
    }


    /*
     * Sets up the recycle view
     */
    private void setupRecycleView() {
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setHasFixedSize(true);

        /*LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);*/


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

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Routes");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject obj : objects) {
                        if (obj.getObjectId().equals(routeObjectId)) {
                            List<String> list = obj.getList("passengers");
                            if (list != null) {
                                for (String s : list) {
                                    Log.d("PASSENGERS", s);

                                    // get route passenger
                                    user = getUser(s);

                                    // get profile image of the passenger
                                    img = getProfileImage(user);

                                    passengers.add(Pair.create(user, img));
                                }
                            }
                        }
                    }

                    Log.d("PASSENGERS", passengers.size() + "");
                } else {
                    e.printStackTrace();
                }
                if (passengers != null) {

                    GridLayoutManager gridLayoutManager = new GridLayoutManager(
                            getContext(),
                            1,
                            GridLayoutManager.HORIZONTAL,
                            false);
                    gridLayoutManager.canScrollVertically();

                    recyclerView.setLayoutManager(gridLayoutManager);

                    adapter = new PassengersAdapter(
                            getContext(),
                            passengers,
                            DetailRouteFragment.this
                    );
                    recyclerView.setAdapter(adapter);
                }
            }
        });
    }

    private Bitmap getProfileImage(ParseUser parseUser) {
        ParseFile profileImage = null;
        if (parseUser != null) {
            profileImage = (ParseFile) parseUser.get("profilePic");
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
            Log.d("IMAGE", "No image. Decoding default image");
            img = BitmapFactory.decodeResource(
                    getResources(),
                    R.drawable.ic_profile_black);
        }
        return img;
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

            img = getProfileImage(routeOwner);
            imageViewProfileImage.setImageBitmap(img);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void initialize(View view) {
        currentUser = ParseUser.getCurrentUser();

        passengers = new ArrayList<>();

        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.coordinatorLayout);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycle_view_passenger);
        fabSubscribe = (FloatingActionButton) view.findViewById(R.id.fab_subscribe);

        mapView = (MapView) view.findViewById(R.id.map);

        textViewName = (TextView) view.findViewById(R.id.text_view_name);
        textViewStart = (TextView) view.findViewById(R.id.text_view_from);
        textViewDestination = (TextView) view.findViewById(R.id.text_view_destination);
        textViewDate = (TextView) view.findViewById(R.id.text_view_date);
        textViewPrice = (TextView) view.findViewById(R.id.text_view_price);
        imageViewProfileImage = (ImageView) view.findViewById(R.id.detail_route_profile_image);
        linearLayoutOwner = (LinearLayout) view.findViewById(R.id.owner_layout);

        buttonCall = (ImageButton) view.findViewById(R.id.button_call);
        buttonSendEmail = (ImageButton) view.findViewById(R.id.button_send_email);
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
        MarkerOptions markerOptionStarting = new MarkerOptions().position(start).title(starting);
        markerStart = map.addMarker(markerOptionStarting);
    }

    private void addMarkersToDestination(LatLng dest) {
        if (markerDestination != null) {
            markerDestination.remove();
        }
        MarkerOptions markerOptionDestination = new MarkerOptions().position(dest).title(destination);
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
