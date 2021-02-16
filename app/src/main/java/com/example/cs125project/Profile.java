package com.example.cs125project;

//import androidx.annotation.NonNull;
//import androidx.room.PrimaryKey; //Where IS the PrimaryKey annotation?

import java.util.ArrayList;

//TODO: Finalize user setup and attribute changing (all classes)
//May want to add more vectors to compare after finalizing Profile class
public class Profile {
    //@PrimaryKey
    //@NonNull
    private int ID;
    private int age;
    //@NonNull
    private String userName;
    private String email;
    private String name;
    private Location location;
    private ArrayList<String> interests;

    public Profile() {
        //Required for DataSnapshot.getValue(Profile.class) calls
    }

    public Profile(String userName, String name, String email, int id, int age, Location location, ArrayList<String> interests) {
        this.userName = userName;
        this.name = name;
        this.email = email;
        this.ID = id;
        this.age = age;
        this.location = location;
        this.interests = interests;
    }

    public int getID() {
        return this.ID;
    }

    public void setID(int i) {
        this.ID = i;
    }

    public String getName() {
        return this.name;
    }

    public String getUserName() {
        return this.userName;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String e) {
        this.email = e;
    }

    public int getAge() {
        return this.age;
    }

    public void setAge(int i) {
        this.age = i;
    }

    public Location getLocation() {
        return this.location;
    }

    public ArrayList<String> getInterests() {
        return this.interests;
    }

    //Calculates weighted vector sum of two users with content-based feature vectors
    public int calculateWeightedVectorSum(Profile p2) {
        int sum = 0;
        //Context check: ignore users not in the same state
        if (!this.location.getState().equals(p2.getLocation().getState())) {
            return 0;
        }
        //Get p2's non-Profile data for convenience and simplicity
        int p2Lat = p2.getLocation().getCityLatitude();
        int p2Lon = p2.getLocation().getCityLongitude();
        ArrayList<String> p2Interests = p2.getInterests();
        /*Checks user distances by checking for the same city; very basic
        If the latlong check works better, feel free to remove this
        if (this.location.getCity().equals(p2.getLocation().getCity())) {
            sum += 5;
        }*/
        //Uses latitude and longitude to measure users' distances; weight decreases with distance
        float distance = Math.abs(this.location.getCityLatitude() - p2Lat) + Math.abs(this.location.getCityLongitude() - p2Lon);
        if (distance <= 0.01) {       //= Same block
            sum += 5;
        } else if (distance <= 0.03) { //= Same street
            sum += 4;
        } else if (distance <= 0.05) { //= Westminster to Garden Grove (neighboring cities)
            sum += 3;
        } else if (distance <= 0.1) { //= Westminster to Santa Ana
            sum += 2;
        } else if (distance <= 0.15) { //= Westminster to UCI
            sum += 1;
        }
        //Checks what interests the users share
        for (int i = 0; i < this.interests.size(); i++) {
            if (p2Interests.contains(this.interests.get(i))) {
                sum += 1;
            }
        }
        return sum;
    }
}