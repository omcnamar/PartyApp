package com.olegsagenadatrytwo.partyapp.eventbus;

import java.util.List;


public class Friends {

    private List<String> friendsList;

    public Friends(List<String> friendsList) {

        this.friendsList = friendsList;
    }

    public List<String> getFriendsList() {
        return friendsList;
    }


}
