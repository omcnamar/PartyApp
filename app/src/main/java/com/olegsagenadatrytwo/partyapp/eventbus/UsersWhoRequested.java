package com.olegsagenadatrytwo.partyapp.eventbus;

import java.util.List;


public class AllUsers {
    private List<String> userIDs;
    private List<String> userNames;
    private List<String> myFriendsList;

    public AllUsers(List<String> userIDs, List<String> userNames, List<String> myFriendsList){
        this.userIDs = userIDs;
        this.userNames = userNames;
        this.myFriendsList = myFriendsList;
    }

    public List<String> getUserIds(){
        return userIDs;
    }

    public List<String> getUserNames() {
        return userNames;
    }

    public List<String> getMyFriendsList() {
        return myFriendsList;
    }

}
