package com.engineering.software.sapi.project;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by Zsolt on 2015. 12. 08..
 */
public class OwnRoutesAdapter extends BaseAdapter {

    private String objId;
    private Activity context;
    private List<ParseObject> list;

    public OwnRoutesAdapter(Activity context, List<ParseObject> list) {
        this.context = context;
        this.list = list;
    }


    @Override
    public int getCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        if (v == null) {
            LayoutInflater inflater;
            inflater = LayoutInflater.from(context);
            v = inflater.inflate(R.layout.own_route_items, null);
        }
        ParseObject object = (ParseObject) getItem(i);
        TextView from = (TextView) v.findViewById(R.id.own_from);
        TextView destination = (TextView) v.findViewById(R.id.own_to);
        TextView date = (TextView) v.findViewById(R.id.own_date);
        TextView passangers = (TextView) v.findViewById(R.id.own_passengers);
        from.setText(object.getString("from"));
        destination.setText(object.getString("destionation"));
        date.setText(object.getString("date"));
        passangers.setText(object.get("numberOfPassengers").toString());
        return v;
    }
}


