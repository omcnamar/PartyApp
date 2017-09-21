package com.olegsagenadatrytwo.partyapp.view.map_fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.olegsagenadatrytwo.partyapp.R;
import com.olegsagenadatrytwo.partyapp.model.custom_map.CustomLocationObject;
import com.olegsagenadatrytwo.partyapp.model.custompojos.Party;
import com.olegsagenadatrytwo.partyapp.utilities.ConvertionUtilities.ConvertionUtilities;
import com.olegsagenadatrytwo.partyapp.utilities.location.LocationUtilities;
import com.olegsagenadatrytwo.partyapp.utilities.map.MapUtilities;

import java.io.IOException;
import java.util.ArrayList;

import static com.olegsagenadatrytwo.partyapp.Constant.COLOR_BLUE;
import static com.olegsagenadatrytwo.partyapp.Constant.LOCATIOM_UPDATE_MILES;
import static com.olegsagenadatrytwo.partyapp.Constant.LOCATION_UPDATE_MINUTES;
import static com.olegsagenadatrytwo.partyapp.Constant.TAG_LOCATION_INFO;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    String locationProvider;
    CustomLocationObject userCustomLocation;
    DisplayMetrics displaymetrics;
    SeekBar sbMilesViewed;
    EditText etZipCode;
    double milage = 1;
    boolean keyboardHidden = false;
    ArrayList<Party>  partyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent intent = getIntent();
        partyList = intent.getParcelableArrayListExtra("parties");

        userCustomLocation = new CustomLocationObject();
        displaymetrics = new DisplayMetrics();
        etZipCode = (EditText)findViewById(R.id.etZipCode);
        setEditTextFocus(keyboardHidden);
        etZipCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (v == etZipCode) {
                    if (hasFocus) {
                        // Open keyboard
                        ((InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(etZipCode, InputMethodManager.SHOW_FORCED);
                        setEditTextFocus(true);
                    } else {
                        // Close keyboard
                        ((InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(etZipCode.getWindowToken(), 0);
                    }
                }
            }
        });
        etZipCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEditTextFocus(!keyboardHidden);
            }
        });

        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);



        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationProvider = locationManager.getBestProvider(new Criteria(), false);
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
        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
        Log.d(TAG_LOCATION_INFO, "onCreate: " + lastKnownLocation.toString());
        if(etZipCode.getText().toString().isEmpty()) {
            userCustomLocation = LocationUtilities.setGeographicalLocation(lastKnownLocation);
        }
        sbMilesViewed = (SeekBar)findViewById(R.id.sbSetDistance);

        sbMilesViewed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            double progressChange = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progressChange = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                milage = progressChange;
                onMapReady(mMap);
                Toast.makeText(MapsActivity.this, "" + milage*10 + " Miles out", Toast.LENGTH_LONG).show();

            }
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        setEditTextFocus(false);
        mMap = googleMap;
        mMap.clear();
        //display current location marker
        CustomLocationObject currentLocation = userCustomLocation;
        MarkerOptions markerOptions = MapUtilities.initializeNewMarker(new MarkerOptions(), "Some Place", currentLocation, COLOR_BLUE);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.you_are_here);
        markerOptions = MapUtilities.setImageAsMarker(markerOptions,getApplicationContext() ,null, bitmap);
        mMap.addMarker(markerOptions);

        //Add ArrayList of Addresses
        ArrayList<Address> addressArrayList = new ArrayList<>();
        final ArrayList<CustomLocationObject> customList = new ArrayList<>();
        addressArrayList = initTestValues(addressArrayList);
        for(Address addr : addressArrayList){
            CustomLocationObject addrLocation ;
            MarkerOptions markerAddrLocation ;
            try {
                addrLocation = LocationUtilities.getLatitudeLongitudeOfAddress(new CustomLocationObject(), addr, locationProvider);
                addrLocation.setId(addrLocation.getId() + customList.size());

                customList.add(addrLocation);
                markerAddrLocation = MapUtilities.initializeNewMarker(new MarkerOptions(), addrLocation.getId(), addrLocation, COLOR_BLUE);
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.party);
                markerAddrLocation = MapUtilities.setImageAsMarker(markerAddrLocation,getApplicationContext() ,null, bitmap);

                //Test distance values
                //String driveDistance = LocationUtilities.getDrivingDistance(currentLocation, addrLocation);
                //String crowDistance = LocationUtilities.getDistanceAsTheCrowFlies(currentLocation, addrLocation);
                //Log.d(TAG_DISTANCE_RETURNS, "onMapReady: Distance:  Driving Distance = " + driveDistance + "|||   As crow flies = " + crowDistance);

                mMap.addMarker(markerAddrLocation);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                for(CustomLocationObject clo : customList){
                    if(marker.getTitle().equalsIgnoreCase(clo.getId())){
                        Toast.makeText(MapsActivity.this, clo.getId(), Toast.LENGTH_SHORT).show();

                    }

                }


                return false;
            }
        });
        mMap.animateCamera(MapUtilities.mapDisplayToRequestedDistance(userCustomLocation, ConvertionUtilities.convertMilesToMeters(milage * 10),displaymetrics));

    }

    public void onLocationChanged(Location location) {
        userCustomLocation = LocationUtilities.setGeographicalLocation(location);
        onMapReady(mMap);


    }

    @Override
    protected void onResume() {
        super.onResume();
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
        locationManager.requestLocationUpdates(locationProvider,
                ConvertionUtilities.convertMinutesToMillisec(LOCATION_UPDATE_MINUTES),
                (int)ConvertionUtilities.convertMilesToMeters(LOCATIOM_UPDATE_MILES), this);

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

    public void getLocation(View view) {
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
        Location location = locationManager.getLastKnownLocation(locationProvider);
        onLocationChanged(location);


    }

    public void changeCurrentLocationToRequestedZip(View view) {
        CustomLocationObject clo = LocationUtilities.getGeographicalLocationBasedOffZip(
                etZipCode.getText().toString(), locationProvider);
        userCustomLocation = clo;
        onMapReady(mMap);


    }

    public ArrayList<Address> initTestValues(ArrayList<Address> passedList){
        for(Party party : partyList){
            Log.d("TAG", "initTestValues: " + party.getAddress());
            //Address addr = LocationUtilities.parseStringToAddress(party.getAddress());
            //passedList.add(addr);
        }
        passedList.add(LocationUtilities.setupAddress("2909 Austell Rd SW #100", "Marietta", "GA", "30008"));
        passedList.add(LocationUtilities.setupAddress("2215 D and B Dr SE", "Marietta", "GA", "30008"));
        return passedList;
    }

    public void setEditTextFocus(boolean isFocused) {
        etZipCode.setCursorVisible(isFocused);
        etZipCode.setFocusable(isFocused);
        etZipCode.setFocusableInTouchMode(isFocused);

        if (isFocused) {
            etZipCode.requestFocus();
        }
    }
}
