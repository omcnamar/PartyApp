package com.olegsagenadatrytwo.partyapp.model.custom_map;

import android.location.Address;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by aaron on 9/16/17.
 */

public class CustomLocationObject {
    Double latitude;
    Double longitude;
    LatLng latitude_longitude;
    String id = "id";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Address getPhysicalAddress() {
        return physicalAddress;
    }

    public void setPhysicalAddress(Address physicalAddress) {
        this.physicalAddress = physicalAddress;
    }

    Address physicalAddress;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    Location location;

    public CustomLocationObject() {
        //default constructor
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public LatLng getLatitude_longitude() {
        return latitude_longitude;
    }

    public void setLatitude_longitude(LatLng latitude_longitude) {
        this.latitude_longitude = latitude_longitude;
    }
}
