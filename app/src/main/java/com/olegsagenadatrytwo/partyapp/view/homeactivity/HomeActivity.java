package com.olegsagenadatrytwo.partyapp.view.homeactivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.olegsagenadatrytwo.partyapp.R;
import com.olegsagenadatrytwo.partyapp.inject.view.home_activity.DaggerHomeActivityComponent;
import com.olegsagenadatrytwo.partyapp.inject.view.home_activity.HomeActivityComponent;
import com.olegsagenadatrytwo.partyapp.model.custompojos.Party;
import com.olegsagenadatrytwo.partyapp.utilities.location.LocationUtilities;
import com.olegsagenadatrytwo.partyapp.utils.DepthPageTransformer;
import com.olegsagenadatrytwo.partyapp.view.loginactivity.LoginActivity;
import com.olegsagenadatrytwo.partyapp.view.profileactivity.ProfileActivity;
import com.olegsagenadatrytwo.partyapp.view.map_fragment.MapsActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.olegsagenadatrytwo.partyapp.Constant.MY_PERMISSIONS_REQUEST_READ_LOCATION;

public class HomeActivity extends AppCompatActivity implements HomeActivityContract.view {

    private static final String TAG = "HomeActivity";

    @BindView(R.id.party_view_pager)
    ViewPager viewPager;


    @Inject
    HomeActivityPresenter presenter;

    private static final String PARTY_ID = "party_id";
    @BindView(R.id.pbLoading)
    ProgressBar pbLoading;
    @BindView(R.id.flMap)
    FrameLayout flMap;
    @BindView(R.id.ivMapBackgroundFrame)
    ImageView ivMapBackgroundFrame;
    ArrayList<Party> partiesList;
    Window window;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        runtimePermission();
        ButterKnife.bind(this);
        DaggerHomeActivityComponent.create().inject(this);
        presenter.attachView(this);
        presenter.setContext(this);
        presenter.rxJavaEventbrite();

        // Check if we're running on Android 5.0 or higher
        if (Build.VERSION.SDK_INT >= 21) {
            // Call some material design APIs here
            updateStatusBar();
        } else {
            // Implement this feature without material design
        }
        updateMapSnapshot();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void updateStatusBar() {
        window = this.getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorBlack));
    }

    private void updateMapSnapshot() {
        // TODO: 9/21/2017 Everytime you go to the mapview
        // TODO: a snapshot is taken before you exit and that will be the new background
        // TODO: 9/21/2017 if user has no saved snapshot load default else load snapshot
        ivMapBackgroundFrame.setImageResource(R.drawable.default_map_background);
        ivMapBackgroundFrame.setAlpha(0.2f);
    }

    @Override
    protected void onDestroy() {
        presenter.removeView();
        super.onDestroy();
    }

    @Override
    public void eventsLoadedUpdateUI(final List<Party> parties) {
        partiesList = (ArrayList<Party>)parties;
        final FragmentStatePagerAdapter adapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            //each view of the view pager is an Instance of the PartyFragment

            @Override
            public Fragment getItem(int position) {
                //get the Event from the list of the events
                Party party = parties.get(position);
                //return an instance of the PartyFragment which is initialized with with the id of the event
                return PartyFragment.newInstance(party.getId());
            }

            @Override
            public int getCount() {
                return parties.size();
            }
        };
        pbLoading.setVisibility(View.GONE);
        viewPager.setPageTransformer(true, new DepthPageTransformer());
        //set Adapter for ViewPager
        viewPager.setAdapter(adapter);

        //add the listener to change the list when its changed
        DatabaseReference partiesReference = FirebaseDatabase.getInstance().getReference("parties");
        partiesReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildAdded: ");
                final Party party = dataSnapshot.getValue(Party.class);
                party.setId(dataSnapshot.getKey());

                Log.d("ggg", "onSuccess: Home: " +  "on child added before image: ");
                //get reference to storage
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReferenceFromUrl("gs://partyapp-fc6fb.appspot.com/");

                //download image
                storageRef.child("images/" + party.getId() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("ggg", "onSuccess: " +  "on child added image success: ");
                        party.setImageURL(uri.toString());
                        PartyLabSingleTon partyLabSingleTon = PartyLabSingleTon.getInstance(getApplicationContext());
                        List<Party> parties = partyLabSingleTon.getEvents();
                        parties.add(party);
                        partyLabSingleTon.setEvents(parties);
                        adapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                        PartyLabSingleTon partyLabSingleTon = PartyLabSingleTon.getInstance(getApplicationContext());
                        List<Party> parties = partyLabSingleTon.getEvents();
                        parties.add(party);
                        partyLabSingleTon.setEvents(parties);
                        adapter.notifyDataSetChanged();
                    }
                });

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                final Party party = dataSnapshot.getValue(Party.class);
                party.setId(dataSnapshot.getKey());

                //get reference to storage
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReferenceFromUrl("gs://partyapp-fc6fb.appspot.com/");

                Log.d("ggg", "onSuccess: Home: " +  "on child changed before image: ");
                //download image
                storageRef.child("images/" + party.getId() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        party.setImageURL(uri.toString());
                        PartyLabSingleTon partyLabSingleTon = PartyLabSingleTon.getInstance(getApplicationContext());
                        List<Party> parties = partyLabSingleTon.getEvents();
                        int i = parties.indexOf(party);
                        parties.set(i, party);
                        partyLabSingleTon.setEvents(parties);
                        adapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                        PartyLabSingleTon partyLabSingleTon = PartyLabSingleTon.getInstance(getApplicationContext());
                        List<Party> parties = partyLabSingleTon.getEvents();
                        int i = parties.indexOf(party);
                        parties.set(i, party);
                        partyLabSingleTon.setEvents(parties);
                        adapter.notifyDataSetChanged();
                    }
                });
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                final Party party = dataSnapshot.getValue(Party.class);
                party.setId(dataSnapshot.getKey());
                PartyLabSingleTon partyLabSingleTon = PartyLabSingleTon.getInstance(getApplicationContext());
                List<Party> parties = partyLabSingleTon.getEvents();
                int i = parties.indexOf(party);
                parties.remove(i);
                partyLabSingleTon.setEvents(parties);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildMoved: ");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: ");
            }
        });
    }

    @OnClick({R.id.action_map, R.id.action_location, R.id.action_profile})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.action_map:
//                //if there is no current user send the user to log in
//                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//                    Intent addPartyIntent = new Intent(this, AddPartyActivity.class);
//                    startActivity(addPartyIntent);
//                } else {
//                    Intent logInIntent = new Intent(this, LoginActivity.class);
//                    startActivity(logInIntent);
//                }
                // TODO: 9/17/17 implement the Map View
                Intent intent = new Intent(this, MapsActivity.class);
                intent.putParcelableArrayListExtra("parties", partiesList);
                startActivity(intent);

                break;
            case R.id.action_location:
                Toast.makeText(this, "Location", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_profile:
//                Intent loginIntent = new Intent(this, LoginActivity.class);
//                startActivity(loginIntent);
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    /*startActivity(new Intent(this, MyPartiesActivity.class));*/
                    startActivity(new Intent(this, ProfileActivity.class));
                } else {
                    Intent logInIntent = new Intent(this, LoginActivity.class);
                    startActivity(logInIntent);
                }
                // TODO: 9/17/17 need to implement back button for profile class
                break;

        }
    }

    public void goToLocation(View view) {
    }

    //                       //
    //  Runtime Permission   //
    //=======================//
    public void runtimePermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

    }

    //                       //
    //  Permission Result    //
    //=======================//
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                } else {


                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
