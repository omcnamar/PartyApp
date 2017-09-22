package com.olegsagenadatrytwo.partyapp;

/**
 * Created by Admin on 9/14/2017.
 */

public class Constant {

    /**
     * Eventbrite
     */
    public static final String EVENTBRITE_TOKEN = "JWRK43KE6JVXMOR3JCLF";
    public static final String EVENTBRITE_BASE_URL = "https://www.eventbriteapi.com/v3/";
    public static final String EVENTBRITE_EVENTS_PATH = "events/search/";

    /**
     * Google geolocation
     */
    public static final String GEOCODE_BASE_URL = "https://maps.googleapis.com/";
    public static final String GEOCODE_PATH = "maps/api/geocode/json?";
    //API Keys
    public static final String GOOGLE_GEO_API_KEY = "AIzaSyBy4spNfpqrr8awZ3c1MmmtbHipkHU_2Ss";
    //Misc inforamtion
    public static final double RADIUS_OF_EARTH= 637;//radius of earth in Km
    public static final double METERS_TO_MILES_RATIO = 0.000621371;
    public static final double MILES_TO_METERS_RATIO = 1609.344;
    //Log Tags
    public static final String TAG_LOCATION_INFO = "TAG_Location_info";
    public static final String TAG_MARKER_COLOR_SET = "TAG_Marker_Color_Set";
    public static final String TAG_MARKER_LOCATION_SET = "TAG_Marker_Loc_Set";
    public static final String TAG_IMAGE_MARKER_SET = "TAG_Marker_Img_Set";
    public static final String TAG_DISTANCE_RETURNS = "TAG_Distances";

    //Color Names
    public static final String COLOR_RED = "Red";
    public static final String COLOR_ORANGE = "Orange";
    public static final String COLOR_YELLOW = "Yellow";
    public static final String COLOR_GREEN = "Green";
    public static final String COLOR_CYAN = "Cyan";
    public static final String COLOR_AZURE = "Azure";
    public static final String COLOR_BLUE = "Blue";
    public static final String COLOR_VIOLET = "Violet";
    public static final String COLOR_MAGENTA = "Magenta";
    public static final String COLOR_ROSE = "Rose";

    //Default values
    public static final String DEFAULT_TITLE = "New Location";

    //Location Update settings
    public static final int LOCATION_UPDATE_MINUTES = 5;
    public static final int LOCATIOM_UPDATE_MILES = 10;

    //Markers length and width
    public static final int MARKER_LENGTH_DEFAULT = 50;
    public static final int MARKER_WIDTH_DEFAULT = 50;

}
