package com.example.cs125project;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.TimeZone;


public class Report {

    // Location is the bare minimum used to describe the geographic
    // location of a person, place, or thing.

    // locations don't have a primary key since they can occur multiple times
    // among multiple places or people.

    private String intensity;
    private String category;
    private int minutesElapsed;
    private int hoursElapsed;

    private int yearReported;
    private int monthReported;
    private int dayReported;
    private int hourReported;
    private int minuteReported;


    public Report() {
        //Ditto from Profile default constructor
    }


    public Report(String intensity, String category, int min, int hrs) {
        this.intensity = intensity;
        this.category = category;
        this.minutesElapsed = min;
        this.hoursElapsed = hrs;

        Instant instant = Instant.now();
        LocalTime time = instant.atZone(ZoneOffset.UTC).toLocalTime();
        yearReported = instant.atZone(ZoneOffset.UTC).getYear();
        monthReported = instant.atZone(ZoneOffset.UTC).getMonthValue();
        dayReported = instant.atZone(ZoneOffset.UTC).getDayOfMonth();
        hourReported = instant.atZone(ZoneOffset.UTC).getHour();
        minuteReported = instant.atZone(ZoneOffset.UTC).getMinute();
    }

    public String getIntensity() { return this.intensity; }
    public String getCategory() { return this.category; }
    public int getMinutesElapsed() { return this.minutesElapsed; }
    public int getHoursElapsed() { return this.hoursElapsed; }


    public int getYearReported() { return yearReported; }
    public int getMonthReported() { return monthReported; }
    public int getDayReported() { return dayReported; }
    public int getHourReported() { return hourReported; }
    public int getMinuteReported() { return minuteReported; }

    public void setIntensity(String intensity) { this.intensity = intensity; }
    public void setCategory(String category) { this.category = category; }
    public void setMinutesElapsed(int minutesElapsed) { this.minutesElapsed = minutesElapsed; }
    public void setHoursElapsed(int hoursElapsed) { this.hoursElapsed = hoursElapsed; }
    public void setDatetime() {
        Instant instant = Instant.now();
        LocalTime time = instant.atZone(ZoneOffset.UTC).toLocalTime();
        yearReported = instant.atZone(ZoneOffset.UTC).getYear();
        monthReported = instant.atZone(ZoneOffset.UTC).getMonthValue();
        dayReported = instant.atZone(ZoneOffset.UTC).getDayOfMonth();
        hourReported = instant.atZone(ZoneOffset.UTC).getHour();
        minuteReported = instant.atZone(ZoneOffset.UTC).getMinute();
    }







}
