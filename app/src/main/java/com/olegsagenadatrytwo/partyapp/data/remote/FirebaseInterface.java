package com.olegsagenadatrytwo.partyapp.data.remote;

import android.content.Context;

public interface FirebaseInterface {
    void getAllParties(Context context);
    void saveLike(String userId, String partyId, String partyOwnerId);
    void removeLike(String userId, String partyId, String partyOwnerID);
    void getMyLikes();
}
