package com.example.cs125project;

import android.util.Log;

import java.util.ArrayList;
import java.lang.Integer;
import java.util.HashMap;
import java.util.Map;


//import javafx.util.Pair; //implementing Pair compatibility seems like too much of a pain

public class Recommendation {
    // pops up if you have 0 length arrays
    public static final String DEFAULT_VALUE = "NOT ENOUGH INFO";


    //Scores the distance between two entities based on their lat-long coordinates
    static int locationScore(double lat1, double long1, double lat2, double long2) {
        // haversine formula
        final int R = 6_371_000; // in meters

        double phi1 = lat1 * Math.PI / 180;
        double phi2 = lat2 * Math.PI / 180;
        double deltaphi = (lat2 - lat1) * Math.PI / 180;
        double deltalambda = (long2 - long1) * Math.PI / 180;

        double a = Math.sin(deltaphi / 2) * Math.sin(deltaphi / 2)  +
                Math.cos(phi1) + Math.cos(phi2)
                * Math.sin(deltalambda/2) * Math.sin(deltalambda / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = R * c; // in meters
        Log.d("haversine distance", Double.toString(dist));
        int score = 0;

        if (dist <= 924.2) {
            score += 6;
        } else if (dist <= 2772 ) {
            score += 5;
        } else if (dist <= 4621) {
            score += 4;
        } else if (dist <= 9242) {
            score += 3;
        } else if (dist <= 13860) {
            score += 2;
        } else if (dist <= 50000) {
            score += 1;
        }

        return score;
    }

    // Scores two entities based on how many interests they share
    static int interestsScore(ArrayList<String> interests1, ArrayList<String> interests2) {
        int score = 0;
        for (String i : interests1) {
            if (interests2.contains(i)) {
                score += 4;
            }
        }
        return score;
    }

    //Calculates the recommended amount of time for an activity given the last n user reports
    //Returns the average time of the last n user reports...
    static int getRecommendedDuration(ArrayList<Integer> durationList) {
        int minutes = 0;
        for (int i : durationList){
            minutes += i;
        }
        if (durationList.size() != 0) {
            return minutes / durationList.size();
        } else {
            return 0;
        }
    }

    //Calculates the recommended intensity level for an activity given the last n user reports
    //Takes the average intensity
    static String getRecommendedIntensity(ArrayList<String> intensityList) {
        int intensityScore = 0;
        double trueAverage = 0.0;
        for (String intensity : intensityList) {
            //Ints to be replaced with actual intensity level strings?
            if (intensity.equals("Low")){
                intensityScore += 1;
            }
            else if (intensity.equals("Medium")){
                intensityScore += 2;
            }
            else if (intensity.equals("High")){
                intensityScore += 3;
            }
        }

        if (intensityList.size() != 0) {
            trueAverage = (double)intensityScore/intensityList.size();
        }

        //Default value check here since the average cannot be below 1
        if (trueAverage < 1){
            return DEFAULT_VALUE;
        } else if (trueAverage <= 1.5) {
            return "Low";
        } else if (1.5 < trueAverage && trueAverage <= 2.5) {
            return "Medium";
        } else {
            return "High";
        }

        /*switch (intensityScore){
            case 1:
                return "Low";
            case 2:
                return "Medium";
            case 3:
                return "High";
            default:
                return DEFAULT_VALUE;
        }*/
    }

    //Returns a recommended activity given the user reports
    //Specifically, we return the most common activity
    //By default, this algorithm will return the first activity it finds if all are tied
    //Returns null if no interests are found...
    static String getRecommendedInterest(ArrayList<String> interestList) {
        HashMap<String, Integer> frequencies = new HashMap<String, Integer>();

        for (String interest : interestList) {
            if (frequencies.containsKey(interest)) {
                frequencies.put(interest, frequencies.get(interest) + 1);
            } else {
                frequencies.put(interest, 1);
            }
        }

        int maxFreq = 0;
        String recommendedInterest = DEFAULT_VALUE;
        for (Map.Entry<String, Integer> interest : frequencies.entrySet()) {
            if (interest.getValue() > maxFreq) {
                recommendedInterest = interest.getKey();
                maxFreq = interest.getValue();
            }
        }

        return recommendedInterest;
    }
}
