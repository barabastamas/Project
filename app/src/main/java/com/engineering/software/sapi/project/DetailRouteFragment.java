package com.engineering.software.sapi.project;


import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.GetCallback;
import com.parse.ParseException;
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

    private TextView textViewName;
    private TextView textViewStart;
    private TextView textViewDestination;
    private TextView textViewDate;
    private TextView textViewPrice;

    private Button buttonCall;
    private Button buttonSendEmail;

    private ParseUser routeOwner;
    private ParseObject route;

    private String ownerName;
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
         * Initialization
         */
        initialize(view);

        /*
         * Initialize Map.
         */
        MapsInitializer.initialize(getActivity().getApplicationContext());

        /*
         * Get route and the user who published it
         */
        getRoute();

        /*
         * Setup map
         */
        setupMap();


        return view;
    }

    private void getRoute() {
        ParseQuery<ParseObject> routes = ParseQuery.getQuery("Routes");
        routes.getInBackground("Yht8tYYaHl", new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {

                    Log.d("OWNER", object.get("routeOwner").toString());

                    ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
                    try {
                        routeOwner = userQuery.get(object.get("routeOwner").toString());
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }

                    Log.d("OWNER", routeOwner.get("name").toString());

                    textViewName.setText(routeOwner.get("name").toString());
                    textViewStart.setText(object.get("from").toString());
                    textViewDestination.setText(object.get("destination").toString());
                    textViewDate.setText(object.get("date").toString());
                    textViewPrice.setText(object.get("price").toString());

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
                                Toast.makeText(getContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initialize(View view) {
        currentUser = ParseUser.getCurrentUser();

        mapView = (MapView) view.findViewById(R.id.map);

        textViewName = (TextView) view.findViewById(R.id.text_view_name);
        textViewStart = (TextView) view.findViewById(R.id.text_view_from);
        textViewDestination = (TextView) view.findViewById(R.id.text_view_destination);
        textViewDate = (TextView) view.findViewById(R.id.text_view_date);
        textViewPrice = (TextView) view.findViewById(R.id.text_view_price);

        buttonCall = (Button) view.findViewById(R.id.button_call);
        buttonSendEmail = (Button) view.findViewById(R.id.button_send_email);

        getData();
    }

    private void getData() {
        starting = textViewStart.getText().toString();
        destination = textViewDestination.getText().toString();
    }

    /*
     * Set up map
     */
    private void setupMap() {
        mapView.onCreate(bundle);
        if (map == null) {
            map = mapView.getMap();
            if (map != null) {
                coordinatesStarting = getCoordinatesFromLocation(starting);
                if (coordinatesStarting != null) {
                    latLngStarting = new LatLng(
                            coordinatesStarting.get(0).latitude,
                            coordinatesStarting.get(0).longitude);
                }

                coordinatesDestination = getCoordinatesFromLocation(destination);
                if (coordinatesDestination != null) {
                    latLngDestination = new LatLng(
                            coordinatesDestination.get(0).latitude,
                            coordinatesDestination.get(0).longitude);
                }

                markerOptionStarting = new MarkerOptions().position(latLngStarting).title(starting);
                markerStart = map.addMarker(markerOptionStarting);

                markerOptionDestination = new MarkerOptions().position(latLngDestination).title(destination);
                markerDestination = map.addMarker(markerOptionDestination);

                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngStarting, (float) 13.0));
            }
        }
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

}
