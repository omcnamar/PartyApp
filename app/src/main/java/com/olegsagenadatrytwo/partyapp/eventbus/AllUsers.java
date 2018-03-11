package com.olegsagenadatrytwo.partyapp.eventbus;

import java.util.List;


public class AllUsers {
    private List<String> userIDs;
    private List<String> userNames;
    private List<String> myFriendsList;

    private List<String> listOfAlreadyRequested;

    public AllUsers(List<String> userIDs, List<String> userNames, List<String> myFriendsList, List<String> listOfAlreadyRequested){
        this.userIDs = userIDs;
        this.userNames = userNames;
        this.myFriendsList = myFriendsList;
        this.listOfAlreadyRequested = listOfAlreadyRequested;
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

    public List<String> getListOfAlreadyRequested() {
        return listOfAlreadyRequested;
    }

}
