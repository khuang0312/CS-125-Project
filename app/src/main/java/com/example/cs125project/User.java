package com.example.cs125project;

//import androidx.annotation.NonNull;
//import androidx.room.PrimaryKey; //Where IS the PrimaryKey annotation?

import java.util.ArrayList;

//TODO: Finalize user setup and attribute changing (all classes)
//May want to add more vectors to compare after finalizing Profile class
public class User {
    //username will be used as primary key in firebase storage
    private String username;

    // these keys can never be null...
    private String name;
    private String email;
    private String age;

    // address is stored to avoid a lat-long lookup
    // this will populate the auto-complete field if not updated
    private String address;
    private String city;
    private String state;
    private String country;
    private double longitude;
    private double latitude;

    private ArrayList<Interest> interests;  // TODO: update this to use strings instead...

    public User() {
        // needed because dataSnapshot.getValue()
        // requires a class with a paramterless constructor
        this.username = "unknown";
    }

    public User(String username) {
        this.username = username; // usernames never change
    }

    // Interest related code
    public ArrayList<Interest> getInterests() { return interests; }

    public void addInterest(String str) {
        Interest i = Interest.getInterest(str);
        if (i != Interest.UNAVAILABLE && !interests.contains(i)) {
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


    // SETTERS
    public void setEmail(String email) {
        this.email = email;
    }
    public void setName(String name) { this.name = name; }
    public void setAge(String age) { this.age = age; }
    public void setAddress(String address) { this.address = address; }
    public void setCity(String city) { this.city = city; }
    public void setState(String state) { this.state = state; }
    public void setCountry(String country) { this.country = country; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    // GETTERS
    public String getUsername() { return username; }
    public String getEmail() {
        return email;
    }
    public String getName() { return name; }
    public String getAge() { return age; }
    public String getAddress() { return this.address; }
    public String getCity() { return this.city; }
    public String getState() { return this.state; }
    public String getCountry() { return this.country; }
    public double getLatitude() { return this.latitude; }
    public double getLongitude() { return this.longitude; }
}