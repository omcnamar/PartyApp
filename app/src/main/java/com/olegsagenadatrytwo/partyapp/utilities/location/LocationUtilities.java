package com.olegsagenadatrytwo.partyapp.utilities.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.olegsagenadatrytwo.partyapp.model.custom_map.CustomLocationObject;
import com.olegsagenadatrytwo.partyapp.model.custompojos.Party;
import com.olegsagenadatrytwo.partyapp.model.geocoding_profile.GeocodingProfile;
import com.olegsagenadatrytwo.partyapp.utilities.ConvertionUtilities.ConvertionUtilities;
import com.olegsagenadatrytwo.partyapp.utilities.json.JsonUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import okhttp3.HttpUrl;

import static com.olegsagenadatrytwo.partyapp.Constant.GOOGLE_GEO_API_KEY;
import static com.olegsagenadatrytwo.partyapp.Constant.RADIUS_OF_EARTH;


/**
 * Created by aaron on 9/16/17.
 */

public class LocationUtilities {
    static int apiFailureDistanceFailSafe = 1;
    //                                                    //
    //   setGeograchicalLocation                          //
    //      Receives a location which info is used to set //
    //          geographical data in the custom object    //
    //****************************************************//
    public static CustomLocationObject setGeographicalLocation(Location location) {
        CustomLocationObject returnCustomLocation = new CustomLocationObject();
        returnCustomLocation.setLatitude(location.getLatitude());
        returnCustomLocation.setLongitude(location.getLongitude());
        returnCustomLocation.setLatitude_longitude(new LatLng(location.getLatitude(), location.getLongitude()));
        return returnCustomLocation;
    }
    static String locationProvider;
    public static final CustomLocationObject getPhysicalDeviceLocation(Context context) {
        LocationManager locationManager;


        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationProvider = locationManager.getBestProvider(new Criteria(), false);
        if (ActivityCompat.checkSelfPermission(context , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        Location physicalDeviceLocation = locationManager.getLastKnownLocation(locationProvider);
        return setGeographicalLocation(physicalDeviceLocation);
     }

     public static final String getDistanceFromDeviceLocation(Party passedParty, Context context ) throws IOException {
         CustomLocationObject currentDeviceLocObject = getPhysicalDeviceLocation(context);
         Address passedPartyAddress = parseStringToAddress(passedParty.getAddress());
         CustomLocationObject passedPartyLocation = getLatitudeLongitudeOfAddress(new CustomLocationObject(), passedPartyAddress, locationProvider);
         return getDistanceAsTheCrowFlies(currentDeviceLocObject,passedPartyLocation);
     }

     public static List<Party> setPartyDistances(List<Party> passedPartyList, Context context) throws IOException {
         int error = 0;
         for(Party beingEvalParty : passedPartyList){
            if(beingEvalParty.getAddress() != null) {
                beingEvalParty.setDistance(getDistanceFromDeviceLocation(beingEvalParty, context));
            } else {
                beingEvalParty.setDistance("100,000,00" + error);
                error = error + 2;
            }
         }
         Collections.sort(passedPartyList, new Comparator<Party>() {
             @Override
             public int compare(Party party1, Party party2)
             {
                 if(party1.getDistance() != null && party2.getDistance() != null) {
                     Double d1 = Double.parseDouble(party1.getDistance().replace(",", "").replaceAll("[^\\d.]", ""));
                     Double d2 = Double.parseDouble(party2.getDistance().replace(",", "").replaceAll("[^\\d.]", ""));
                     return d1.compareTo(d2);
                 }
                 return 0;
             }
         });

         return passedPartyList;
     }






        //                                                    //
       //   getDrivingDistance                               //
      //      Returns the Driving by road distance between  //
     //          2 locations                               //
    //****************************************************//
     private static String returnDistance;
     public static String getDrivingDistance(final CustomLocationObject currentLocation, final CustomLocationObject requestedLocation){
         Thread getDistanceThread = new Thread(new Runnable() {
             @Override
             public void run() {
                 try {
                     String response;
                     URL url = new URL("http://maps.googleapis.com/maps/api/directions/json?origin="
                             + currentLocation.getLatitude()
                             + "," + currentLocation.getLongitude()
                             + "&destination=" + requestedLocation.getLatitude()
                             + "," + requestedLocation.getLongitude()
                             + "&sensor=false&units=metric&mode=driving");
                     response = JsonUtilities.getJsonResponse(url);

                     JSONObject jsonObject = new JSONObject(response);
                     JSONArray array = jsonObject.getJSONArray("routes");
                     JSONObject routes = array.getJSONObject(0);
                     JSONArray legs = routes.getJSONArray("legs");
                     JSONObject steps = legs.getJSONObject(0);
                     JSONObject distance = steps.getJSONObject("distance");
                     returnDistance=distance.getString("text");
                     returnDistance = returnDistance.replace("km","");
                     returnDistance = returnDistance.replace(" ","");


                 } catch (ProtocolException e) {
                     e.printStackTrace();
                } catch (MalformedURLException e) {
                 e.printStackTrace();
                } catch (IOException e) {
                     e.printStackTrace();
                } catch (JSONException e) {
                     e.printStackTrace();
                 }
             }
         });
         getDistanceThread.start();
         try {
             getDistanceThread.join();
         } catch (InterruptedException e) {
             e.printStackTrace();
         }
         String distance = returnDistance;
         returnDistance = null;
         return distance;

     }
        //                                                    //
       //   getDistanceAsTheCrowFlies                        //
      //      Returns the Straight line distance between    //
     //          2 locations in KM                         //
    //****************************************************//
     public static String getDistanceAsTheCrowFlies(CustomLocationObject currentLocation, CustomLocationObject requestedLocation){
         if(requestedLocation.getLatitude() != null) {
             double lat1 = currentLocation.getLatitude();
             double lat2 = requestedLocation.getLatitude();
             double lng1 = currentLocation.getLongitude();
             double lng2 = requestedLocation.getLongitude();
             double earthRadius = 6371000; //meters
             double dLat = Math.toRadians(lat2 - lat1);
             double dLng = Math.toRadians(lng2 - lng1);
             double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                     Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                             Math.sin(dLng / 2) * Math.sin(dLng / 2);
             double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
             float dist = (float) (earthRadius * c);

             return String.format("%.2f", ConvertionUtilities.convertMetersToMiles(dist));
         } else {
             return String.valueOf(apiFailureDistanceFailSafe++);
         }
     }



        //                                                    //
       //   getLatitudeLongitudeOfAddress                    //
      //      Sets the custom Location Object latitude and  //
     //          longitude based off of a address          //
    //****************************************************//
    private static GeocodingProfile returnedGeoProfile;
    public static CustomLocationObject getLatitudeLongitudeOfAddress(CustomLocationObject passedLocationObject, Address passedAddress, String provider) throws IOException {
        String address = passedAddress.getAddressLine(1) + " " + passedAddress.getLocality() + " " + passedAddress.getPostalCode();
        final HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("maps.googleapis.com")
                .addPathSegment("maps")
                .addPathSegment("api")
                .addPathSegment("geocode")
                .addPathSegment("json")
                .addQueryParameter("address", address)
                .addQueryParameter("key", GOOGLE_GEO_API_KEY)
                .build();
        Log.d("TAG", "getLatitudeLongitudeOfAddress: " + url.toString());
        Thread latLngThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String jsonResponse = JsonUtilities.getJsonResponse(new URL(url.toString()));
                    Gson gson = new Gson();
                    returnedGeoProfile = gson.fromJson(jsonResponse, GeocodingProfile.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        latLngThread.start();
        try {
            latLngThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Location location = new Location(provider);
        if(returnedGeoProfile.getResults().size() > 0) {
            location.setLatitude(returnedGeoProfile.getResults().get(0).getGeometry().getLocation().getLat());
            location.setLongitude(returnedGeoProfile.getResults().get(0).getGeometry().getLocation().getLng());
            passedLocationObject.setLocation(location);
            passedLocationObject = setGeographicalLocation(passedLocationObject.getLocation());
        }
        return passedLocationObject;
    }
        //                                                    //
       //   setupAddress                                     //
      //      Setup a Address object                        //
     //                                                    //
    //****************************************************//
    public static Address setupAddress(String street, String city, String state, String zipCode){
        Address address = new Address(Locale.US);
        address.setAddressLine(1,street);
        address.setLocality(city + ", " + state);
        address.setPostalCode(zipCode);
        return address;
    }


    public static CustomLocationObject getGeographicalLocationBasedOffZip(String zipCode, String passedProvider){
        CustomLocationObject returnedLocation = new CustomLocationObject();
        if(zipCode.length() != 5){
            Log.d("TAG", "getGeographicalLocationBasedOffZip: Zip Code Length not valid");
        } else if(!(zipCode.matches("-?\\d+(\\.\\d+)?"))) {
            Log.d("TAG", "getGeographicalLocationBasedOffZip: Zip Code Is not a number");
        } else {
            final HttpUrl url = new HttpUrl.Builder()
                    .scheme("https")
                    .host("maps.googleapis.com")
                    .addPathSegment("maps")
                    .addPathSegment("api")
                    .addPathSegment("geocode")
                    .addPathSegment("json")
                    .addQueryParameter("address", zipCode)
                    .addQueryParameter("key", GOOGLE_GEO_API_KEY)
                    .build();
            Log.d("TAG", "getGeographicalLocationBasedOffZip: url = " + url.toString());
            Thread latLngThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String jsonResponse = JsonUtilities.getJsonResponse(new URL(url.toString()));
                        Gson gson = new Gson();
                        returnedGeoProfile = gson.fromJson(jsonResponse, GeocodingProfile.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            latLngThread.start();
            try {
                latLngThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Location location = new Location(passedProvider);
            if(returnedGeoProfile.getResults().size() > 0) {
                Log.d("TAG", "getGeographicalLocationBasedOffZip: " + returnedGeoProfile.getResults().get(0).getGeometry().getLocation().getLat());
                location.setLatitude(returnedGeoProfile.getResults().get(0).getGeometry().getLocation().getLat());
                location.setLongitude(returnedGeoProfile.getResults().get(0).getGeometry().getLocation().getLng());
                returnedLocation.setLocation(location);
                returnedLocation = setGeographicalLocation(returnedLocation.getLocation());
                Log.d("TAG", "getGeographicalLocationBasedOffZip: " + returnedLocation.getLatitude_longitude().toString());
            }

        }
        return returnedLocation;
    }

    public static Address parseStringToAddress(String passedString){
        Address address = new Address(Locale.US);
        address.setAddressLine(1,passedString);
        address.setLocality("");
        address.setPostalCode("");
        return address;

    }


}





