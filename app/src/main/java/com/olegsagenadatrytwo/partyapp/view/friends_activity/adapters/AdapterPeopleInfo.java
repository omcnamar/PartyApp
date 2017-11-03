package com.olegsagenadatrytwo.partyapp.view.friends_activity.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.olegsagenadatrytwo.partyapp.Constant;
import com.olegsagenadatrytwo.partyapp.R;
import com.olegsagenadatrytwo.partyapp.view.friends_activity.FriendsActivityContract;

import java.util.ArrayList;
import java.util.List;

public class AdapterPeopleInfo extends RecyclerView.Adapter<AdapterPeopleInfo.ViewHolder> {

    private List<String> idsList = new ArrayList<>();
    private FriendsActivityContract.view view;
    private Context context;

    public AdapterPeopleInfo(List<String> idsList, FriendsActivityContract.view view, Context context) {
        this.idsList = idsList;
        this.view = view;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.people_user_info_init, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.tvID.setText(idsList.get(position));

        //load the profile image of each user
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference profileReference = database.getReference(Constant.PROFILES);
        profileReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String url  = dataSnapshot.child(idsList.get(position)).child("imageURL").getValue().toString();
                Glide.with(context).load(url).into(holder.ivProfileImage);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return idsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvID;
        private ImageView ivProfileImage;

        public ViewHolder(View itemView) {
            super(itemView);
            tvID = itemView.findViewById(R.id.tvUserID);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
        }
    }
}
