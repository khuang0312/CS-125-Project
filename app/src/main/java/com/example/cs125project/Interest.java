package com.example.cs125project;

// import androidx.annotation.NonNull;

public enum Interest {
    WALKING,
    RUNNING,
    SWIMMING,
    CLIMBING,
    YOGA,
    BADMINTON,
    HOCKEY,
    TENNIS,
    BASKETBALL,
    SOCCER,
    FOOTBALL,
    BASEBALL,
    GOLF,
    PILATES,
    PARKOUR,
    DANCING,
    LACROSSE,
    WRESTLING,
    MARTIAL_ARTS,
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
