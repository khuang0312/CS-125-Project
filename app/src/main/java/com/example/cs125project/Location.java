package com.example.cs125project;

//import androidx.annotation.NonNull;

public class Location {
    // Location is the bare minimum used to describe the geographic
    // location of a person, place, or thing.

    // locations don't have a primary key since they can occur multiple times
    // among multiple places or people.

    private float longitude;
    private float latitude;

    private String city;
    private String state;
    private String country;


    public Location() {
        //Ditto from Profile default constructor
    }

    public Location(float lat, float lon, String city, String state, String country) {
        this.latitude = lat;
        this.longitude = lon;
        this.city = city;
        this.state = state;
        this.country = country;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String c) {
        this.city = c;
    }

    public float getLatitude() {
        return this.latitude;
    }

    public void setLatitude(float lat) {
        this.latitude = lat;
    }

    public float getLongitude() {
        return this.longitude;
    }

    public void setLongitude(float lon) {
        this.longitude = lon;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String s) {
        this.state = s;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String c) {
        this.country = c;
    }
}