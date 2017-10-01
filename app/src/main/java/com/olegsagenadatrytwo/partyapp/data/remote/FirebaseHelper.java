package com.olegsagenadatrytwo.partyapp.data.remote;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.olegsagenadatrytwo.partyapp.Constant;
import com.olegsagenadatrytwo.partyapp.eventbus.Caller;
import com.olegsagenadatrytwo.partyapp.eventbus.MyLikes;
import com.olegsagenadatrytwo.partyapp.model.custompojos.Party;
import com.olegsagenadatrytwo.partyapp.view.homeactivity.PartyLabSingleTon;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.olegsagenadatrytwo.partyapp.utilities.location.LocationUtilities.getDistanceFromDeviceLocation;

public class FirebaseHelper implements FirebaseInterface {

    /**
     * Method that gets all parties from Fire base
     */
    @Override
    public void getAllParties(final Context context) {
        //add the listener to change the list when its changed
        DatabaseReference partiesReference = FirebaseDatabase.getInstance().getReference("parties");

        partiesReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final Party party = dataSnapshot.getValue(Party.class);

                if (party != null) {
                    party.setId(dataSnapshot.getKey());
                    //create an array list of likes and fill it with user ids that liked this party
                    ArrayList<String> likes = new ArrayList<>();
                    for (DataSnapshot snap : dataSnapshot.child("profileIdLikes").getChildren()) {
                        if (snap.getValue() != null) {
                            likes.add(snap.getValue().toString());
                        }
                    }
                    party.setProfileIdLikes(likes);
                    setupParty(party, Constant.ADD_NEW_PARTY, context);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                final Party party = dataSnapshot.getValue(Party.class);
                if (party != null) {
                    party.setId(dataSnapshot.getKey());
                    //create an array list of likes and fill it with user ids that liked this party
                    ArrayList<String> likes = new ArrayList<>();
                    for (DataSnapshot snap : dataSnapshot.child("profileIdLikes").getChildren()) {
                        if (snap.getValue() != null) {
                            likes.add(snap.getValue().toString());
                        }
                    }
                    party.setProfileIdLikes(likes);
                    setupParty(party, Constant.UPDATE_PARTY, context);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                final Party party = dataSnapshot.getValue(Party.class);
                if (party != null) {
                    party.setId(dataSnapshot.getKey());
                    setupParty(party, Constant.DELETE_PARTY, context);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /**
     * Helper method to get all parties from fire base
     */
    private void setupParty(final Party party, final String task, final Context context) {

        //get reference to storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://partyapp-fc6fb.appspot.com/");

        //set the distance of the party
        try {
            party.setDistance(getDistanceFromDeviceLocation(party, context));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //download image
        storageRef.child("images/" + party.getId() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                party.setImageURL(uri.toString());
                PartyLabSingleTon partyLabSingleTon = PartyLabSingleTon.getInstance(context);

                switch (task) {
                    case Constant.ADD_NEW_PARTY:
                        partyLabSingleTon.getEvents().add(party);
                        break;
                    case Constant.UPDATE_PARTY:
                        partyLabSingleTon.getEvents().set(partyLabSingleTon.getEvents().indexOf(party), party);
                        break;
                    case Constant.DELETE_PARTY:
                        partyLabSingleTon.getEvents().remove(partyLabSingleTon.getEvents().indexOf(party));
                        break;
                }
                EventBus.getDefault().post(new Caller());

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                PartyLabSingleTon partyLabSingleTon = PartyLabSingleTon.getInstance(context);

                switch (task) {
                    case Constant.ADD_NEW_PARTY:
                        partyLabSingleTon.getEvents().add(party);
                        break;
                    case Constant.UPDATE_PARTY:
                        partyLabSingleTon.getEvents().set(partyLabSingleTon.getEvents().indexOf(party), party);
                        break;
                    case Constant.DELETE_PARTY:
                        partyLabSingleTon.getEvents().remove(partyLabSingleTon.getEvents().indexOf(party));
                        break;
                }
                EventBus.getDefault().post(new Caller());
            }
        });
    }

    @Override
    public void saveLike(Party party, List<String> likes) {
        Log.d("abc", "saveLike: ");
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            //add the like to the party inside user profile
            final DatabaseReference profileReference = database.getReference("profiles");
            profileReference.child(party.getOwnerId()).child("parties").child(party.getId()).setValue(party);

            //add like to the party where all parties
            DatabaseReference partyReference = database.getReference("parties");
            partyReference.child(party.getId()).setValue(party);

            //add the liked party to the user liked party List
            profileReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("myLikes").setValue(likes);

        }
    }

    @Override
    public void getMyLikes() {
        Log.d("abc", "getMyLikes: " + 1);
        final List<String> likes = new ArrayList<>();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference profileReference = database.getReference("profiles");

        profileReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("abc", "getMyLikes: " + 2);
                for(DataSnapshot snapshot: dataSnapshot
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("myLikes").getChildren()){
                    likes.add(snapshot.getValue().toString());
                }
                EventBus.getDefault().post(new MyLikes(likes));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("abc", "getMyLikes: " + 3);

            }
        });
    }
}
