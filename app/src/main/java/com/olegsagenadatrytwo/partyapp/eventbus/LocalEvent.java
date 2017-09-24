package com.olegsagenadatrytwo.partyapp.eventbus;

/**
 * Created by Admin on 9/22/2017.
 */

public class LocalEvent {

    final String locale;

    public LocalEvent(String locale) {
        this.locale = locale;
    }

    public String getLocale() {
        return locale;
    }
}
