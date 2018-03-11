package com.olegsagenadatrytwo.partyapp.eventbus;

/**
 * Created by omcna on 10/1/2017.
 */

public class Caller {

    String message;

    public Caller(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
