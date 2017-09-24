package com.olegsagenadatrytwo.partyapp.eventbus;

import com.olegsagenadatrytwo.partyapp.model.geocoding_profile.GeocodingProfile;

/**
 * Created by Admin on 9/22/2017.
 */

public class GeocodingProfileEvent {

    final GeocodingProfile profile;

    public GeocodingProfileEvent(GeocodingProfile profile) {
        this.profile = profile;
    }

    public GeocodingProfile getProfile() {
        return profile;
    }
}
