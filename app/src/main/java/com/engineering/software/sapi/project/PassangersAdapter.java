package com.engineering.software.sapi.project;

import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.engineering.software.sapi.project.Profile.ProfileFragment;

import java.util.List;

public class PassangersAdapter extends RecyclerView.Adapter<PassangersAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView passengerName;
        Button buttonViewProfile;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view_passenger);
            buttonViewProfile = (Button) itemView.findViewById(R.id.button_view_profile);
            passengerName = (TextView) itemView.findViewById(R.id.text_view_passenger_name);

            buttonViewProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (parentFragment != null) {
                        Fragment fragment = new ProfileFragment();

                        FragmentTransaction fragmentTransaction = parentFragment.getFragmentManager().beginTransaction();

                        fragmentTransaction.hide(parentFragment);
                        fragmentTransaction.add(R.id.content, fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                }
            });
        }
    }

    static Fragment parentFragment = null;
    final LayoutInflater layoutInflater;

    List<String> passengers;

    PassangersAdapter(Context context, List<String> passengers, Fragment fragment) {
        layoutInflater = LayoutInflater.from(context);
        this.passengers = passengers;
        parentFragment = fragment;

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
