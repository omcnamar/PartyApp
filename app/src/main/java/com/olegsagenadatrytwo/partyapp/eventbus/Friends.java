package com.olegsagenadatrytwo.partyapp.eventbus;

import java.util.List;


public class UsersWhoRequested {

    private List<String> userIDsWhoRequested;

    public UsersWhoRequested(List<String> userIDsWhoRequested){
        this.userIDsWhoRequested = userIDsWhoRequested;
    }

    public List<String> getUserIDsWhoRequested() {
        return userIDsWhoRequested;
    }

}
