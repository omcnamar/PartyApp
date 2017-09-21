package com.olegsagenadatrytwo.partyapp.view.homeactivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.olegsagenadatrytwo.partyapp.R;
import com.olegsagenadatrytwo.partyapp.customviews.AutoResizeTextView;
import com.olegsagenadatrytwo.partyapp.model.custompojos.Party;

import java.util.List;
import java.util.UUID;

public class PartyFragment extends Fragment {

    private static final String PARTY_ID = "party_id";
    private List<Party> parties;
    private Party party;
    private AutoResizeTextView tvPartyName;
    private AutoResizeTextView tvDescription;
    private ImageView ivLogo;

    public static PartyFragment newInstance(UUID id){
        Bundle args = new Bundle();
        args.putSerializable(PARTY_ID, id);
        PartyFragment fragment = new PartyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get the id of the event that were are trying to display
        UUID id = (UUID) getArguments().getSerializable(PARTY_ID);
        //get the singleTon that holds the list of the events
        PartyLabSingleTon partySingleton = PartyLabSingleTon.getInstance(getActivity());
        //get the list of the events from the singleton
        parties = partySingleton.getEvents();
        //find the specific event based on the id
        party = getParty(id);
        setHasOptionsMenu(true);
    }

    //this method will return the Event from the list of events based on id of the event passed
    public Party getParty(UUID id){
        for(int i = 0; i< parties.size(); i++){
            if(parties.get(i).getId() == id){
                return parties.get(i);
            }
        }
        return null;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.party_card_item2, container, false);
        tvPartyName = v.findViewById(R.id.tvPartyType);
        tvDescription = v.findViewById(R.id.tvPartyDescription);

        ivLogo = v.findViewById(R.id.ivPartyHeader);

        //if the event is not null than set the ImageViews and TextViews according to the Event
        if(party != null) {
            tvPartyName.setText(party.getPartyName());
            tvDescription.setText(party.getDescription());

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.partylogo);
            requestOptions.error(R.drawable.partylogo);
            requestOptions.fallback(R.drawable.partylogo);
            requestOptions.centerCrop();

            Glide.with(getActivity())
                    .load(R.drawable.partylogo)
                    .apply(requestOptions)
                    .into(ivLogo);

        }


        DatabaseReference partiesReference = FirebaseDatabase.getInstance().getReference("parties");
        //add the listener to change the current item on the screen if changed
        partiesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<Party> existingParties = PartyLabSingleTon.getInstance(getActivity()).getEvents();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Party p =  snapshot.getValue(Party.class);
                    p.setId(UUID.fromString(snapshot.getKey()));
                    boolean changed = false;
                    //determine if the party is in the current list
                    for (int i = 0; i < existingParties.size(); i++) {
                        if(p.getId().toString().equals(party.getId().toString())){
                            tvPartyName.setText(p.getPartyName());
                            tvDescription.setText(p.getDescription());
                        }
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return v;
    }


}