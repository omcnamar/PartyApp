package com.olegsagenadatrytwo.partyapp.eventbus;

import java.util.List;

/**
 * Created by omcna on 10/1/2017.
 */

public class MyLikes {

    private List<String> likes;

    public MyLikes(List<String> likes){
        this.likes = likes;
    }

    public List<String> getLikes(){
        return likes;
    }
}
