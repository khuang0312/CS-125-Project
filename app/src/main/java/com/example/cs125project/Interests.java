package com.example.cs125project;

public class Interests {
    //@NonNull
    //@PrimaryKey
    private int ID;
    private String interest;

    public Interests() {
        //Ditto from Profile default constructor
    }

    public Interests(int i, String interest) {
        this.ID = i;
        this.interest = interest;
    }

    public int getID() {
        return this.ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getInterest() {
        return this.interest;
    }

    public void setInterest(String s) {
        this.interest = s;
    }
}
