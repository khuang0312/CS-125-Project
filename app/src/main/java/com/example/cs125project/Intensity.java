package com.example.cs125project;

public enum Intensity {
    Low,
    Medium,
    High,
    UNAVAILABLE;

    public static String getString(com.example.cs125project.Intensity intensity) {
        for (com.example.cs125project.Intensity i : com.example.cs125project.Intensity.values()) {
            if (i == intensity) {
                return i.toString();
            }
        }
        return "UNAVAILABLE";
    }

    public static com.example.cs125project.Intensity getInterest(String s) {
        for (com.example.cs125project.Intensity i : com.example.cs125project.Intensity.values()) {
            if (i.toString().equals(s)) {
                return i;
            }
        }
        return com.example.cs125project.Intensity.UNAVAILABLE;
    }


}
