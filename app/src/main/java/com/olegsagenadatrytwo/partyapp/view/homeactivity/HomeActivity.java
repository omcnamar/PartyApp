package com.olegsagenadatrytwo.partyapp.view.homeactivity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.olegsagenadatrytwo.partyapp.App;
import com.olegsagenadatrytwo.partyapp.Constant;
import com.olegsagenadatrytwo.partyapp.R;
import com.olegsagenadatrytwo.partyapp.eventbus.Caller;
import com.olegsagenadatrytwo.partyapp.eventbus.LocalEvent;
import com.olegsagenadatrytwo.partyapp.inject.view.shared_preference.MySharedPreferences;
import com.olegsagenadatrytwo.partyapp.model.custompojos.Party;
import com.olegsagenadatrytwo.partyapp.utilities.viewpager_utils.DepthPageTransformer;
import com.olegsagenadatrytwo.partyapp.view.loginactivity.LoginActivity;
import com.olegsagenadatrytwo.partyapp.view.map_fragment.MapsActivity;
import com.olegsagenadatrytwo.partyapp.view.profileactivity.ProfileActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.olegsagenadatrytwo.partyapp.Constant.REQUEST_PERMISSION;

public class HomeActivity extends AppCompatActivity implements HomeActivityContract.view, LocationListener {

    private static final String TAG = "HomeActivity";
    private static final String PARTY_ID = "party_id";
    @BindView(R.id.party_view_pager)
    ViewPager viewPager;
    @Inject
    HomeActivityPresenter presenter;
    @Inject
    MySharedPreferences preferences;
    @BindView(R.id.pbLoading)
    ProgressBar pbLoading;
    @BindView(R.id.flMap)
    FrameLayout flMap;
    @BindView(R.id.ivMapBackgroundFrame)
    ImageView ivMapBackgroundFrame;
    @BindView(R.id.toolbar)
    LinearLayout toolbar;
    @BindView(R.id.action_location)
    TextView actionLocation;
    ArrayList<Party> partiesList;
    Window window;

    private FragmentStatePagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        ((App) getApplicationContext()).getHomeActivityComponent().inject(this);
        createViewPagerAdapter();
        presenter.attachView(this);
        presenter.setContext(this);
        // Check if we're running on Android 5.0 or higher
        if (Build.VERSION.SDK_INT >= 21) {
            // Call some material design APIs here
            updateStatusBar();
        } else {
            // Implement this feature without material design
        }

        checkPermissions();
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
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        presenter.removeView();
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Caller caller) {
        pbLoading.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
    }

    @Override
    public String getCurrentLocation() {
        return preferences.getStringData(Constant.CURRENT_LOCATION);
    }

    private void createViewPagerAdapter() {
        Log.d(TAG, "createViewPagerAdapter: ");
        adapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            //each view of the view pager is an Instance of the PartyFragment
            @Override
            public Fragment getItem(int position) {
                //get the Event from the list of the events
                Party party = PartyLabSingleTon.getInstance(getApplicationContext()).getEvents().get(position);
                //return an instance of the PartyFragment which is initialized with with the id of the event
                return PartyFragment.newInstance(party.getId());
            }

            @Override
            public int getCount() {
                return PartyLabSingleTon.getInstance(getApplicationContext()).getEvents().size();
            }
        };
        viewPager.setPageTransformer(true, new DepthPageTransformer());
        //set Adapter for ViewPager
        viewPager.setAdapter(adapter);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(LocalEvent locale) {
        if (locale.getLocale() != null) {
            actionLocation.setText(locale.getLocale());
            preferences.putStringData(Constant.ZIP, locale.getLocale());
        } else {
            Snackbar sb = Snackbar
                    .make(findViewById(R.id.main_content), "Invalid Zipcode", Snackbar.LENGTH_INDEFINITE)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getZip();
                        }
                    });
            sb.show();
        }
    }

    @OnClick({R.id.action_map, R.id.action_location, R.id.action_profile})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.action_map:

                Intent intent = new Intent(this, MapsActivity.class);
                intent.putParcelableArrayListExtra("parties", partiesList);
                startActivity(intent);

                break;
            case R.id.action_location:

                getZip();

                break;
            case R.id.action_profile:

                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    startActivity(new Intent(this, ProfileActivity.class));
                } else {
                    startActivity(new Intent(this, LoginActivity.class));
                }

                break;

        }
    }

    private void getZip() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.new_zipcode);
        dialog.setTitle("Enter New Zip code");

        Button btnSaveZip = dialog.findViewById(R.id.btnSaveZip);
        btnSaveZip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText etZip = dialog.findViewById(R.id.etZipcode);
                presenter.getLocaleRetrofit(etZip.getText().toString());
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void goToLocation(View view) {
    }

    //                       //
    //  Runtime Permission   //
    //=======================//
    public void checkPermissions() {
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
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_PERMISSION);
            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_PERMISSION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            saveUpdatedCurrentLocation();
            loadLocation();
            presenter.getPartiesFromFireBase();
        }

    }

    //                       //
    //  Permission Result    //
    //=======================//
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    saveUpdatedCurrentLocation();
                    loadLocation();
                    presenter.getPartiesFromFireBase();
                }

            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void saveUpdatedCurrentLocation() {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                String latlng = location.getLatitude() + "," + location.getLongitude();
                preferences.putStringData(Constant.CURRENT_LOCATION, latlng);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void loadLocation() {
        if (preferences.getStringData(Constant.ZIP) != null) {
            actionLocation.setText(preferences.getStringData(Constant.ZIP));
        } else {
            FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    String latlng = location.getLatitude() + "," + location.getLongitude();
                    presenter.getCurrentLocale(latlng);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    getZip();
                }
            });
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
