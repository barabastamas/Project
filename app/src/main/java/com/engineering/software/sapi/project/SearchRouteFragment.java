package com.engineering.software.sapi.project;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.preference.DialogPreference;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchRouteFragment extends Fragment {
    private TextView fromDateEtxt;
    private String[] countries;
    private ArrayAdapter<String> adapter;

    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;

    private SimpleDateFormat dateFormatter;

    private int minYear;
    private int minMonth;
    private int minDay;
    private List<ParseObject> routeList;

    public SearchRouteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View view = inflater.inflate(R.layout.fragment_search_route, container, false);

        final AutoCompleteTextView etFrom = (AutoCompleteTextView) view.findViewById(R.id.fromid);
        countries = getResources().getStringArray(R.array.city_array);
        adapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, countries);
        etFrom.setAdapter(adapter);

        final AutoCompleteTextView etTo = (AutoCompleteTextView) view.findViewById(R.id.toid);
        countries = getResources().getStringArray(R.array.city_array);
        adapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, countries);
        etTo.setAdapter(adapter);

        final Button searchButton = (Button) view.findViewById(R.id.button);
        Button setDateButton = (Button) view.findViewById(R.id.date_button);

        fromDateEtxt = (TextView) view.findViewById(R.id.etxt_fromdate);

        final RecyclerView recList = (RecyclerView) view.findViewById(R.id.recyclerview);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity().getApplicationContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);


        Calendar c = Calendar.getInstance();
        minYear = c.get(Calendar.YEAR);
        minMonth = c.get(Calendar.MONTH);
        minDay = c.get(Calendar.DAY_OF_MONTH);

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        setDateTimeField();
        setDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(searchButton.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);

                fromDatePickerDialog.show();
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(searchButton.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);

                if (etFrom.getText().toString().equals("") || etTo.getText().toString().equals("") || fromDateEtxt.getText().toString().equals("") || fromDateEtxt.getText().toString().equals("Error Date")) {

                    Snackbar.make(getView(),"Error location or date!",Snackbar.LENGTH_LONG).show();

                } else {
                    routeList = new ArrayList<>();
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Routes");

                    query.whereEqualTo("from", etFrom.getText().toString());
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            for (ParseObject obj : objects) {
                                if (obj.get("destination").toString().equals(etTo.getText().toString()) && obj.get("date").toString().equals(fromDateEtxt.getText().toString())) {
                                    Log.d("too", obj.get("destination").toString());
                                    routeList.add(obj);

                                }

                            }
                            if (routeList.size() == 0) {
                                Snackbar.make(getView(),"Empty route list",Snackbar.LENGTH_LONG).show();
                            }

                            RoutesAdapter routesAdapter = new RoutesAdapter(routeList,SearchRouteFragment.this, getContext());
                            recList.setAdapter(routesAdapter);


                        }
                    });


                }
            }
        });


        return view;
    }

    private void setDateTimeField() {
        //fromDateEtxt.setOnClickListener((View.OnClickListener) getActivity());

        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                if (year < minYear || monthOfYear < minMonth || dayOfMonth < minDay) {
                    fromDateEtxt.setText("Error Date");
                } else {
                    fromDateEtxt.setText(dateFormatter.format(newDate.getTime()));
                }
            }


        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

    }


}
