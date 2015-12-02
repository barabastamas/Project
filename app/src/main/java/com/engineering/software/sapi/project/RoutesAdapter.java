package com.engineering.software.sapi.project;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.Date;
import java.util.List;

/**
 * Created by norby on 11/26/2015.
 */
public class RoutesAdapter extends RecyclerView.Adapter<RoutesAdapter.ViewHolder>{
    public class RouteInfo {
        protected String from;
        protected String destination;
        protected String date;
        private float price;
        protected int numberOfPassanger;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView rFrom;
        protected TextView rTo;
        protected TextView rDate;
        protected TextView rPrice;
        protected TextView rNumbOfPass;

        public ViewHolder(View itemView) {
            super(itemView);
            rFrom = (TextView) itemView.findViewById(R.id.txtFrom);
            rTo = (TextView) itemView.findViewById(R.id.txtTo);
            rDate = (TextView) itemView.findViewById(R.id.txtDate);
            rPrice = (TextView) itemView.findViewById(R.id.txtPrice);
            rNumbOfPass = (TextView) itemView.findViewById(R.id.txtNumb);

        }
    }
    private List<ParseObject> routeList;
    public RoutesAdapter(List<ParseObject> routeList){
        this.routeList = routeList;
    }
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_routes, parent, false);
        return new ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int i) {
        ParseObject ri = routeList.get(i);
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
