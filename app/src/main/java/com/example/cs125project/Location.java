package com.example.cs125project;

//import androidx.annotation.NonNull;

public class Location {
    //@PrimaryKey
    //@NonNull
    private int cityLatitude;
    //@PrimaryKey
    //@NonNull
    private int cityLongitude;
    private String city;
    private String state;
    private String country;

    public Location(){
        //Ditto from Profile default constructor
    }

    public Location(int lat, int lon, String city, String state, String country){
        this.cityLatitude = lat;
        this.cityLongitude = lon;
        this.city = city;
        this.state = state;
        this.country = country;
    }

    public void setCityLatitude(int lat){
        this.cityLatitude = lat;
    }

    public void setCityLongitude(int lon){
        this.cityLongitude = lon;
    }

    public void setCity(String c){
        this.city = c;
    }

    public void setState(String s){
        this.state = s;
    }

    public void setCountry(String c){
        this.country = c;
    }

    public String getCity(){
        return this.city;
    }

    public int getCityLatitude(){
        return this.cityLatitude;
    }

    public int getCityLongitude(){
        return this.cityLongitude;
    }

    public String getState(){
        return this.state;
    }

    public String getCountry(){
        return this.country;
    }
}