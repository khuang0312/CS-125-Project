package com.example.cs125project;

import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AddressComponent;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Calendar;
import java.util.Set;

public class ReportFragment extends Fragment {
    String username;
    long numReports;
    HashSet<String> lastInterests;
    double userLat;
    double userLong;
    String currentKeyword;
    int numLocations;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("ReportFragment", "Begin initialization");
        final View view = inflater.inflate(R.layout.fragment_report, container, false);
        final Button reportIntensityET = view.findViewById(R.id.report_intensity_select);
        final Button reportCategoryET = view.findViewById(R.id.report_category_select);
        final EditText reportHoursET = view.findViewById(R.id.report_duration_hours_edittext);
        final EditText reportMinET = view.findViewById(R.id.report_duration_min_edittext);
        Button saveDataBtn = view.findViewById(R.id.report_save_button);
        final Button category_selection = (Button) view.findViewById(R.id.report_category_select);
        category_selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getActivity().getApplicationContext(), v);
                popup.inflate(R.menu.exercise_category_menu);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        category_selection.setText(item.getTitle().toString());
                        return true;
                    }
                });
                popup.show();
            }
        });
        final Button intensity_selection = (Button) view.findViewById(R.id.report_intensity_select);
        intensity_selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getActivity().getApplicationContext(), v);
                popup.inflate(R.menu.intensity_menu);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        intensity_selection.setText(item.getTitle().toString());
                        return true;
                    }
                });
                popup.show();
            }
        });


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //get username through shared preferences, check if it already exists in firebase
        SharedPreferences sharedPref = view.getContext().getSharedPreferences(getString(R.string.username_shared_preference_key), view.getContext().MODE_PRIVATE);
        username = sharedPref.getString("username", "noUsername");

        //get userLat and userLong for location parse later down
        DatabaseReference dbRef = database.getReference();
        dbRef.child("users").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    Log.d("MapsFragment", "user found");
                    userLat = user.getLatitude();
                    userLong = user.getLongitude();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("onCancelled", "triggered in the event that this listener either failed at the server, or is removed as a result of the security and Firebase Database rules.");
            }
        });

        dbRef.child("users").child(username).child("reports").child("count").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Integer database_num_reports = dataSnapshot.getValue(Integer.class);
                if (database_num_reports != null) {
                    numReports = database_num_reports;
                } else {
                    numReports = 0;
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("onCancelled", "triggered in the event that this listener either failed at the server, or is removed as a result of the security and Firebase Database rules.");
            }
        });




        //email number age
        saveDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Report report = new Report();
                report.setDatetime();
                report.setCategory(reportCategoryET.getText().toString());
                report.setIntensity(reportIntensityET.getText().toString());
                report.setMinutesElapsed(Integer.parseInt(reportMinET.getText().toString()));
                report.setHoursElapsed(Integer.parseInt(reportHoursET.getText().toString()));

                Log.d("DATE", Calendar.getInstance().toString() );


                numReports += 1;
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference dbRef = database.getReference();

                dbRef.child("users").child(username).child("reports").child("count").setValue(numReports);
                dbRef.child("users").child(username).child("reports").child(Long.toString(numReports)).setValue(report);

                //update recommended locations
                //get last 3 reports
                dbRef.child("users").child(username).child("locations").removeValue();
                numLocations = 0;
                Query orderLogs = dbRef.child("users").child(username).child("reports").orderByKey().limitToLast(3);
                orderLogs.addListenerForSingleValueEvent(new ValueEventListener(){
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot){
                        Log.i("REPORTS", "Children in this snapshot: " + dataSnapshot.getChildrenCount());
                        lastInterests = new HashSet<String>();


                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            if (snapshot.getKey().equals("count") ) {
                                continue;
                            }

                            Report report = snapshot.getValue(Report.class);
                            if (lastInterests.contains(report.getCategory())) {
                                continue;
                            } //if interest is new,
                            lastInterests.add(report.getCategory());
                            //queue a json
                            RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());

                            //build request url

                            //This IP, site or mobile application is not authorized to use this API key. Request received from IP address 99.90.74.90, with empty referer
                            //temporarily removed restrictions entirely
                            String key = "key=AIzaSyBcqrPk1ZYNinfBEAcSk47kkdq7HdyI71U";

                            String location = "location="+ userLat + "," + userLong;
                            String radius = "radius=50000";
                            String keyword = "";
                            switch(report.getCategory()) {
                                case ("Walking"):
                                    keyword = "hiking trail";
                                    break;
                                case ("Running"):
                                    keyword = "jogging trail";
                                    break;
                                case ("Swimming"):
                                    keyword = "swimming pool";
                                    break;
                                case ("Climbing"):
                                    keyword = "climbing gym";
                                    break;
                                case ("Yoga"):
                                    keyword = "yoga studio";
                                    break;
                                case ("Badminton"):
                                    keyword = "play badminton here";
                                    break;
                                case ("Hockey"):
                                    keyword = "play hockey here";
                                    break;
                                case ("Tennis"):
                                    keyword = "tennis center court";
                                    break;
                                case ("Basketball"):
                                    keyword = "play basketball here";
                                    break;
                                case ("Soccer"):
                                    keyword = "play soccer here";
                                    break;
                                case ("Football"):
                                    keyword = "play football here";
                                    break;
                                case ("Baseball"):
                                    keyword = "play baseball here";
                                    break;
                                case ("Golf"):
                                    keyword = "play golf here";
                                    break;
                                case ("Pilates"):
                                    keyword = "pilates";
                                    break;
                                case ("Parkour"):
                                    keyword = "parkour school";
                                    break;
                                case ("Dancing"):
                                    keyword = "dance center";
                                    break;
                                case ("Lacrosse"):
                                    keyword = "play lacrosse here";
                                    break;
                                case ("Wrestling"):
                                    keyword = "go wrestling";
                                    break;
                                case ("MMA"):
                                    keyword = "mma gym school";
                                    break;

                            }
                            currentKeyword = report.getCategory();
                            keyword = "keyword=" + keyword;
                            String requestURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" + keyword + "&" + location + "&" + radius + "&" + key;
                            Log.d("Response", requestURL);
                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, requestURL, null, new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.d("Response", response.toString());
                                    //JSONArray array = null;
                                    try {
                                        JSONArray array = response.getJSONArray("results");
                                        for (int i = 0; i < array.length(); i++) {
                                            numLocations += 1;
                                            String placeIDResponse = array.getJSONObject(i).getString("place_id");
                                            Double longResponse = array.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                                            Double latResponse = array.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                                            String nameResponse = array.getJSONObject(i).getString("name");

                                            PointOfInterest poi =  new PointOfInterest();
                                            poi.setLatitude(latResponse);
                                            poi.setLongitude(longResponse);
                                            poi.setInterest(currentKeyword);
                                            poi.setName(nameResponse);

                                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                                            DatabaseReference dbRef = database.getReference();
                                            //locations: key is name, value is pair <double latitude, double longitude>
                                            dbRef.child("users").child(username).child("locations").child("count").setValue(numLocations);
                                            dbRef.child("users").child(username).child("locations").child(placeIDResponse).setValue(poi);
                                            //.getDouble("lat")
//                                            Log.d("ResponseLat", latResponse.toString());
//                                            Log.d("ResponseLong", latResponse.toString());
//                                            Log.d("ResponseName", nameResponse);
                                            //save locations to firebaseref
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                    //Log.d("Response", response.)
                                    //textView.setText("Response: " + response.toString());
                                }
                            }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // TODO: Handle error
                                    Log.d("Response", "failed");
                                }
                            });
                            queue.add(jsonObjectRequest);
                        }
                        //reset values to default
//                        report.setCategory(reportCategoryET.getText().toString());
//                        report.setIntensity(reportIntensityET.getText().toString());
//                        report.setMinutesElapsed(Integer.parseInt(reportMinET.getText().toString()));
//                        report.setHoursElapsed(Integer.parseInt(reportHoursET.getText().toString()));
                        Toast.makeText(getActivity().getApplicationContext(), //Context
                                "Your report has been saved", // Message to display
                                Toast.LENGTH_SHORT // Duration of the message, another possible value is Toast.LENGTH_LONG
                            ).show(); //Finally Show the toast
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.d("onCancelled", "triggered in the event that this listener either failed at the server, or is removed as a result of the security and Firebase Database rules.");
                    }
                });
            }
        });
        Log.d("ProfileFragment", "End initialization");
        return view;
    }
    //exercise time spent, exercise level, exercise activity category
}
