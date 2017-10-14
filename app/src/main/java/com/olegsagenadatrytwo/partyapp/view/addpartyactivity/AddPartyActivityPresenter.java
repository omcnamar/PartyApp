package com.olegsagenadatrytwo.partyapp.view.addpartyactivity;

import android.graphics.Bitmap;

import com.olegsagenadatrytwo.partyapp.data.remote.FirebaseHelper;
import com.olegsagenadatrytwo.partyapp.model.custompojos.Party;

public class AddPartyActivityPresenter implements AddPartyActivityContract.presenter {

    private static final String TAG = "AdActivityPresenter";
    private AddPartyActivityContract.view view;

    @Override
    public void attachView(AddPartyActivityContract.view view) {
        this.view = view;
    }

    @Override
    public void removeView() {
        this.view = null;
    }

    @Override
    public void addNewParty(final Party party, Bitmap bitmap) {
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.addParty(party, bitmap);
        view.partySaved(true);
    }

}
