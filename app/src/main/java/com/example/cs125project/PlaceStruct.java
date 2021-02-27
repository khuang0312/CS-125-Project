package com.example.cs125project;

import java.math.BigInteger;
import java.util.ArrayList;

public class PlaceStruct {
    // Place describes a Point of Interest that a User would want to go to...
    // It contains a Location

    // BigInteger is used because there are many places...
    private final BigInteger id; // Serves as unique identifier
    private Location location;

    private String name;
    private String address1;
    private String address2;
    private String address3;
    private int postal_code;

    // These are the user interests that the place caters to...
    private ArrayList<Interest> interests;


    public PlaceStruct(BigInteger id, Location loc, String name, String address1, int postal_code) {
        this.id = id;
        this.location = loc;
        this.name = name;
        this.address1 = address1;
        this.postal_code = postal_code;
        this.interests = new ArrayList<Interest>();
    }

    public PlaceStruct(BigInteger id, Location loc, String name, String address1, String address2, int postal_code) {
        this.id = id;
        this.location = loc;
        this.name = name;
        this.address1 = address1;
        this.address2 = address2;
        this.postal_code = postal_code;
    }

    public void setAddress(String address1, String address2, String address3) {
        this.address1 = address1;
        this.address2 = address2;
        this.address3 = address3;
    }

    public void addInterest(String str) {
        Interest i = Interest.getInterest(str);
        if (i != Interest.UNAVAILABLE) {
            interests.add(i);
        }
    }

    public void removeInterest(String str) {
        Interest i = Interest.getInterest(str);
        interests.remove(i);
    }

    public void clearInterests() {
        interests.clear();
    }

    public void replaceInterest(ArrayList<Interest> interests) {
        this.interests = interests;
    }

    // GETTERS
    public Location getLocation() {
        return location;
    }

    // SETTERS
    public void setLocation(Location location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getAddress() {
        return new String[]{address1, address2, address3};
    }

    public int getPostalCode() {
        return postal_code;
    }

    public void setPostalCode(int postal_code) {
        this.postal_code = postal_code;
    }

    public ArrayList<Interest> getInterests() {
        return interests;
    }
}
