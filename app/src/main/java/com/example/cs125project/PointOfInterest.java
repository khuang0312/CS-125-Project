package com.example.cs125project;

import java.util.List;

public class PointOfInterest {
    private double longitude;
    private double latitude;
    private String interest;
    private List<String> interests;
    private String name;

    public PointOfInterest() {

    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public void setInterest(String interest) {
        this.interest = interest;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setInterests(List<String> interests) {this.interests = interests; }



    public double getLatitude() { return latitude; }
    public double getLongitude() {
        return longitude;
    }
    public String getInterest() {return interest; }
    public List<String> getInterests() { return interests; }
    public String getName() {
        return name;
    }
}
