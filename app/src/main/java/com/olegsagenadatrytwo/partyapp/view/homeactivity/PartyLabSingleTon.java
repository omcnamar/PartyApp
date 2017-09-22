package com.olegsagenadatrytwo.partyapp.view.homeactivity;

import android.content.Context;

import com.olegsagenadatrytwo.partyapp.model.custompojos.Party;

import java.util.ArrayList;
import java.util.List;


public class PartyLabSingleTon {

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
    List<Party> getEvents() {
        return events;
    }

    //this method will set the list of events
    void setEvents(List<Party> events) {
//        if(events.isEmpty()){
//            Log.d(TAG + " DISTANCE", "getInstance: Is Empty" );
//        } else {
//            try {
//                events = LocationUtilities.setPartyDistances(events, context);
//                for(Party p : events){
//                    Log.d(TAG + " DISTANCE", "getInstance: " + p.getDistance());
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        this.events = events;
    }
}

