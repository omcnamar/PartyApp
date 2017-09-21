package com.olegsagenadatrytwo.partyapp.view.mypartiesactivity;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.olegsagenadatrytwo.partyapp.model.custompojos.Party;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by omcna on 9/21/2017.
 */

public class MyPartiesActivityPresenter implements MyPartiesActivityContract.Presenter{

    public static final String TAG = "MyPartiesActivity";
    private MyPartiesActivityContract.View view;
    private Context context;

    @Override
    public void attachView(MyPartiesActivityContract.View view) {
        this.view = view;
    }

    @Override
    public void removeView() {
        this.view = null;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void getMyParties() {
        //database.getReference().child("Student1").child("Grades");
        DatabaseReference partiesReference = FirebaseDatabase.getInstance().getReference("profiles").
                child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("parties");
        partiesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Party> parties = new ArrayList<>();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Party party =  snapshot.getValue(Party.class);
                    if(party != null) {
                        party.setId(snapshot.getKey());
                        parties.add(party);
                    }
                }
                view.myPartiesLoadedUpdateUI(parties);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
