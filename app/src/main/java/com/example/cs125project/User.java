package com.example.cs125project;

//import androidx.annotation.NonNull;
//import androidx.room.PrimaryKey; //Where IS the PrimaryKey annotation?

import java.util.TreeSet;

//TODO: Finalize user setup and attribute changing (all classes)
//May want to add more vectors to compare after finalizing Profile class
public class User {
    // this serves as our primary key
    // allows user to change email and username
    private final int id;

    // these keys can never be null...
    private String email;
    private String username;

    // these keys can...
    private String name;
    private int age;
    private Location location;
    private TreeSet<Interest> interests;


    public User(int id, String email, String username) {
        this.id = id;
        this.email = email;
        this.username = username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public void replaceInterest(TreeSet<Interest> interests) {
        this.interests = interests;
    }

    // GETTERS
    public int getID() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    // SETTERS
    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        if (age < 0) {
            return;
        }
        this.age = age;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public TreeSet<Interest> getInterests() {
        return interests;
    }

    //Calculates weighted vector sum of two users with content-based feature vectors
    public int calculateWeightedVectorSum(User user2) {
        int sum = 0;
        //Context check: ignore users not in the same state
        if (!this.location.getState().equals(user2.getLocation().getState())) {
            return 0;
        }
        //Get p2's non-Profile data for convenience and simplicity
        float u2Lat = user2.getLocation().getLatitude();
        float u2Lon = user2.getLocation().getLongitude();
        TreeSet<Interest> p2Interests = user2.getInterests();
        /*Checks user distances by checking for the same city; very basic
        If the latlong check works better, feel free to remove this
        if (this.location.getCity().equals(p2.getLocation().getCity())) {
            sum += 5;
        }*/
        //Uses latitude and longitude to measure users' distances; weight decreases with distance
        float distance = (Math.abs(this.location.getLatitude() - u2Lat) + Math.abs(this.location.getLongitude() - u2Lon));
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
        TreeSet<Interest> sharedInterests = new TreeSet<Interest>(interests);
        sharedInterests.retainAll(p2Interests);

        sum += sharedInterests.size();

        return sum;
    }
}