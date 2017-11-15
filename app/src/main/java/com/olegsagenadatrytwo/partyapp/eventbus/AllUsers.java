package com.olegsagenadatrytwo.partyapp.eventbus;

import java.util.List;


public class AllUsers {
    private List<String> userIDs;

    private List<String> userNames;

    public AllUsers(List<String> userIDs, List<String> userNames){
        this.userIDs = userIDs;
        this.userNames = userNames;
    }

    public List<String> getUserIds(){
        return userIDs;
    }

    public List<String> getUserNames() {
        return userNames;
    }
}
