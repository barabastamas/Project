package com.engineering.software.sapi.project;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseObject;

import java.util.Date;
import java.util.List;

/**
 * Created by norby on 11/26/2015.
 */
public class RoutesAdapter extends RecyclerView.Adapter<RoutesAdapter.ViewHolder> {

    public class RouteInfo {
        protected String from;
        protected String destination;
        protected String date;
        private float price;
        protected int numberOfPassanger;

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView rTitle;
        protected TextView rFrom;
        protected TextView rTo;
        protected TextView rDate;
        protected TextView rPrice;
        protected TextView rNumbOfPass;
        protected CardView cardView;
        protected String objectId;
        protected FragmentManager manager;
        protected Fragment fragment;

        public ViewHolder(View itemView) {
            super(itemView);
            rTitle = (TextView) itemView.findViewById(R.id.title);
            rFrom = (TextView) itemView.findViewById(R.id.txtFrom);
            rTo = (TextView) itemView.findViewById(R.id.txtTo);
            rDate = (TextView) itemView.findViewById(R.id.txtDate);
            rPrice = (TextView) itemView.findViewById(R.id.txtPrice);
            rNumbOfPass = (TextView) itemView.findViewById(R.id.txtNumb);
            cardView = (CardView) itemView.findViewById(R.id.card_view);

            //lista elemeinek kivalasztasa, object id atkuldese
            cardView.setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View v) {
                    Log.d("LogCard", "yes");
                    //Toast.makeText(v.getContext(), "View", Toast.LENGTH_SHORT).show();
                    Log.d("Log", "Button was pressed");

                    Bundle bundle = new Bundle();
                    bundle.putString("Object_ID", objectId);

                    Fragment fragment;
                    fragment = new DetailRouteFragment();
                    fragment.setArguments(bundle);

                    FragmentTransaction ft = parentFragment.getFragmentManager().beginTransaction();
                    ft.replace(R.id.content, fragment);
                    ft.addToBackStack("DetailRouteFragment");

                    ft.commit();

                }
            });


        }


    }

    private List<ParseObject> routeList;
    static Fragment parentFragment;
    final LayoutInflater layoutInflater;

    public RoutesAdapter(List<ParseObject> routeList, Fragment fragment, Context context) {
        this.routeList = routeList;
        layoutInflater = LayoutInflater.from(context);
        parentFragment = fragment;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_routes, parent, false);
        return new ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int i) {
        ParseObject ri = routeList.get(i);
        holder.objectId = ri.getObjectId();
        holder.rFrom.setText(ri.get("from").toString());
        holder.rTo.setText(ri.get("destination").toString());
        holder.rDate.setText(ri.get("date").toString());
        holder.rPrice.setText(ri.get("price").toString());
        holder.rNumbOfPass.setText(ri.get("numberOfPassanger").toString());


    }

    @Override
    public int getItemCount() {
        return routeList.size();
    }


}
