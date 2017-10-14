package com.olegsagenadatrytwo.partyapp.data.remote;

import android.content.Context;
import android.graphics.Bitmap;

import com.olegsagenadatrytwo.partyapp.model.custompojos.Party;

public interface FirebaseInterface {
    void getAllParties(Context context);
    void saveLike(String userId, String partyId, String partyOwnerId);
    void removeLike(String userId, String partyId, String partyOwnerID);
    void getMyLikes();
    void addParty(Party party, Bitmap bitmap);
    void editParty(Party party, Bitmap bitmap);
}
