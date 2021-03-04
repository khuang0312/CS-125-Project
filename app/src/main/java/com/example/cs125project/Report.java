package com.example.cs125project;

import java.util.ArrayList;

public class Report {

    // these keys can never be null...
    private String intensity;
    private String interest;
    private int hrs;
    private int min;


    public Report() {
        //need to create an original id
    }

    public Report(String intensity, String interest, int hrs, int min) {
        this.intensity = intensity;
        this.interest = interest;
        this.hrs = hrs;
        this.min = min;
    }

    public String getIntensity(){return this.intensity;}
    public String getInterest(){return this.interest;}
    public int getHours(){return this.hrs;}
    public int getMinutes(){return this.min;}
}
