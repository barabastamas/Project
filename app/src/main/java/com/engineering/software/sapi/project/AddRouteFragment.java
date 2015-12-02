package com.engineering.software.sapi.project;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tagmanager.Container;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.Parse;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddRouteFragment extends Fragment {
    private Bundle bundle;

    AutoCompleteTextView myAutoComplete;

    // Get a reference to the AutoCompleteTextView in the layout
    AutoCompleteTextView tvto, tvfrom;
    // Get the string array
    String[] countries;
    // Create the adapter and set it to the AutoCompleteTextView
    ArrayAdapter<String> adapter;
    EditText from;
    EditText to;
    TextView date;
    EditText price;
    EditText maxp;
    String fromtxt;
    String totxt;
    String datetxt;
    Integer pricetxt;
    Integer maxptxt;
    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormat;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = savedInstanceState;


    }

    public void setAdapter(ArrayAdapter<String> adapter) {
        this.adapter = adapter;
    }

    public AddRouteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_route, container, false);
        tvfrom = (AutoCompleteTextView) v.findViewById(R.id.from);
        countries = getResources().getStringArray(R.array.city_array);
        adapter = new ArrayAdapter<String>(v.getContext(), android.R.layout.simple_list_item_1, countries);
        tvfrom.setAdapter(adapter);
        tvto = (AutoCompleteTextView) v.findViewById(R.id.to);
        countries = getResources().getStringArray(R.array.city_array);
        adapter = new ArrayAdapter<String>(v.getContext(), android.R.layout.simple_list_item_1, countries);
        tvto.setAdapter(adapter);
        //to = (EditText) v.findViewById(R.id.to);
        //from = (EditText) v.findViewById(R.id.from);
        date = (TextView) v.findViewById(R.id.date);
        price = (EditText) v.findViewById(R.id.price);
        maxp = (EditText) v.findViewById(R.id.max);

        dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        setupDatePickerDialog();
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        Button button = (Button) v.findViewById(R.id.add_route);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Toast.makeText(v.getContext(), "Message", Toast.LENGTH_SHORT).show();
                Date datevegso = null;
                Log.d("Log", "Button was pressed");
                fromtxt = tvfrom.getText().toString();
                totxt = tvto.getText().toString();
                datetxt = date.getText().toString();
                pricetxt = Integer.parseInt(price.getText().toString());
                maxptxt = Integer.parseInt(maxp.getText().toString());
                String dtStart = datetxt;

                /*SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                try {
                    datevegso = format.parse(dtStart);
                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }*/

                if (fromtxt.equals("") && totxt.equals("") && datetxt.equals("") && pricetxt.equals("") && maxptxt.equals("")){
                    Toast.makeText(v.getContext(), "Please complete the form", Toast.LENGTH_LONG).show();
                }else if (fromtxt.equals(totxt)) {
                    Toast.makeText(v.getContext(), "Sie sind schon da...", Toast.LENGTH_LONG).show();
                }else if(ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
                        Toast.makeText(v.getContext(), "Please sign up or login", Toast.LENGTH_LONG).show();
                }else {
                    ParseUser currentUser = ParseUser.getCurrentUser();
                    if (currentUser != null) {
                        ParseObject object = ParseObject.create("Routes");
                        object.put("from", fromtxt);
                        object.put("destination", totxt);
                        object.put("date", datetxt);
                        object.put("price", pricetxt);
                        object.put("numberOfPassanger", maxptxt);
                        String owner = ParseUser.getCurrentUser().getObjectId();

                        object.put("routeOwner", owner);
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    //getActivity().finish();
                                    //Toast.makeText(getView().getContext(), "Sie sind schon daaaaa...", Toast.LENGTH_LONG).show();
                                    FragmentManager fragmentManager;
                                    Fragment fragment;
                                    FragmentTransaction ft;
                                    fragmentManager = getFragmentManager();
                                    fragment = new MainFragment();
                                    ft = fragmentManager.beginTransaction();
                                    ft.replace(R.id.content, fragment);
                                    ft.addToBackStack("MainFragment");
                                    ft.commit();
                                } else {
                                    Toast.makeText(getView().getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }else{
                        Toast.makeText(getView().getContext(),"No user",Toast.LENGTH_LONG).show();
                    }
                }
            }
                    });
        return v;
    }

    private void setupDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar date1 = Calendar.getInstance();
                date1.set(year, monthOfYear, dayOfMonth);
                date.setText(dateFormat.format(date1.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }



    ParseObject po = new ParseObject("Route");
}
