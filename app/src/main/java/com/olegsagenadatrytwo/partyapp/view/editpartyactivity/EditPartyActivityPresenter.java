package com.olegsagenadatrytwo.partyapp.view.editpartyactivity;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.firebase.database.FirebaseDatabase;
import com.olegsagenadatrytwo.partyapp.data.remote.FirebaseHelper;
import com.olegsagenadatrytwo.partyapp.model.custompojos.Party;

/**
 * Created by omcna on 9/21/2017.
 */

public class EditPartyActivityPresenter implements EditPartyActivityContract.presenter {

    public static final String TAG = "EditPartyPres";
    private FirebaseDatabase database;
    private EditPartyActivityContract.view view;
    private Context context;

    @Override
    public void attachView(EditPartyActivityContract.view view) {
        this.view = view;
    }

    @Override
    public void removeView() {
        this.view = null;
    }

    @Override
    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void init() {
        database = FirebaseDatabase.getInstance();
    }

    @Override
    public void editParty(final Party party, Bitmap bitmap) {
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.editParty(party, bitmap);
        view.partyEdited(true);
    }
}
