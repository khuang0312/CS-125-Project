package com.example.cs125project;

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
        int score = 0;
        double distance = (Math.abs(lat1 - lat2) + Math.abs(long1 - long2));
        if (distance <= 0.01) {       //= Same block
            score += 5;
        } else if (distance <= 0.03) { //= Same street
            score += 4;
        } else if (distance <= 0.05) { //= Westminster to Garden Grove (neighboring cities)
            score += 3;
        } else if (distance <= 0.1) { //= Westminster to Santa Ana
            score += 2;
        } else if (distance <= 0.15) { //= Westminster to UCI
            score += 1;
        }
        return score;
    }

    // Scores two entities based on how many interests they share
    static int interestsScore(ArrayList<String> interests1, ArrayList<String> interests2) {
        int score = 0;
        for (String i : interests1) {
            if (interests2.contains(i)) {
                score += 1;
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
            intensityScore /= intensityList.size();
        }


        switch (intensityScore){
            case 1:
                return "Low";
            case 2:
                return "Medium";
            case 3:
                return "High";
            default:
                return DEFAULT_VALUE;
        }
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
