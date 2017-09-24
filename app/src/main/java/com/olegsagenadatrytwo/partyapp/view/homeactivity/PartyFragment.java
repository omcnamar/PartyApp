package com.olegsagenadatrytwo.partyapp.view.homeactivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.olegsagenadatrytwo.partyapp.R;
import com.olegsagenadatrytwo.partyapp.customviews.AutoResizeTextView;
import com.olegsagenadatrytwo.partyapp.model.custompojos.Party;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PartyFragment extends Fragment implements ChildEventListener, View.OnClickListener {

    public static final String TAG = "PartyFragment";
    private static final String PARTY_ID = "party_id";


    AppCompatImageButton btnLike;
    AppCompatImageButton btnShareParty;
    AppCompatImageButton btnPublicOrPrivate;

    ImageView ivLogo;
    AutoResizeTextView tvPartyType;
    AutoResizeTextView tvDescription;
    CircleImageView ivPartyHost;

    private List<Party> parties;
    private Party party;
    private DatabaseReference partiesReference;
    private Context context;

    TextView tvDistance;

    public static PartyFragment newInstance(String id) {
        Bundle args = new Bundle();
        args.putString(PARTY_ID, id);
        PartyFragment fragment = new PartyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get the id of the event that were are trying to display
        String id = getArguments().getString(PARTY_ID);
        //get the singleTon that holds the list of the events
        PartyLabSingleTon partySingleton = PartyLabSingleTon.getInstance(getActivity());
        //get the list of the events from the singleton
        parties = partySingleton.getEvents();
        //find the specific event based on the id
        party = getParty(id);

        this.context = getActivity();

        partiesReference = FirebaseDatabase.getInstance().getReference("parties");
        partiesReference.addChildEventListener(this);
        setHasOptionsMenu(true);
    }

    //this method will return the Event from the list of events based on id of the event passed
    public Party getParty(String id) {
        for (int i = 0; i < parties.size(); i++) {
            if (parties.get(i).getId().equals(id)) {
                return parties.get(i);
            }
        }
        return null;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        View v = inflater.inflate(R.layout.party_card_item2, container, false);
        ivPartyHost = v.findViewById(R.id.ivPartyHost);
        btnLike = v.findViewById(R.id.btnLike);
        btnShareParty = v.findViewById(R.id.btnShareParty);
        btnPublicOrPrivate = v.findViewById(R.id.btnPublicOrPrivate);
        tvPartyType = v.findViewById(R.id.tvPartyType);
        tvDescription= v.findViewById(R.id.tvPartyDescription);
        ivLogo = v.findViewById(R.id.ivPartyHeader);
        btnLike.setOnClickListener(this);
        btnPublicOrPrivate.setOnClickListener(this);
        btnShareParty.setOnClickListener(this);

        tvDistance = v.findViewById(R.id.tvPartyDistance);

        ivLogo = v.findViewById(R.id.ivPartyHeader);
        tvPartyType = v.findViewById(R.id.tvPartyType);
        tvDescription = v.findViewById(R.id.tvPartyDescription);
        ivPartyHost = v.findViewById(R.id.ivPartyHost);

        //if the party is not null than set the ImageViews and TextViews according to the Event
        if (party != null) {
            tvPartyType.setText(party.getPartyName());
            tvDescription.setText(party.getDescription());
//            double distance = Double.parseDouble(party.getDistance().replace(",", "").replaceAll("[^\\d.]", ""));
//            if(distance <=10000000) {
//                tvDistance.setText(String.valueOf(distance) + " miles away");
//            } else {
//                tvDistance.setText("Unknown distance");
//            }
            loadPartyImage(party.getImageURL(), ivLogo); // Header Image
            getHostImage(party);

            if (party.isLiked()){
                btnLike.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_like_48dp));
            } else {
                btnLike.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_unlike));
            }
        }
        return v;
    }

    //this method will place the Host of the party image into ivPartyHost
    private void getHostImage(Party p) {
        //get reference to storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://partyapp-fc6fb.appspot.com/");

        //download host image
        storageRef.child("images/" + p.getOwnerId() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                loadPartyImage(uri.toString(), ivPartyHost); // Host Image
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                loadPartyImage(null, ivPartyHost); // Host Image
            }
        });
    }

    private void loadPartyImagePicasso(String url, ImageView imageView) {
        Log.d(TAG, "loadPartyImagePicasso: ");
        if (party.getImageURL() != null) {
            Picasso.with(getActivity())
                    .load(url)
                    .placeholder(R.drawable.partylogo)
                    .centerCrop()
                    .into(imageView);
        }
    }

    private void loadPartyImage(String url, ImageView imageView) {

        Log.d(TAG, "loadPartyImageGlide: ");
        // Sets properties for Glide images
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.centerCrop();

        if (url == null) {

            Glide.with(context)
                    .load(R.drawable.partylogo)
                    .apply(requestOptions)
                    .into(imageView);

        } else {

            if (context != null) {
                Log.d(TAG, "loadPartyImageGlide: " + "getActivity() was not null");

                Glide.with(context)
                        .load(url)
                        .apply(requestOptions)
                        .into(imageView);
            } else {
                Log.d(TAG, "loadPartyImageGlide: " + "getActivity() was null gonna try picasso");
                loadPartyImagePicasso(url, imageView);
            }

        }

    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        Log.d(TAG, "onChildChanged: ");
        final Party partyChanged = dataSnapshot.getValue(Party.class);
        partyChanged.setId(dataSnapshot.getKey());

        if(party != null) {
            //if the party that was changed is the one on the screen update the changes live
            if (partyChanged.getId().equals(party.getId())) {
                //get reference to storage
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReferenceFromUrl("gs://partyapp-fc6fb.appspot.com/");

                //download image
                storageRef.child("images/" + partyChanged.getId() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d(TAG, "onSuccess: " + "image success");
                        party.setImageURL(uri.toString());
                        tvPartyType.setText(partyChanged.getPartyName());
                        tvDescription.setText(partyChanged.getDescription());
                        party.setPartyName(partyChanged.getPartyName());
                        party.setDescription(partyChanged.getDescription());
                        loadPartyImage(uri.toString(), ivLogo); // Header Image
                        getHostImage(partyChanged);// Host Image
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.d(TAG, "onFailure: " + "image failed");
                        // Handle any errors
                        tvPartyType.setText(partyChanged.getPartyName());
                        tvDescription.setText(partyChanged.getDescription());
                        party.setPartyName(partyChanged.getPartyName());
                        party.setDescription(partyChanged.getDescription());
                        getHostImage(partyChanged);
                    }
                });
            }
        }
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        final Party partyChanged = dataSnapshot.getValue(Party.class);
        partyChanged.setId(dataSnapshot.getKey());

        if(party != null) {
            if (party.getId().equals(partyChanged.getId())) {
                tvPartyType.setText("This party was just deleted");
                tvDescription.setText("");
                ivLogo.setImageBitmap(null);
                ivPartyHost.setImageBitmap(null);
            }
        }
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }


    @Override
    public void onClick(View view) {
        final Animation animation = AnimationUtils.loadAnimation(context, R.anim.bounce);
        TransitionDrawable like = (TransitionDrawable) ContextCompat.getDrawable(context, R.drawable.like);
        TransitionDrawable unlike = (TransitionDrawable) ContextCompat.getDrawable(context, R.drawable.unlike);

        switch (view.getId()) {
            case R.id.btnLike:
                if(party.isLiked()){
                    party.setLiked(false);
                    btnLike.setImageDrawable(unlike);
                    unlike.reverseTransition(500);
                    btnLike.startAnimation(animation);
                    btnLike.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_unlike));
                } else {
                    party.setLiked(true);
                    btnLike.setImageDrawable(like);
                    like.reverseTransition(1000);
                    btnLike.startAnimation(animation);
                }

                break;
            case R.id.btnShareParty:
                btnShareParty.startAnimation(animation);

                try {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, "Party App");
                    String sAux = "\nLet me recommend you this application\n\n";
                    sAux = sAux + "https://play.google.com/store/apps/details?id=PartyApp \n\n";
                    i.putExtra(Intent.EXTRA_TEXT, sAux);
                    startActivity(Intent.createChooser(i, "choose one"));

                } catch(Exception e) {
                    //e.toString();
                }

                break;
            case R.id.btnPublicOrPrivate:
                btnPublicOrPrivate.startAnimation(animation);
                break;
        }
    }
}