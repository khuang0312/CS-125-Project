package com.example.cs125project;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import android.content.SharedPreferences;
import android.widget.TextView;

//TODO: Rename/refactor this fragment and add UI
public class MessageFragment extends Fragment {
    //Global variables
    //Recommendations to be displayed
    String recommendedInterest;
    String recommendedIntensity;
    int recommendedDuration;
    PointOfInterest recommendedPlace;
    User recommendedUser;

    //Used for getting info to inform activity recommendation
    ArrayList<String> lastInterests = new ArrayList<String>();
    ArrayList<String> lastIntensities  = new ArrayList<String>();
    ArrayList<Integer> lastDurations  = new ArrayList<Integer>();

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

    private TextView mActivity;
    private TextView mPlace;
    private TextView mUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_message, container, false);


        //Initialize database and current user
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        //Comment out to connect to the real database
        //database.useEmulator("10.0.2.2", 9000);
        DatabaseReference dbRef = database.getReference();
        SharedPreferences sharedPref = view.getContext().getSharedPreferences(getString(R.string.username_shared_preference_key), view.getContext().MODE_PRIVATE);
        username = sharedPref.getString("username", "noUsername");
        mActivity = (TextView)view.findViewById(R.id.activity);
        mPlace = (TextView)view.findViewById(R.id.place);
        mUser = (TextView)view.findViewById(R.id.rec_user);

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
        Query orderLogs = dbRef.child("users").child(username).child("reports").orderByKey().limitToLast(7);
        orderLogs.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                Log.i("REPORTS", "Children in this snapshot: " + dataSnapshot.getChildrenCount());
                lastIntensities = new ArrayList<String>();
                lastInterests = new ArrayList<String>();
                lastDurations = new ArrayList<Integer>();


                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if (snapshot.getKey().equals("count")) {
                        continue;
                    }
                    Report report = snapshot.getValue(Report.class);
                    lastIntensities.add(report.getIntensity());
                    lastInterests.add(report.getCategory());
                    lastDurations.add(report.getMinutesElapsed() + report.getHoursElapsed()*60);

                }

                //Feeds user report info to recommendation methods

                recommendedInterest = Recommendation.getRecommendedInterest(lastInterests);
                recommendedIntensity = Recommendation.getRecommendedIntensity(lastIntensities);
                recommendedDuration = Recommendation.getRecommendedDuration(lastDurations);
                interestForScoring.add(recommendedInterest);
                mActivity.setText(recommendedInterest + " at " + recommendedIntensity + " intensity for " + recommendedDuration + " minutes");

                // I believe this needs to be moved to the listener
                //Log.i("RECOMMENDATIONS", "Recommended activity: " + recommendedInterest);
                //Log.i("RECOMMENDATIONS", "Recommended intensity: " + recommendedIntensity);
                //Log.i("RECOMMENDATIONS", "Recommended time: " + recommendedDuration + " minutes");

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("onCancelled", "triggered in the event that this listener either failed at the server, or is removed as a result of the security and Firebase Database rules.");
            }
        });


        Query orderPlaces = dbRef.child("users").child(username).child("locations").orderByKey();
        orderPlaces.addListenerForSingleValueEvent((new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String bestPlaceName = "";
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //snapshot.getKey(); = name of location
                    if (snapshot.getKey().equals("count")) {
                        continue;
                    }
                    PointOfInterest poi = snapshot.getValue(PointOfInterest.class);
                    ArrayList<String> poiInterest = new ArrayList<String>();
                    poiInterest.add(poi.getInterest());
                    currPlaceScore = 0;
                    currPlaceScore += Recommendation.locationScore(userLat, userLong, poi.getLatitude(), poi.getLongitude());
                    currPlaceScore += Recommendation.interestsScore(interestForScoring, poiInterest);
                    if (bestPlaceScore < currPlaceScore){
                        bestPlaceScore = currPlaceScore;
                        recommendedPlace = poi;
                        bestPlaceName = snapshot.getKey();
                    }
                }
                if (!bestPlaceName.equals("")){
                    mPlace.setText(bestPlaceName);
                    //Log.i("RECOMMENDATIONS", "Recommended place: " + bestPlaceName);
                } else {
                    mPlace.setText("NO PLACE RECOMMENDED");
                    //Log.i("RECOMMENDATIONS", "Recommended place: " + "NO PLACE RECOMMENDED");
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("onCancelled", "triggered in the event that this listener either failed at the server, or is removed as a result of the security and Firebase Database rules.");
            }
        }));

        //Finds a User to match the current user based on proximity and the activity recommended
        Query orderedUsers = dbRef.child("users").orderByKey();
        orderedUsers.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                recommendedUser = null; // resets RecommendedUser, allows to account for if there are no other users to recommend
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user2 = snapshot.getValue(User.class);
                    //Skip if the iterated user is the current user
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

                if (recommendedUser != null) {
                    mUser.setText(recommendedUser.getUsername());
                    //Log.i("RECOMMENDATIONS", "Recommended user: " + recommendedUser.getUsername());
                } else {
                    mUser.setText("NO USER RECOMMENDED");
                    //Log.i("RECOMMENDATIONS", "Recommended user: " + "NO USER RECOMMENDED");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("onCancelled", "triggered in the event that this listener either failed at the server, or is removed as a result of the security and Firebase Database rules.");
            }
        });



        return view;
    }
}
