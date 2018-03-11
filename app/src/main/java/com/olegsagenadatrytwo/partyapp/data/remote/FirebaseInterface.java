package com.olegsagenadatrytwo.partyapp.data.remote;

import android.content.Context;
import android.graphics.Bitmap;

import com.olegsagenadatrytwo.partyapp.model.custompojos.Party;

public interface FirebaseInterface {
    void getAllParties(Context context);
    void saveLike(String userId, String partyId, String partyOwnerId);
    void sendFriendRequest(String currentUserId, String friendUserId);
    void acceptFriendRequest(String currentUserId, String friendUserId);
    void getMyFriends();
    void removeLike(String userId, String partyId, String partyOwnerID);
    void removeFriend(String currentUserId, String friendUserId);
    void getMyLikes();
    void getPendingFriendRequests();
    void addParty(Party party, Bitmap bitmap);
    void editParty(Party party, Bitmap bitmap);
    void updateImageURLForUser(String url);
    void getMyUsername();
    void updateUsername(String username, String oldUsername);
}
