package com.example.cs125project;

// import androidx.annotation.NonNull;

public enum Interest {
//    WALKING,
//    RUNNING,
//    SWIMMING,
//    CLIMBING,
//    YOGA,
//    BADMINTON,
//    HOCKEY,
//    TENNIS,
//    BASKETBALL,
//    SOCCER,
//    FOOTBALL,
//    BASEBALL,
//    GOLF,
//    PILATES,
//    PARKOUR,
//    DANCING,
//    LACROSSE,
//    WRESTLING,
//    MMA,
//    UNAVAILABLE;
    Walking,
    Running,
    Swimming,
    Climbing,
    Yoga,
    Badminton,
    Hockey,
    Tennis,
    Basketball,
    Soccer,
    Football,
    Baseball,
    Golf,
    Pilates,
    Parkour,
    Dancing,
    Lacrosse,
    Wrestling,
    Mma,
    UNAVAILABLE;


    public static String getString(Interest interest) {
        for (Interest i : Interest.values()) {
            if (i == interest) {
                return i.toString();
            }
        }
        return "UNAVAILABLE";
    }

    public static Interest getInterest(String s) {
        for (Interest i : Interest.values()) {
            if (i.toString().equals(s)) {
                return i;
            }
        }
        return Interest.UNAVAILABLE;
    }
}
