package com.olegsagenadatrytwo.partyapp.data.remote;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.olegsagenadatrytwo.partyapp.utilities.location.LocationUtilities.getDistanceFromDeviceLocation;

public class FirebaseHelper implements FirebaseInterface {

    public static final String TAG = "FirebaseHelper";

    /**
     * Method that gets all parties from Fire base
     */
    @Override
    public void getAllParties(final Context context) {
        //add the listener to change the list when its changed
        DatabaseReference partiesReference = FirebaseDatabase.getInstance().getReference(Constant.PARTIES);

        partiesReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final Party party = dataSnapshot.getValue(Party.class);

                if (party != null) {
                    party.setId(dataSnapshot.getKey());
                    //create Map list of likes and fill it with user ids that liked this party
                    Map<String, String> likes = new HashMap<>();
                    for (DataSnapshot snap : dataSnapshot.child(Constant.IDS_OF_USERS_WHO_LIKED_THIS_PARTY).getChildren()) {
                        if (snap.getValue() != null) {
                            likes.put(snap.getValue().toString(), snap.getValue().toString());
                        }
                    }
                    party.setIdsOfUsersWhoLikedThisParty(likes);
                    setupParty(party, Constant.ADD_NEW_PARTY, context);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                final Party party = dataSnapshot.getValue(Party.class);
                if (party != null) {
                    party.setId(dataSnapshot.getKey());
                    //create Map list of likes and fill it with user ids that liked this party
                    Map<String, String> likes = new HashMap<>();
                    for (DataSnapshot snap : dataSnapshot.child(Constant.IDS_OF_USERS_WHO_LIKED_THIS_PARTY).getChildren()) {
                        if (snap.getValue() != null) {
                            likes.put(snap.getValue().toString(), snap.getValue().toString());
                        }
                    }
                    party.setIdsOfUsersWhoLikedThisParty(likes);
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

    /**
     * This method will save a like to the party that was liked
     */
    @Override
    public void saveLike(String userId, String partyId, String partyOwnerID) {
        //if user is not null you can save the like to the current party
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();

            //add the like to the party (all parties)
            final DatabaseReference partiesReference = database.getReference(Constant.PARTIES);
            partiesReference
                    .child(partyId)
                    .child(Constant.IDS_OF_USERS_WHO_LIKED_THIS_PARTY)
                    .child(userId)
                    .setValue(userId);

            //add the like to the party (inside profile)
            final DatabaseReference profileReference = database.getReference(Constant.PROFILES);
            profileReference
                    .child(partyOwnerID)
                    .child(Constant.PARTIES)
                    .child(partyId)
                    .child(Constant.IDS_OF_USERS_WHO_LIKED_THIS_PARTY)
                    .child(userId)
                    .setValue(userId);

            //add the id of the party to the liked parties of user
            profileReference
                    .child(userId)
                    .child(Constant.IDS_OF_PARTIES_THAT_CURRNET_USER_LIKED)
                    .child(partyId)
                    .setValue(partyId);
        }
    }


    /**
     * This method will remove a like from the party that was previously liked
     */
    @Override
    public void removeLike(String userId, String partyId, String partyOwnerId) {
        //if user is not null you can remove the like from the party
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();

            //remove the like to the party (all parties)
            final DatabaseReference partiesReference = database.getReference(Constant.PARTIES);
            partiesReference
                    .child(partyId)
                    .child(Constant.IDS_OF_USERS_WHO_LIKED_THIS_PARTY)
                    .child(userId)
                    .setValue(null);

            //remove the like to the party (inside profile)
            final DatabaseReference profileReference = database.getReference(Constant.PROFILES);
            profileReference
                    .child(partyOwnerId)
                    .child(Constant.PARTIES)
                    .child(partyId)
                    .child(Constant.IDS_OF_USERS_WHO_LIKED_THIS_PARTY)
                    .child(userId)
                    .setValue(null);

            //remove the id of the party to the liked parties of user
            profileReference
                    .child(userId)
                    .child(Constant.IDS_OF_PARTIES_THAT_CURRNET_USER_LIKED)
                    .child(partyId)
                    .setValue(null);
        }
    }

    /**
     * This method will get ids of all parties that current user liked
     */
    @Override
    public void getMyLikes() {
        //likes holds ids of parties that current user liked
        final List<String> likes = new ArrayList<>();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference profileReference = database.getReference(Constant.PROFILES);

        profileReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(Constant.IDS_OF_PARTIES_THAT_CURRNET_USER_LIKED).getChildren()) {
                    likes.add(snapshot.getValue().toString());
                }
                EventBus.getDefault().post(new MyLikes(likes));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
