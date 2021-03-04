package com.example.cs125project;

import java.util.Date;

public class Report {

    // Location is the bare minimum used to describe the geographic
    // location of a person, place, or thing.

    // locations don't have a primary key since they can occur multiple times
    // among multiple places or people.

    private String intensity;
    private String category;
    private int min;
    private int hrs;
    private Date submissionTime;


    public Report() {
        //Ditto from Profile default constructor
    }

    public Report(String intensity, String category, int min, int hrs) {
        this.intensity = intensity;
        this.category = category;
        this.min = min;
        this.hrs = hrs;
    }

    public String getIntensity() {
        return this.intensity;
    }

    public void setIntensity(String intensity) {
        this.intensity = intensity;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getMin() {
        return this.min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public void setHrs(int hrs) {
        this.hrs = hrs;
    }

    public int getHrs() {
        return hrs;
    }

    public void setSubmissionTime(Date datetime) {
        this.submissionTime = datetime;
    }

    public Date getSubmissionTime() {
        return submissionTime;
    }
}
