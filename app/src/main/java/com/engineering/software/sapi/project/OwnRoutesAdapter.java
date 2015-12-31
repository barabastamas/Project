package com.engineering.software.sapi.project;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by Zsolt on 2015. 12. 08..
 */
public class OwnRoutesAdapter extends RecyclerView.Adapter<OwnRoutesAdapter.ViewHolder> {

    private List<ParseObject> list = null;
    private Fragment parentFragment = null;
    private LayoutInflater layoutInflater;
    private String routeObjectId;

    public OwnRoutesAdapter(List<ParseObject> list, Fragment fragment, Context context) {
        this.list = list;
        this.parentFragment = fragment;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_own_routes, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ParseObject obj = list.get(position);

        routeObjectId = obj.getObjectId();

        holder.objectId = obj.get("routeOwner").toString();
        holder.from.setText(obj.get("from").toString());
        holder.dest.setText(obj.get("destination").toString());
        holder.date.setText(obj.get("date").toString());
        List<String> list = obj.getList("passengers");
        if (list != null) {
            holder.pass.setText("" + list.size());
        } else {
            holder.pass.setText("0");
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView from;
        private TextView dest;
        private TextView date;
        private TextView pass;
        private CardView cardView;
        private String objectId;

        public ViewHolder(View itemView) {
            super(itemView);
            from = (TextView) itemView.findViewById(R.id.own_from);
            dest = (TextView) itemView.findViewById(R.id.own_to);
            date = (TextView) itemView.findViewById(R.id.own_date);
            pass = (TextView) itemView.findViewById(R.id.own_passengers);
            cardView = (CardView) itemView.findViewById(R.id.card_view_own_routes);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("Object_ID", routeObjectId);
                    FragmentManager fragmentManager = parentFragment.getFragmentManager();
                    EditRouteFragment fragment = new EditRouteFragment();
                    fragment.setArguments(bundle);
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    ft.replace(R.id.content, fragment);
                    ft.addToBackStack("EditRouteFragment");
                    ft.commit();
                }
            });
        }
    }
}


