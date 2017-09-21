package com.olegsagenadatrytwo.partyapp.view.editpartyactivity;

import android.content.Context;
import android.graphics.Bitmap;

import com.olegsagenadatrytwo.partyapp.BasePresenter;
import com.olegsagenadatrytwo.partyapp.BaseView;
import com.olegsagenadatrytwo.partyapp.model.custompojos.Party;

/**
 * Created by omcna on 9/21/2017.
 */

public interface EditPartyActivityContract {

    interface view extends BaseView {
        void partyEdited(Boolean saved);
    }

    interface presenter extends BasePresenter<view> {
        void editParty(Party party, Bitmap bitmap);
        void setContext(Context context);
        void init();
    }
}
