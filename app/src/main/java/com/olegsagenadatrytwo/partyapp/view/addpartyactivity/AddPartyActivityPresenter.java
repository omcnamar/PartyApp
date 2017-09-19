package com.olegsagenadatrytwo.partyapp.view.addpartyactivity;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.olegsagenadatrytwo.partyapp.model.custompojos.Party;
import com.olegsagenadatrytwo.partyapp.model.custompojos.Profile;

import java.util.UUID;

public class AddPartyActivityPresenter implements AddPartyActivityContract.presenter {

    private static final String TAG = "AdActivityPresenter";
    private AddPartyActivityContract.view view;
    private Context context;

    private FirebaseDatabase database;

    @Override
    public void attachView(AddPartyActivityContract.view view) {
        this.view = view;
    }

    @Override
    public void removeView() {
        this.view = null;
    }

    @Override
    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void init() {
        database = FirebaseDatabase.getInstance();
    }

    @Override
    public void addNewParty(final Party party) {

        String id = UUID.randomUUID().toString();

        final DatabaseReference profileReference = database.getReference("profiles");
        profileReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("parties").child(id).setValue(party);

        DatabaseReference partyReference = database.getReference("parties");
        partyReference.child(id).setValue(party);
        view.partySaved(true);
    }
}
