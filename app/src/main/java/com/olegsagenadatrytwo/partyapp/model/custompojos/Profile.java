package com.olegsagenadatrytwo.partyapp.model.custompojos;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by omcna on 9/18/2017.
 */

public class Profile {
    String id;
    String email;
    String username;
    List<Party> parties;
    List<String> likesPartyID;

    public Profile() {
        parties = new ArrayList<>();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
