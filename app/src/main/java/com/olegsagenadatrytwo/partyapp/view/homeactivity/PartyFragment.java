package com.olegsagenadatrytwo.partyapp.view.homeactivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.olegsagenadatrytwo.partyapp.R;
import com.olegsagenadatrytwo.partyapp.customviews.AutoResizeTextView;
import com.olegsagenadatrytwo.partyapp.model.eventbrite.Event;
import java.util.List;

public class PartyFragment extends Fragment {

    private static final String PARTY_ID = "party_id";
    private List<Event> events;
    private Event event;
    private AutoResizeTextView tvPartyType;
    private ImageView ivLogo;

    public static PartyFragment newInstance(String id){
        Bundle args = new Bundle();
        args.putSerializable(PARTY_ID, id);
        PartyFragment fragment = new PartyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get the id of the event that were are trying to display
        String id = String.valueOf(getArguments().getSerializable(PARTY_ID));
        //get the singleTon that holds the list of the events
        PartyLabSingleTon partySingleton = PartyLabSingleTon.getInstance(getActivity());
        //get the list of the events from the singleton
        events = partySingleton.getEvents();
        //find the specific event based on the id
        event = getEvent(id);
        setHasOptionsMenu(true);
    }

    //this method will return the Event from the list of events based on id of the event passed
    public Event getEvent(String id){
        for(int i = 0; i< events.size(); i++){
            if(events.get(i).getId().equals(id)){
                return events.get(i);
            }
        }
        return null;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.party_card_item2, container, false);
        tvPartyType = v.findViewById(R.id.tvPartyType);
        ivLogo = v.findViewById(R.id.ivPartyHeader);

        //if the event is not null than set the ImageViews and TextViews according to the Event
        if(event != null) {
            tvPartyType.setText(event.getName().getText());
            Glide.with(getActivity()).load(event.getLogo().getUrl()).into(ivLogo);
        }
        return v;
    }

}