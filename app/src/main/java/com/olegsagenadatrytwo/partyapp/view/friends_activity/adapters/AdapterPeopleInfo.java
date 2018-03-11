package com.olegsagenadatrytwo.partyapp.view.friends_activity.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.olegsagenadatrytwo.partyapp.Constant;
import com.olegsagenadatrytwo.partyapp.R;
import com.olegsagenadatrytwo.partyapp.customviews.AutoResizeTextView;
import com.olegsagenadatrytwo.partyapp.data.remote.FirebaseHelper;
import com.olegsagenadatrytwo.partyapp.view.friends_activity.FriendsActivityContract;

import java.util.ArrayList;
import java.util.List;

public class AdapterPeopleInfo extends RecyclerView.Adapter<AdapterPeopleInfo.ViewHolder> {

    private List<String> idsList = new ArrayList<>();
    private List<String> usernameList = new ArrayList<>();
    private List<String> alreadyFriendsList = new ArrayList<>();
    private List<String> alreadyRequested = new ArrayList<>();
    private FriendsActivityContract.view view;
    private Context context;

    public AdapterPeopleInfo(List<String> idsList, List<String> usernameList, List<String> alreadyFriendsList, List<String> alreadyRequested , FriendsActivityContract.view view, Context context) {
        this.idsList = idsList;
        this.usernameList = usernameList;
        this.alreadyFriendsList = alreadyFriendsList;
        this.alreadyRequested = alreadyRequested;
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
        holder.tvID.setText(usernameList.get(position));

        //do not show add on your self
        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(idsList.get(position))){
            holder.btnAddFriend.setEnabled(false);
            holder.btnAddFriend.setText("You");
        }

        //if already friends show friends
        for(String friendId : alreadyFriendsList){
            if(idsList.get(position).equals(friendId)){
                holder.btnAddFriend.setEnabled(false);
                holder.btnAddFriend.setText("Friends");
            }
        }

        //if already friends show friends
        for(String requsetedID : alreadyRequested){
            if(idsList.get(position).equals(requsetedID)){
                holder.btnAddFriend.setEnabled(false);
                holder.btnAddFriend.setText("Requested");
            }
        }

        //load the profile image of each user
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference profileReference = database.getReference(Constant.PROFILES);
        profileReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(idsList.get(position)).child("imageURL").getValue() != null) {
                    String url = dataSnapshot.child(idsList.get(position)).child("imageURL").getValue().toString();
                    Glide.with(context).load(url).into(holder.ivProfileImage);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        holder.btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseHelper firebaseHelper = new FirebaseHelper();
                firebaseHelper.sendFriendRequest(FirebaseAuth.getInstance().getCurrentUser().getUid(), idsList.get(position));
                Toast.makeText(context, "Requested " + usernameList.get(position), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return idsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private AutoResizeTextView tvID;
        private ImageView ivProfileImage;
        private Button btnAddFriend;

        public ViewHolder(View itemView) {
            super(itemView);
            tvID = itemView.findViewById(R.id.tvUserID);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            btnAddFriend = itemView.findViewById(R.id.btnAddToFriends);
        }
    }
}
