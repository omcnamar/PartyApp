package com.olegsagenadatrytwo.partyapp.view.homeactivity;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.olegsagenadatrytwo.partyapp.model.custompojos.Party;
import com.olegsagenadatrytwo.partyapp.utilities.ConvertionUtilities.ConvertionUtilities;

import java.util.ArrayList;
import java.util.List;


public class PartyLabSingleTon {

    private static final String TAG = "PartyST";
    private Context context;
    private static PartyLabSingleTon partyLab;
    private static List<Party> events = new ArrayList<>();

    //private constructor
    private PartyLabSingleTon(Context context){
        this.context = context;
    }

    //this method will return an instance of PartyLabSingleton
    public static PartyLabSingleTon getInstance(Context context){
        //if partyLab is null than we create a new Instance and return it
        if(partyLab == null){
            return new PartyLabSingleTon(context);
        }
        //if partyLab is not null than we return the existing instance not new Instance
        else{

            return partyLab;
        }
    }

    //this method will return the list of events
    public List<Party> getEvents() {
        return events;
    }

    //this method will set the list of events
    void setEvents(List<Party> events) {
        this.events = events;
    }

    public String getDistance(Party p, String currentLatLng) {
        String[] myLocation = currentLatLng.split(",");
        double startLatitude = Double.valueOf(myLocation[0]);
        double startLongitude = Double.valueOf(myLocation[0]);

        String[] partylocation = p.getLatlng().split(",");
        double endLatitude = Double.valueOf(partylocation[0]);
        double endLongitude = Double.valueOf(partylocation[1]);

        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(endLatitude - startLatitude);
        double dLng = Math.toRadians(endLongitude - startLongitude);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(startLatitude)) * Math.cos(Math.toRadians(endLatitude)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        float dist = (float) (earthRadius * c);

        float[] results = new float[1];
        Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, results);
        Log.d(TAG, "getDistance: " + String.format("%.2f", ConvertionUtilities.convertMetersToMiles(dist))
        + "---" + ConvertionUtilities.convertMetersToMiles(results[0]));

        return String.format("%.2f", ConvertionUtilities.convertMetersToMiles(dist));
    }

}

