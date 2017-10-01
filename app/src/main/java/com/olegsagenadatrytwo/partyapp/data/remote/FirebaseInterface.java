package com.olegsagenadatrytwo.partyapp.data.remote;

import android.content.Context;

import com.olegsagenadatrytwo.partyapp.model.custompojos.Party;

import java.util.List;

public interface FirebaseInterface {
    void getAllParties(Context context);
    void saveLike(Party party, List<String> likes);
    void getMyLikes();
}
