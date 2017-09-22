package com.olegsagenadatrytwo.partyapp.view.profileactivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.olegsagenadatrytwo.partyapp.R;
import com.olegsagenadatrytwo.partyapp.model.custompojos.Party;
import com.olegsagenadatrytwo.partyapp.view.editpartyactivity.EditPartyActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by omcna on 9/21/2017.
 */

public class MyPartiesAdapter extends RecyclerView.Adapter<MyPartiesAdapter.ViewHolder>{

    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    private List<Party> parties = new ArrayList<>();
    private Context context;

    public MyPartiesAdapter(List<Party> parties, Context context) {
        this.parties = parties;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_for_adapter_my_parties, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.tvPartyTitle.setText(parties.get(position).getPartyName());
        holder.tvPartyAddress.setText(parties.get(position).getAddress());

        final Party party = parties.get(position);
        viewBinderHelper.bind(holder.swipeLayout, party.getId());

        //get reference to storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://partyapp-fc6fb.appspot.com/");

        //download image
        storageRef.child("images/" + parties.get(position).getId() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(context)
                        .load(uri.toString())
                        .into(holder.ivPartyLogo);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });

        holder.tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editParty = new Intent(context, EditPartyActivity.class);
                editParty.putExtra("party", parties.get(position));
                context.startActivity(editParty);
            }
        });

        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "delete " + party.getId(), Toast.LENGTH_SHORT).show();
                parties.remove(position);
                notifyDataSetChanged();

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                ref.child("parties").child(party.getId()).removeValue();
                ref.child("profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("parties")
                        .child(party.getId()).removeValue();

                //get reference to storage
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReferenceFromUrl("gs://partyapp-fc6fb.appspot.com/");
                //delete image
                storageRef.child("images/" + party.getId() + ".jpg").delete();

            }
        });
    }

    @Override
    public int getItemCount() {
        return parties.size();
    }

    public void saveStates(Bundle outState) {
        viewBinderHelper.saveStates(outState);
    }

    public void restoreStates(Bundle inState) {
        viewBinderHelper.restoreStates(inState);
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView ivPartyLogo;
        private TextView tvPartyTitle;
        private TextView tvPartyAddress;
        private SwipeRevealLayout swipeLayout;
        private ImageView ivDelete;
        private TextView tvEdit;

        public ViewHolder(View itemView) {
            super(itemView);
            swipeLayout = itemView.findViewById(R.id.swipe_layout);
            ivPartyLogo = itemView.findViewById(R.id.ivPartyLogo);
            tvPartyTitle = itemView.findViewById(R.id.tvPartyTitleRV);
            tvPartyAddress = itemView.findViewById(R.id.tvPartyAddress);
            ivDelete = itemView.findViewById(R.id.ivDeleteParty);
            tvEdit = itemView.findViewById(R.id.tvEdit);
        }
    }
}
