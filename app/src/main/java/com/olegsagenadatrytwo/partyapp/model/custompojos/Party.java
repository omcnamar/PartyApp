package com.olegsagenadatrytwo.partyapp.model.custompojos;

import android.os.Parcel;
import android.os.Parcelable;

public class Party implements Parcelable{

    private String id;
    private String ownerId;
    private String partyName;
    private String description;
    private String address;
    private String date;
    private String startTime;
    private String endDate;
    private String endTime;
    private String ageRequired;
    private int capacity;
    private String imageURL;
    private boolean isLiked;
    private String distance;
    private String latlng;

    public Party() {
    }

    public Party(String id, String ownerId, String partyName, String description, String address, String date, String startTime, String endDate, String endTime, String ageRequired, int capacity, String imageURL, boolean isLiked, String distance, String latlng) {
        this.id = id;
        this.ownerId = ownerId;
        this.partyName = partyName;
        this.description = description;
        this.address = address;
        this.date = date;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.ageRequired = ageRequired;
        this.capacity = capacity;
        this.imageURL = imageURL;
        this.isLiked = isLiked;
        this.distance = distance;
        this.latlng = latlng;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getPartyName() {
        return partyName;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getAgeRequired() {
        return ageRequired;
    }

    public void setAgeRequired(String ageRequired) {
        this.ageRequired = ageRequired;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getLatlng() {
        return latlng;
    }

    public void setLatlng(String latlng) {
        this.latlng = latlng;
    }

    public static Creator<Party> getCREATOR() {
        return CREATOR;
    }

    protected Party(Parcel in) {
        id = in.readString();
        ownerId = in.readString();
        partyName = in.readString();
        description = in.readString();
        address = in.readString();
        date = in.readString();
        startTime = in.readString();
        endDate = in.readString();
        endTime = in.readString();
        ageRequired = in.readString();
        capacity = in.readInt();
        imageURL = in.readString();
        isLiked = in.readByte() != 0;
        distance = in.readString();
        latlng = in.readString();
    }

    public static final Creator<Party> CREATOR = new Creator<Party>() {
        @Override
        public Party createFromParcel(Parcel in) {
            return new Party(in);
        }

        @Override
        public Party[] newArray(int size) {
            return new Party[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(ownerId);
        parcel.writeString(partyName);
        parcel.writeString(description);
        parcel.writeString(address);
        parcel.writeString(date);
        parcel.writeString(startTime);
        parcel.writeString(endDate);
        parcel.writeString(endTime);
        parcel.writeString(ageRequired);
        parcel.writeInt(capacity);
        parcel.writeString(imageURL);
        parcel.writeByte((byte) (isLiked ? 1 : 0));
        parcel.writeString(distance);
        parcel.writeString(latlng);
    }
}
