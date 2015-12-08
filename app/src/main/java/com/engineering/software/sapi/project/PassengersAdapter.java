package com.engineering.software.sapi.project;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.engineering.software.sapi.project.Profile.ProfileFragment;
import com.parse.ParseUser;

import java.util.List;

public class PassengersAdapter extends RecyclerView.Adapter<PassengersAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView passengerName;
        Button buttonViewProfile;
        ImageView imageViewProfileImage;
        String userObjectID;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view_passenger);
            buttonViewProfile = (Button) itemView.findViewById(R.id.button_view_profile);
            passengerName = (TextView) itemView.findViewById(R.id.text_view_passenger_name);
            imageViewProfileImage = (ImageView) itemView.findViewById(R.id.card_view_profile_image);

            buttonViewProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (parentFragment != null) {
                        Bundle arg = new Bundle();
                        arg.putString("userObjectID", userObjectID);

                        Fragment fragment = new ProfileFragment();
                        fragment.setArguments(arg);

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

    List<Pair<ParseUser, Bitmap>> passengers;

    PassengersAdapter(Context context, List<Pair<ParseUser, Bitmap>> passengers, Fragment fragment) {
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
        holder.userObjectID = passengers.get(position).first.getObjectId();
        holder.passengerName.setText(passengers.get(position).first.get("name").toString());
        holder.imageViewProfileImage.setImageBitmap(passengers.get(position).second);
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
