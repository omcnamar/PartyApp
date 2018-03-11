package com.olegsagenadatrytwo.partyapp.view.friends_activity.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

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

public class AdapterRequests extends RecyclerView.Adapter<AdapterRequests.ViewHolder> {

    private List<String> idsList = new ArrayList<>();
    private FriendsActivityContract.view view;
    private Context context;

    public AdapterRequests(List<String> idsList, FriendsActivityContract.view view, Context context) {
        this.idsList = idsList;
        this.view = view;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //holder.tvID.setText(usernameList.get(position));

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
                holder.tvID.setText(dataSnapshot.child(idsList.get(position)).child("username").getValue().toString());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //on click for accept
        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseHelper firebaseHelper = new FirebaseHelper();
                firebaseHelper.acceptFriendRequest(FirebaseAuth.getInstance().getCurrentUser().getUid(), idsList.get(position));
            }
        });

        //on click for reject
        holder.btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                FirebaseHelper firebaseHelper = new FirebaseHelper();
//                firebaseHelper.sendFriendRequest(FirebaseAuth.getInstance().getCurrentUser().getUid(), idsList.get(position));
//                Toast.makeText(context, "Requested " + usernameList.get(position), Toast.LENGTH_SHORT).show();
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
        private Button btnAccept;
        private Button btnReject;

        public ViewHolder(View itemView) {
            super(itemView);
            tvID = itemView.findViewById(R.id.tvUserID);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}
