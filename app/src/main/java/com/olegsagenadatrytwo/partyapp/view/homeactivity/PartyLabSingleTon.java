package com.olegsagenadatrytwo.partyapp.view.homeactivity;

import android.content.Context;
import com.olegsagenadatrytwo.partyapp.model.eventbrite.Event;
import java.util.ArrayList;
import java.util.List;


public class PartyLabSingleTon {

    private Context context;
    private static PartyLabSingleTon partyLab;
    private static List<Event> events = new ArrayList<>();

    //private constructor
    private PartyLabSingleTon(Context context){
        this.context = context;
    }

    //this method will return an instance of PartyLabSingleton
    static PartyLabSingleTon getInstance(Context context){
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
    List<Event> getEvents() {
        return events;
    }

    //this method will set the list of events
    void setEvents(List<Event> events) {
        this.events = events;
    }
}

