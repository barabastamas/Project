package com.engineering.software.sapi.project;


import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
    private int minYear;
    private int minMonth;
    private int minDay;
    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormat;
    private TextInputLayout inputLayoutTo, inputLayoutFrom, inputLayoutDate, inputLayoutMax, inputLayoutPrice;

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
        getActivity().setTitle("Add route");

        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_add_route, container, false);
        inputLayoutTo = (TextInputLayout) container.findViewById(R.id.input_layout_to);
        inputLayoutFrom = (TextInputLayout) container.findViewById(R.id.input_layout_from);
        inputLayoutDate = (TextInputLayout) container.findViewById(R.id.input_layout_date);
        inputLayoutMax = (TextInputLayout) container.findViewById(R.id.input_layout_max);
        inputLayoutPrice = (TextInputLayout) container.findViewById(R.id.input_layout_price);
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
        tvto.addTextChangedListener(new MyTextWatcher(tvto));
        tvfrom.addTextChangedListener(new MyTextWatcher(tvfrom));
        date.addTextChangedListener(new MyTextWatcher(date));
        price.addTextChangedListener(new MyTextWatcher(price));
        maxp.addTextChangedListener(new MyTextWatcher(maxp));

        Calendar c = Calendar.getInstance();
        minYear = c.get(Calendar.YEAR);
        minMonth = c.get(Calendar.MONTH);
        minDay = c.get(Calendar.DAY_OF_MONTH);
        dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        setupDatePickerDialog();
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        final Button button = (Button) v.findViewById(R.id.add_route);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addValidate();
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(button.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                //Toast.makeText(v.getContext(), "Message", Toast.LENGTH_SHORT).show();
                Date datevegso = null;


                Log.d("Log", "Button was pressed");


                /*SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                try {
                    datevegso = format.parse(dtStart);
                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }*/

                if (tvfrom.getText().toString().equals("") || tvto.getText().toString().equals("") || date.getText().toString().equals("") || price.getText().toString().equals("") || maxp.getText().toString().equals("") || date.getText().toString().equals("Error Date")) {
                    //Toast.makeText(v.getContext(), "Please complete the form (correctly)", Toast.LENGTH_LONG).show();
                    Snackbar.make(getView(), "Please complete the form (correctly)", Snackbar.LENGTH_LONG).show();
                } else {
                    //addValidate();
                    fromtxt = tvfrom.getText().toString();
                    totxt = tvto.getText().toString();
                    datetxt = date.getText().toString();
                    pricetxt = Integer.parseInt(price.getText().toString());
                    maxptxt = Integer.parseInt(maxp.getText().toString());
                    String dtStart = datetxt;

                    if (fromtxt.equals(totxt)) {
                        Toast.makeText(v.getContext(), "Sie sind schon da...", Toast.LENGTH_LONG).show();
                    } else if (ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
                        Toast.makeText(v.getContext(), "Please sign up or login", Toast.LENGTH_LONG).show();
                    } else {

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
                            object.put("isValid", true);
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
                        } else {
                            Toast.makeText(getView().getContext(), "No user", Toast.LENGTH_LONG).show();
                        }
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
                //date.setText(dateFormat.format(date1.getTime()));
                if (year < minYear || monthOfYear < minMonth || dayOfMonth < minDay) {
                    date.setText("Error Date");
                } else {
                    date.setText(dateFormat.format(date1.getTime()));
                }
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }


    ParseObject po = new ParseObject("Route");

    private void addValidate() {
        if (!validateTo()) {
            return;
        }

        if (!validateFrom()) {
            return;
        }

        if (!validateDate()) {
            return;
        }

        if (!validatePrice()) {
            return;
        }

        if (!validateMax()) {
            return;
        }

        Toast.makeText(getContext(), "Saved!", Toast.LENGTH_SHORT).show();
    }


    private boolean validateTo() {
        if (tvto.getText().toString().trim().isEmpty()) {
            inputLayoutTo.setError(getString(R.string.err_msg_to));
            requestFocus(tvto);
            return false;
        } else {
            //inputLayoutTo.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateFrom() {
        if (tvfrom.getText().toString().trim().isEmpty()) {
            inputLayoutFrom.setError(getString(R.string.err_msg_from));
            requestFocus(tvfrom);
            return false;
        } else {
            //inputLayoutFrom.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateDate() {
        if (date.getText().toString().trim().isEmpty()) {
            inputLayoutDate.setError(getString(R.string.err_msg_date));
            requestFocus(date);
            return false;
        } else {
            //inputLayoutDate.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePrice() {
        if (price.getText().toString().trim().isEmpty()) {
            inputLayoutPrice.setError(getString(R.string.err_msg_price));
            requestFocus(price);
            return false;
        } else {
            //inputLayoutPrice.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateMax() {
        if (maxp.getText().toString().trim().isEmpty()) {
            inputLayoutMax.setError(getString(R.string.err_msg_maxperson));
            requestFocus(maxp);
            return false;
        } else {
            //inputLayoutMax.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.to:
                    validateTo();
                    break;
                case R.id.from:
                    validateFrom();
                    break;
                case R.id.date:
                    validateDate();
                    break;
                case R.id.price:
                    validatePrice();
                    break;
                case R.id.max:
                    validateMax();
                    break;
                default: break;
            }
        }
    }
}
