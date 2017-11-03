package com.olegsagenadatrytwo.partyapp.eventbus;

import java.util.List;


public class AllUsers {
    private List<String> userIDs;

    public AllUsers(List<String> userIDs){
        this.userIDs = userIDs;
    }

    public List<String> getUserIds(){
        return userIDs;
    }
}
