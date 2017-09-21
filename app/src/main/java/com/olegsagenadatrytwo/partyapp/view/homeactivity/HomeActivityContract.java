package com.olegsagenadatrytwo.partyapp.view.homeactivity;

import com.olegsagenadatrytwo.partyapp.BasePresenter;
import com.olegsagenadatrytwo.partyapp.BaseView;
import com.olegsagenadatrytwo.partyapp.model.custompojos.Party;

import java.util.List;

/**
 * Created by Admin on 9/13/2017.
 */

public interface HomeActivityContract {

    interface view extends BaseView {
        void eventsLoadedUpdateUI(List<Party> parties);
    }

    interface presenter extends BasePresenter<view> {

        void rxJavaEventbrite();
    }
}
