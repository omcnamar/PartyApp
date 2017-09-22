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

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    private int currentAttendance;

    public Party() {
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
        currentAttendance = in.readInt();
        distance = in.readString();
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
    public String toString() {
        return "Party{" +
                "id='" + id + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", partyName='" + partyName + '\'' +
                ", description='" + description + '\'' +
                ", address='" + address + '\'' +
                ", date='" + date + '\'' +
                ", startTime='" + startTime + '\'' +
                ", date='" + endDate + '\'' +
                ", endTime='" + endTime + '\'' +
                ", ageRequired='" + ageRequired + '\'' +
                ", capacity=" + capacity +
                ", currentAttendance=" + currentAttendance +
                '}';
    }

    public Party(Party party){
        this.id = party.getId();
        this.ownerId = party.getOwnerId();
        this.partyName = party.getPartyName();
        this.description = party.getDescription();
        this.address = party.getAddress();
        this.date = party.getDate();
        this.startTime = party.getStartTime();
        this.endDate = party.getEndDate();
        this.endTime = party.getEndTime();
        this.ageRequired = party.getAgeRequired();
        this.capacity = party.getCapacity();
        this.currentAttendance = party.getCurrentAttendance();
        this.isLiked = party.isLiked();
}

    @Override
    public boolean equals(Object obj) {
        Party p = (Party)obj;
        boolean eq = true;

        if(!this.id.toString().equals(p.getId().toString())){
            return false;
        }

        return eq;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
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

    public int getCurrentAttendance() {
        return currentAttendance;
    }

    public void setCurrentAttendance(int currentAttendance) {
        this.currentAttendance = currentAttendance;
    }

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
        parcel.writeInt(currentAttendance);
        parcel.writeString(distance);
    }


}
