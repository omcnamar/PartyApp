package com.olegsagenadatrytwo.partyapp.model.custompojos;

import java.util.UUID;

public class Party {
    private UUID id;
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
    private int currentAttendance;

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

    public Party() {
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
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
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
}
