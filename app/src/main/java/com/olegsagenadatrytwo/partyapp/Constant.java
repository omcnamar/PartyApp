package com.olegsagenadatrytwo.partyapp;

/**
 * Created by Admin on 9/14/2017.
 */

public class Constant {

    /**
     * Eventbrite
     */
    public static final String EVENTBRITE = "eventbrite";
    public static final String EVENTBRITE_TOKEN = "JWRK43KE6JVXMOR3JCLF";
    public static final String EVENTBRITE_BASE_URL = "https://www.eventbriteapi.com/v3/";
    public static final String EVENTBRITE_EVENTS_PATH = "events/search/";

    /**
     * Google geolocation
     */
    public static final String GEOLOCATION = "geolocation";
    public static final String GEOCODE_BASE_URL = "https://maps.googleapis.com/";
    public static final String GEOCODE_PATH = "maps/api/geocode/json?";
    //API Keys
    public static final String GOOGLE_GEO_API_KEY = "AIzaSyBjAJD64HgIglsVxviCvYYMb8w04ysTxTg";
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

    public static final int REQUEST_PERMISSION = 402;

    //Zoom Levels
    public static final int ZOOM_LEVEL_ZERO = 5;
    public static final int ZOOM_LEVEL_ONE = 10;
    public static final int ZOOM_LEVEL_TWO = 25;
    public static final int ZOOM_LEVEL_THREE = 50;
    public static final int ZOOM_LEVEL_FOUR = 100;
    public static final int ZOOM_LEVEL_FIVE = 200;
    public static final int ZOOM_LEVEL_SIX = 500;
    public static final int ZOOM_LEVEL_SEVEN = 1000;
    public static final int ZOOM_LEVEL_EIGHT = 1500;
    public static final int ZOOM_LEVEL_NINE = 3000;
    public static final int ZOOM_LEVEL_TEN = 6000;

    /**
     * Shared preference keys
     */
    public static final String ZIP = "ZipSharedPreference";
    public static final String CURRENT_LOCATION = "CurrentLocationSP";

    /**
     * Firebase
     */
    public static final String ADD_NEW_PARTY = "add";
    public static final String UPDATE_PARTY = "update";
    public static final String DELETE_PARTY = "delete";

    /**
     * global parties constants
     */
    public static final String PARTIES = "parties";
    public static final String PROFILES = "profiles";
    public static final String IDS_OF_USERS_WHO_LIKED_THIS_PARTY = "idsOfUsersWhoLikedThisParty";
    public static final String IDS_OF_PARTIES_THAT_CURRNET_USER_LIKED = "idsOfPartiesThatCurrentUserLiked";
    public static final String ID = "id";
    public static final String OWNERID = "ownerId";
    public static final String PARTYNAME = "partyName";
    public static final String DESCRIPTION = "description";
    public static final String ADDRESS = "address";
    public static final String DATE = "date";
    public static final String STARTTIME = "startTime";
    public static final String ENDDATE = "endDate";
    public static final String ENDTIME = "endTime";
    public static final String AGEREQUIRED = "ageRequired";
    public static final String CAPACITY = "capacity";
    public static final String IMAGEURL = "imageURL";
    public static final String DISTANCE = "distance";
    public static final String LATLNG = "latlng";
}
