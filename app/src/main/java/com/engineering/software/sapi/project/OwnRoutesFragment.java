package com.engineering.software.sapi.project;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class OwnRoutesFragment extends Fragment {

    private RecyclerView recyclerView;
    private OwnRoutesAdapter adapter;
    private List<ParseObject> list = new ArrayList<>();
    private View view;
    private ParseUser currentUser;
    private String objId;
    private ProgressDialog progressDialog;

    public OwnRoutesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_own_routes, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.own_recyclerview);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading your routes…");
        progressDialog.show();
        getRoutes();
        progressDialog.dismiss();
        return view;
    }

    public void getRoutes() {
        currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            objId = currentUser.getObjectId();
        }
        ParseQuery<ParseObject> query = ParseQuery.getQuery(getString(R.string.drawer_sub_title_routes));
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject i : objects) {
                        if (i.getString(getString(R.string.routeOwner)).equals(objId)) {
                            list.add(i);
                        }
                    }
                    if (list != null) {
                        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false);
                        gridLayoutManager.canScrollHorizontally();
                        recyclerView.setLayoutManager(gridLayoutManager);
                        adapter = new OwnRoutesAdapter(list, OwnRoutesFragment.this, getContext());
                        recyclerView.setAdapter(adapter);
                    }
                }
            }
        });
    }
}
