package com.engineering.software.sapi.project;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class OwnRoutesFragment extends Fragment {

    private ListView listview;
    private List<ParseObject> list;
    private OwnRoutesAdapter adapter = null;
    private View view;

    public OwnRoutesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_own_routes, container, false);
        listview = (ListView) view.findViewById(R.id.routelist);
        getRoutes();
        adapter = new OwnRoutesAdapter(getActivity(), list);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                FragmentManager fragmentManager = getFragmentManager();
                EditRouteFragment fragment = new EditRouteFragment();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.content, fragment);
                ft.addToBackStack("EditRouteFragment");
                ft.commit();
            }
        });
        return view;
    }

    public void getRoutes() {
        String objId = ParseUser.getCurrentUser().getObjectId();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Routes");
        query.whereEqualTo(objId, "routeOwner");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    list = objects;
                }
            }
        });
    }

}
