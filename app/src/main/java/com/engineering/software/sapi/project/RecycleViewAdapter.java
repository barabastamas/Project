package com.engineering.software.sapi.project;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView passengerName;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view_passenger);
            passengerName = (TextView) itemView.findViewById(R.id.text_view_passenger_name);
        }
    }

    List<String> passengers;

    RecycleViewAdapter(List<String> passengers){
        this.passengers = passengers;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_row_passangers, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.passengerName.setText(passengers.get(position));
    }

    @Override
    public int getItemCount() {
        return passengers.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
