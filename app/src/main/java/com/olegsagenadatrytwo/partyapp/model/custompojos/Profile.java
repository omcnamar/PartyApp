package com.olegsagenadatrytwo.partyapp.model.custompojos;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by omcna on 9/18/2017.
 */

public class Profile {
    String id;
    String userName;
    List<Party> parties;
    List<String> likesPartyID;

    public Profile() {
        parties = new ArrayList<>();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<Party> getParties() {
        return parties;
    }

    public void setParties(List<Party> parties) {
        this.parties = parties;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getLikesPartyID() {
        return likesPartyID;
    }

    public void setLikesPartyID(List<String> likesPartyID) {
        this.likesPartyID = likesPartyID;
    }
}
