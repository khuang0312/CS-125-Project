package com.example.cs125project;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import android.content.SharedPreferences;

//TODO: Rename/refactor this fragment and add UI
public class MessageFragment extends Fragment {
    //Global variables
    //Recommendations to be displayed
    Interest recommendedInterest;
    int recommendedIntensity;
    int recommendedDuration;
    Place recommendedPlace;
    User recommendedUser;

    //Used for getting info to inform activity recommendation
    ArrayList<String> lastInterests;
    ArrayList<Integer> lastIntensities;
    ArrayList<Integer> lastDurations;

    //Used for informing Place and User recommendations
    ArrayList<String> interestForScoring = new ArrayList<String>();
    int bestPlaceScore = 0;
    int currPlaceScore = 0;
    int bestUserScore = 0;
    int currUserScore = 0;

    //User's info, used for informing recommendations
    String username;
    double userLat;
    double userLong;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_message, container, false);


        //Initialize database and current user
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //Comment out to connect to the real database
        //database.useEmulator("10.0.2.2", 9000);
        DatabaseReference dbRef = database.getReference();
        SharedPreferences sharedPref = view.getContext().getSharedPreferences(getString(R.string.username_shared_preference_key), view.getContext().MODE_PRIVATE);
        username = sharedPref.getString("username", "noUsername");

        //Only used to get the user's latitude and longitude
        dbRef.child("users").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    userLat = user.getLatitude();
                    userLong = user.getLongitude();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("onCancelled", "triggered in the event that this listener either failed at the server, or is removed as a result of the security and Firebase Database rules.");
            }
        });

        //Gathers info about the user's reports
        Query orderLogs = dbRef.child("reports").orderByKey();
        orderLogs.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                Log.i("REPORTS", "Children in this snapshot: " + dataSnapshot.getChildrenCount());
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Report report = snapshot.getValue(Report.class);
                    switch (report.getIntensity()) {
                        case "L":
                            lastIntensities.add(1);
                            break;
                        case "M":
                            lastIntensities.add(2);
                            break;
                        case "H":
                            lastIntensities.add(3);
                            break;
                    }
                    lastInterests.add(report.getCategory());
                    lastDurations.add(report.getMin() + report.getHrs()*60);
                    //Log.i("REPORT INFO", "Report processed: " + report.getSubmissionTime());
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("onCancelled", "triggered in the event that this listener either failed at the server, or is removed as a result of the security and Firebase Database rules.");
            }
        });

        //Feeds user report info to recommendation methods
        recommendedInterest = Recommendation.getRecommendedInterest(lastInterests, 3);
        recommendedIntensity = Recommendation.getRecommendedIntensity(lastIntensities, 3);
        recommendedDuration = Recommendation.getRecommendedDuration(lastDurations, 3);
        interestForScoring.add(Interest.getString(recommendedInterest));

        //Finds a Place to recommend the user based on proximity and the activity they were recommended
        Query orderedPlaces = dbRef.child("places").orderByKey();
        orderedPlaces.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Place place = snapshot.getValue(Place.class);
                    currPlaceScore = 0;
                    currPlaceScore += Recommendation.locationScore(userLat, userLong, place.getLatitude(), place.getLongitude());
                    currPlaceScore += Recommendation.interestsScore(interestForScoring, place.getInterests());
                    if (bestPlaceScore < currPlaceScore) {
                        bestPlaceScore = currPlaceScore;
                        recommendedPlace = place;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("onCancelled", "triggered in the event that this listener either failed at the server, or is removed as a result of the security and Firebase Database rules.");
            }
        });

        //Finds a User to match the current user based on proximity and the activity recommended
        Query orderedUsers = dbRef.child("users").orderByKey();
        orderedUsers.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user2 = snapshot.getValue(User.class);
                    //Skip if database user is the current user
                    if (username.equals(user2.getUsername())){
                        continue;
                    }
                    currUserScore = 0;
                    currUserScore += Recommendation.locationScore(userLat, userLong, user2.getLatitude(), user2.getLongitude());
                    currUserScore += Recommendation.interestsScore(interestForScoring, user2.getInterests());
                    if (bestUserScore < currUserScore) {
                        bestUserScore = currUserScore;
                        recommendedUser = user2;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("onCancelled", "triggered in the event that this listener either failed at the server, or is removed as a result of the security and Firebase Database rules.");
            }
        });
        Log.i("RECOMMENDATIONS", "Recommended " + Interest.getString(recommendedInterest) + " at " + recommendedIntensity + " intensity for " + recommendedDuration + "minutes");
        Log.i("USER AND PLACE",  "Place " + recommendedPlace.getName() + " with " + recommendedUser.getUsername());

        return view;
    }
}
