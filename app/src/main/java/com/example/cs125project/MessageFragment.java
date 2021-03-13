package com.example.cs125project;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.internal.NavigationMenu;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import android.content.SharedPreferences;
import android.widget.TextView;

//TODO: Rename/refactor this fragment and add UI
public class MessageFragment extends Fragment {
    //Global variables
    //Recommendations to be displayed
    // most String and Object variables intialized to avoid NullReferenceException
    String recommendedInterest = "";
    String recommendedIntensity = "";
    int recommendedDuration;
    PointOfInterest recommendedPlace;
    User recommendedUser = new User();
    TreeMap<String, Integer> recommendedUsers;

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
    String username = "";
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

                // access map menu
                // disable or enable toggle options in map menu...
                // replace the images and toggle option text





                //Feeds user report info to recommendation methods

                recommendedInterest = Recommendation.getRecommendedInterest(lastInterests);
                recommendedIntensity = Recommendation.getRecommendedIntensity(lastIntensities);
                recommendedDuration = Recommendation.getRecommendedDuration(lastDurations);
                interestForScoring.add(recommendedInterest);

                if (recommendedInterest.equals(Recommendation.DEFAULT_VALUE) ||
                recommendedUser.equals(Recommendation.DEFAULT_VALUE) || recommendedDuration == 0) {
                    mActivity.setText(Recommendation.DEFAULT_VALUE + ": Submit some reports!");
                } else {
                    mActivity.setText(recommendedInterest + " at " + recommendedIntensity + " intensity for " + recommendedDuration + " minutes");
                }



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




        //Finds a User to match the current user based on proximity and the activity recommended
        Query orderedUsers = dbRef.child("users").orderByKey();
        orderedUsers.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                recommendedUsers = new TreeMap<String, Integer>(); // resets RecommendedUser, allows to account for if there are no other users to recommend

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user2 = snapshot.getValue(User.class);
                    //Skip if the iterated user is the current user
                    if (username.equals(user2.getUsername())){
                        continue;
                    }
                    Log.d("user1 interests", interestForScoring.toString());
                    Log.d("user1 coords", Double.toString(userLat) + " "
                            + Double.toString(userLong) + " " + Double.toString(user2.getLatitude()) + " " + Double.toString(user2.getLongitude()));

                    int user2Score = Recommendation.locationScore(userLat, userLong, user2.getLatitude(), user2.getLongitude())
                     + Recommendation.interestsScore(interestForScoring, user2.getInterests());
                    Log.d("Score", Integer.toString(user2Score));
                    recommendedUsers.put(user2.getUsername(), user2Score);
                }
                if (recommendedUsers.size() == 0) {
                    recommendedUsers.put(Recommendation.DEFAULT_VALUE, 0);
                }


                LinkedHashMap<String, Integer> sortedUsers = new LinkedHashMap<String, Integer>();

                recommendedUsers.entrySet()
                        .stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .sorted(Map.Entry.comparingByKey())
                        .forEachOrdered(x -> sortedUsers.put(x.getKey(), x.getValue()));

                // iterate through stream and set text to name and score... in RecyclerView!
                String usersListString = new String();
                for (Map.Entry<String, Integer> user : sortedUsers.entrySet()) {
                    usersListString += user.getKey() + ", Score: " + user.getValue() + "\n";
                }


                mUser.setText(usersListString);
                mUser.setMovementMethod(new ScrollingMovementMethod());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("onCancelled", "triggered in the event that this listener either failed at the server, or is removed as a result of the security and Firebase Database rules.");
            }
        });



        return view;
    }
}
