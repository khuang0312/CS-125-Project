package com.example.cs125project;

import java.util.ArrayList;
import java.lang.Integer;
import java.util.List;
//import javafx.util.Pair; //implementing Pair compatibility seems like too much of a pain

public class Recommendation {
    //Scores the distance between two entities based on their lat-long coordinates
    static int locationScore(float lat1, float long1, float lat2, float long2) {
        int score = 0;
        float distance = (Math.abs(lat1 - lat2) + Math.abs(long1 - long2));
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

    //Scores two entities based on how many interests they share
    static int interestsScore(ArrayList<Interest> interests1, ArrayList<Interest> interests2) {
        int score = 0;
        for (Interest i : interests2) {
            if (interests1.contains(i)) {
                score += 4; //change to 1 if weight is applied outside this function?
            }
        }
        return score;
    }

    //Calculates the recommended amount of time for an activity given the last n user reports
    static int getRecommendedDuration(ArrayList<Integer> durationList, int n) {
        int minutes = 0;
        int listSize = durationList.size()-1;
        for (int i = listSize; i >= listSize-n; i--){ //or (int i = 0; i < n; i++) for reverse order
            minutes += durationList.get(i);
        }
        minutes = minutes/n;
        return minutes;
    }

    //Calculates the recommended intensity level for an activity given the last n user reports
    static int getRecommendedIntensity(ArrayList<Integer> intensityList, int n) {
        int intensity = 0;
        int intensityLevel = 0;
        int listSize = intensityList.size()-1;
        for (int i = listSize; i >= listSize-n; i--){ //or (int i = 0; i < n; i++) for reverse order
            //Ints to be replaced with actual intensity level strings?
            if (intensityList.get(i).equals(1)){
                intensityLevel = 1;
            }
            else if (intensityList.get(i).equals(2)){
                intensityLevel = 2;
            }
            else if (intensityList.get(i).equals(3)){
                intensityLevel = 3;
            }
            intensity += intensityLevel;
        }
        intensity = intensity/n;
        return intensity;
    }

    //Returns a recommended activity given the last n user reports
    //By default, this algorithm will return the first activity it finds if all are tied
    static Interest getRecommendedInterest(ArrayList<Interest> interestList, int n) {
        // average of last n interests
        Interest recommended;
        Interest maxActivity = Interest.UNAVAILABLE;
        int maxCount = 0;
        int currCount;
        int listSize = interestList.size()-1;
        List<Interest> interests = interestList.subList(listSize-n, listSize); //or interestList.subList(0,n) for reverse order
        for (int i = 0; i < n; i++){
            if (interests.get(i).equals(maxActivity)){
                continue;
            }
            currCount = 0;
            for (int j = 0; j < n; j++){
                if (interests.get(j).equals(interests.get(i))){
                    currCount++;
                }
            }
            if (currCount > maxCount){
                maxCount = currCount;
                maxActivity = interests.get(i);
            }
        }
        recommended = maxActivity;
        return recommended;

    }
}