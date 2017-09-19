package com.olegsagenadatrytwo.partyapp.view.addpartyactivity;

import android.content.Context;

import com.olegsagenadatrytwo.partyapp.BasePresenter;
import com.olegsagenadatrytwo.partyapp.BaseView;
import com.olegsagenadatrytwo.partyapp.model.custompojos.Party;
import com.olegsagenadatrytwo.partyapp.model.eventbrite.EventbriteEvents;
import com.olegsagenadatrytwo.partyapp.view.homeactivity.HomeActivityContract;

/**
 * Created by omcna on 9/17/2017.
 */

public interface AddPartyActivityContract {

    interface view extends BaseView {
        void partySaved(Boolean saved);
    }

    interface presenter extends BasePresenter<AddPartyActivityContract.view> {
        void addNewParty(Party party);
        void setContext(Context context);
        void init();
    }
}
