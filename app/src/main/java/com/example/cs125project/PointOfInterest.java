package com.example.cs125project;

public class PointOfInterest {
    private double longitude;
    private double latitude;
    private String Interest;

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setInterest(String Interest) {
        this.Interest = Interest;
    }

    public String getInterest() {
        return Interest;
    }
}