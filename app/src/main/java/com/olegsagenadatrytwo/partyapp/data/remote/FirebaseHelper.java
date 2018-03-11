package com.olegsagenadatrytwo.partyapp.data.remote;

import android.content.Context;
import android.graphics.Bitmap;
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
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.olegsagenadatrytwo.partyapp.Constant;
import com.olegsagenadatrytwo.partyapp.eventbus.AllUsers;
import com.olegsagenadatrytwo.partyapp.eventbus.Caller;
import com.olegsagenadatrytwo.partyapp.eventbus.Friends;
import com.olegsagenadatrytwo.partyapp.eventbus.MyLikes;
import com.olegsagenadatrytwo.partyapp.eventbus.User;
import com.olegsagenadatrytwo.partyapp.eventbus.UsersWhoRequested;
import com.olegsagenadatrytwo.partyapp.model.custompojos.Party;
import com.olegsagenadatrytwo.partyapp.view.homeactivity.PartyLabSingleTon;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
                EventBus.getDefault().post(new Caller(""));

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
                EventBus.getDefault().post(new Caller(""));
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
     * This method will send a friend request
     */
    @Override
    public void sendFriendRequest(String currentUserId, String friendUserId) {
        //if user is not null you can save the like to the current party
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();

            //add friend request to the user who is being requested
            final DatabaseReference profileReference = database.getReference(Constant.PROFILES);
            profileReference
                    .child(friendUserId)
                    .child(Constant.FRIEND_REQUEST_LIST)
                    .child(currentUserId)
                    .setValue(currentUserId);

            //add friend request to the current user to keep track of pending friend requests
            final DatabaseReference profileReferenceTwo = database.getReference(Constant.PROFILES);
            profileReferenceTwo
                    .child(currentUserId)
                    .child(Constant.CURRENT_USER_REQUESTED)
                    .child(friendUserId)
                    .setValue(friendUserId);

        }
    }

    /**
     * This method will accept friend request
     */
    @Override
    public void acceptFriendRequest(String currentUserId, String friendUserId) {
        //if user is not null you can save the like to the current party
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();

            //add friend to the Current user
            final DatabaseReference profileReference = database.getReference(Constant.PROFILES);
            profileReference
                    .child(currentUserId)
                    .child(Constant.FRIENDS)
                    .child(friendUserId)
                    .setValue(friendUserId);

            //add friend to the requesting user
            profileReference
                    .child(friendUserId)
                    .child(Constant.FRIENDS)
                    .child(currentUserId)
                    .setValue(currentUserId);

            //remove the pending request
            final DatabaseReference profileReferenceTwo = database.getReference(Constant.PROFILES);
            profileReferenceTwo
                    .child(currentUserId)
                    .child(Constant.FRIEND_REQUEST_LIST)
                    .child(friendUserId)
                    .setValue(null);

            //remove the pending request
            profileReferenceTwo
                    .child(friendUserId)
                    .child(Constant.CURRENT_USER_REQUESTED)
                    .child(currentUserId)
                    .setValue(null);

        }
    }

    /**
     * This method will remove friend
     */
    @Override
    public void removeFriend(String currentUserId, String friendUserId) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();

            //remove friend from the Current user
            final DatabaseReference profileReference = database.getReference(Constant.PROFILES);
            profileReference
                    .child(currentUserId)
                    .child(Constant.FRIENDS)
                    .child(friendUserId)
                    .setValue(null);

            //remove current user from friends of the other user
            profileReference
                    .child(friendUserId)
                    .child(Constant.FRIENDS)
                    .child(currentUserId)
                    .setValue(null);

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

    /**
     * This method will add Party
     */
    @Override
    public void addParty(final Party party, Bitmap bitmap) {

        //add new UUID to the party
        UUID id = UUID.randomUUID();
        final String idString = id.toString();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        //add the image of the party to the firebase
        if(bitmap != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl("gs://partyapp-fc6fb.appspot.com/");
            StorageReference mountainImagesRef = storageRef.child("images/" + idString + ".jpg");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = mountainImagesRef.putBytes(data);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    //add the party to the user
                    final DatabaseReference profileReference = database.getReference("profiles");
                    profileReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("parties").child(idString).setValue(party);

                    //add the party to all parties
                    DatabaseReference partyReference = database.getReference("parties");
                    partyReference.child(idString).setValue(party);
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Log.d(TAG, "onSuccess: " + downloadUrl);
                    if(downloadUrl != null) {
                        party.setImageURL(downloadUrl.toString());
                    }
                    //add the party to the user
                    final DatabaseReference profileReference = database.getReference("profiles");
                    profileReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("parties").child(idString).setValue(party);

                    //add the party to all parties
                    DatabaseReference partyReference = database.getReference("parties");
                    partyReference.child(idString).setValue(party);
                }
            });

        }
    }

    /**
     * This method will Edit Party
     */
    @Override
    public void editParty(final Party party, Bitmap bitmap) {

        if(bitmap != null) {

            //add the image of the party to the firebase
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl("gs://partyapp-fc6fb.appspot.com/");
            StorageReference mountainImagesRef = storageRef.child("images/" + party.getId() + ".jpg");
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] data = byteArrayOutputStream.toByteArray();
            UploadTask uploadTask = mountainImagesRef.putBytes(data);

            //set a listener when image is done uploading
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    saveEditParty(party);

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    saveEditParty(party);
                }
            });

        }else{
            //Edit the party to the user
            saveEditParty(party);
        }
    }

    @Override
    public void updateImageURLForUser(String url) {
        //get Reference to database
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference profileReference = database.getReference("profiles");
        //edit user image url
        profileReference
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("imageURL")
                .setValue(url);
    }

    /**
     * helper method to edit Party
     */
    private void saveEditParty(Party party) {

        //get Reference to database
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        //Edit the party to the user
        final DatabaseReference profileReference = database.getReference("profiles");

        //edit each individual field inorder to keep likes saved
        profileReference
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("parties")
                .child(party.getId())
                .child(Constant.OWNERID)
                .setValue(party.getOwnerId());

        profileReference
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("parties")
                .child(party.getId())
                .child(Constant.PARTYNAME)
                .setValue(party.getPartyName());

        profileReference
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("parties")
                .child(party.getId())
                .child(Constant.DESCRIPTION)
                .setValue(party.getDescription());

        profileReference
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("parties")
                .child(party.getId())
                .child(Constant.ADDRESS)
                .setValue(party.getAddress());

        profileReference
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("parties")
                .child(party.getId())
                .child(Constant.DATE)
                .setValue(party.getDate());

        profileReference
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("parties")
                .child(party.getId())
                .child(Constant.ENDDATE)
                .setValue(party.getEndDate());

        profileReference
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("parties")
                .child(party.getId())
                .child(Constant.STARTTIME)
                .setValue(party.getStartTime());

        profileReference
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("parties")
                .child(party.getId())
                .child(Constant.ENDTIME)
                .setValue(party.getEndTime());

        profileReference
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("parties")
                .child(party.getId())
                .child(Constant.AGEREQUIRED)
                .setValue(party.getAgeRequired());

        profileReference
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("parties")
                .child(party.getId())
                .child(Constant.CAPACITY)
                .setValue(party.getCapacity());

        profileReference
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("parties")
                .child(party.getId())
                .child(Constant.IMAGEURL)
                .setValue(party.getImageURL());

        profileReference
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("parties")
                .child(party.getId())
                .child(Constant.LATLNG)
                .setValue(party.getLatlng());

        //edit the party to all parties
        DatabaseReference partyReference = database.getReference("parties");

        partyReference
                .child(party.getId())
                .child(Constant.OWNERID)
                .setValue(party.getOwnerId());

        partyReference
                .child(party.getId())
                .child(Constant.PARTYNAME)
                .setValue(party.getPartyName());

        partyReference
                .child(party.getId())
                .child(Constant.DESCRIPTION)
                .setValue(party.getDescription());

        partyReference
                .child(party.getId())
                .child(Constant.ADDRESS)
                .setValue(party.getAddress());

        partyReference
                .child(party.getId())
                .child(Constant.DATE)
                .setValue(party.getDate());

        partyReference
                .child(party.getId())
                .child(Constant.ENDDATE)
                .setValue(party.getEndDate());

        partyReference
                .child(party.getId())
                .child(Constant.STARTTIME)
                .setValue(party.getStartTime());

        partyReference
                .child(party.getId())
                .child(Constant.ENDTIME)
                .setValue(party.getEndTime());

        partyReference
                .child(party.getId())
                .child(Constant.AGEREQUIRED)
                .setValue(party.getAgeRequired());

        partyReference
                .child(party.getId())
                .child(Constant.CAPACITY)
                .setValue(party.getCapacity());

        partyReference
                .child(party.getId())
                .child(Constant.IMAGEURL)
                .setValue(party.getImageURL());

        partyReference
                .child(party.getId())
                .child(Constant.LATLNG)
                .setValue(party.getLatlng());

    }

    public void getAllUsers() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference profileReference = database.getReference(Constant.PROFILES);

        profileReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> userIDs = new ArrayList<>();
                List<String> usernameList = new ArrayList<>();
                List<String> listOfAlreadyFriends = new ArrayList<>();
                List<String> listOfAlreadyRequested = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    userIDs.add(snapshot.getKey());
                    usernameList.add(snapshot.child("username").getValue().toString());

                    //if the id is the current users id, then get the list of his friends
                    if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(snapshot.getKey())){
                        for(DataSnapshot snap : snapshot.child(Constant.FRIENDS).getChildren()){
                            listOfAlreadyFriends.add(snap.getKey());
                        }
                        for(DataSnapshot snap : snapshot.child(Constant.CURRENT_USER_REQUESTED).getChildren()){
                            listOfAlreadyRequested.add(snap.getKey());
                        }
                    }

                }
                Log.d(TAG, "onDataChange my id: " + FirebaseAuth.getInstance().getCurrentUser().getUid());
                EventBus.getDefault().post(new AllUsers(userIDs, usernameList, listOfAlreadyFriends, listOfAlreadyRequested));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void getMyFriends() {

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference profileReference = database.getReference(Constant.PROFILES).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(Constant.FRIENDS);

        profileReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> userIDsOfFriends = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    userIDsOfFriends.add(snapshot.getKey());
                }
                EventBus.getDefault().post(new Friends(userIDsOfFriends));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    /**
     * This method will get pending Friend Requests list
     */
    @Override
    public void getPendingFriendRequests() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference profileReference = database.getReference(Constant.PROFILES)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(Constant.FRIEND_REQUEST_LIST);

        profileReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> userIDsWhoRequested = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    userIDsWhoRequested.add(snapshot.getKey());
                }
                EventBus.getDefault().post(new UsersWhoRequested(userIDsWhoRequested));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * This method will get current users username
     */
    @Override
    public void getMyUsername() {
        //likes holds ids of parties that current user liked

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference profileReference = database.getReference(Constant.PROFILES).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("username");

        profileReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                EventBus.getDefault().post(new User(dataSnapshot.getValue().toString()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * This method will update Username of the current user
     */
    @Override
    public void updateUsername(final String username, final String oldUsername) {

        //______________________________________________________________

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference firebaseRef = database.getReference();
        firebaseRef.child("usernames").child(username).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                if (mutableData.getValue() == null) {
                    mutableData.setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    return Transaction.success(mutableData);
                }

                return Transaction.abort();
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean commited, DataSnapshot dataSnapshot) {
                if (commited) {
                    // username saved
                    final DatabaseReference profileReference = database.getReference(Constant.PROFILES).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("username");
                    profileReference.setValue(username);

                    final DatabaseReference usernameRef = database.getReference("usernames").child(oldUsername);
                    usernameRef.setValue(null);
                    EventBus.getDefault().post(new Caller("Username Updated"));

                } else {
                    // username exists
                    EventBus.getDefault().post(new Caller("Username already taken, Try a different one!"));

                }
            }


        });

        //______________________________________________________________

    }
}
