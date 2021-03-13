package com.example.cs125project;

import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

public class MapsFragment extends Fragment {
    String username;
    double userLat = 0;
    double userLong = 0;
    GoogleMap mMap;
    Button interestSelect;
    Interest currentInterest = Interest.Walking;
    HashSet<String> userInterests;
    ArrayList<String> userInterestsList;
    int currentInterestIndex;
    LatLng userLoc;

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            Places.initialize(getActivity().getApplicationContext(), "AIzaSyDmgABoOuT2Fy_LEq-QEHK9T1y3Ff6NPxQ");
            PlacesClient placesClient = Places.createClient(getActivity().getApplicationContext());
            userInterests = new HashSet<String>();
            userInterestsList = new ArrayList<String>();

            mMap = googleMap;
            mMap.moveCamera(CameraUpdateFactory.zoomTo(10));
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            //get username through shared preferences, check if it already exists in firebase
            SharedPreferences sharedPref = getContext().getSharedPreferences(getString(R.string.username_shared_preference_key), getContext().MODE_PRIVATE);
            username = sharedPref.getString("username", "noUsername");
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
                        Log.d("MapsFragment", Double.toString(userLat) + ", " + Double.toString(userLong));
                        userLoc = new LatLng(userLat, userLong);

                        //make http request of relevant locations nearby

                        mMap.addMarker(new MarkerOptions()
                                .position(userLoc)
                                .title("You are here!")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(userLoc));

                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.d("onCancelled", "triggered in the event that this listener either failed at the server, or is removed as a result of the security and Firebase Database rules.");
                }
            });
            dbRef.child("users").child(username).child("locations").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.'

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        //snapshot.getKey(); = name of location
                        if (snapshot.getKey().equals("count")) {
                            continue;
                        }
                        PointOfInterest poi = snapshot.getValue(PointOfInterest.class);
                        LatLng location = new LatLng(poi.getLatitude(), poi.getLongitude());
                        int numInterests = snapshot.child("interests").child("count").getValue(Integer.class);
                        for (int i = 0; i < numInterests; i++) {
                            String interest = snapshot.child("interests").child(Integer.toString(i+1)).getValue(String.class);
                            userInterests.add(interest);
                        }
                        //make http request of relevant locations nearby

                        // only display the interest currently toggled
                        Bitmap bMap;
                        String initialDisplayInterest = snapshot.child("interests").child("1").getValue(String.class);
                        switch (initialDisplayInterest){
                            case ("Walking"):
                                bMap = BitmapFactory.decodeResource(getResources(), R.drawable.walking);
                                break;
                            case ("Running"):
                                bMap = BitmapFactory.decodeResource(getResources(), R.drawable.running);
                                break;
                            case ("Swimming"):
                                bMap = BitmapFactory.decodeResource(getResources(), R.drawable.swimming);
                                break;
                            case ("Climbing"):
                                bMap = BitmapFactory.decodeResource(getResources(), R.drawable.climbing);
                                break;
                            case ("Yoga"):
                                bMap = BitmapFactory.decodeResource(getResources(), R.drawable.yoga);
                                break;
                            case ("Badminton"):
                                bMap = BitmapFactory.decodeResource(getResources(), R.drawable.badminton);
                                break;
                            case ("Hockey"):
                                bMap = BitmapFactory.decodeResource(getResources(), R.drawable.hockey);
                                break;
                            case ("Tennis"):
                                bMap = BitmapFactory.decodeResource(getResources(), R.drawable.tennis);
                                break;
                            case ("Basketball"):
                                bMap = BitmapFactory.decodeResource(getResources(), R.drawable.basketball);
                                break;
                            case ("Soccer"):
                                bMap = BitmapFactory.decodeResource(getResources(), R.drawable.soccer);
                                break;
                            case ("Football"):
                                bMap = BitmapFactory.decodeResource(getResources(), R.drawable.football);
                                break;
                            case ("Baseball"):
                                bMap = BitmapFactory.decodeResource(getResources(), R.drawable.baseball);
                                break;
                            case ("Golf"):
                                bMap = BitmapFactory.decodeResource(getResources(), R.drawable.golf);
                                break;
                            case ("Pilates"):
                                bMap = BitmapFactory.decodeResource(getResources(), R.drawable.pilates);
                                break;
                            case ("Parkour"):
                                bMap = BitmapFactory.decodeResource(getResources(), R.drawable.parkour);
                                break;
                            case ("Dancing"):
                                bMap = BitmapFactory.decodeResource(getResources(), R.drawable.dancing);
                                break;
                            case ("Lacrosse"):
                                bMap = BitmapFactory.decodeResource(getResources(), R.drawable.lacrosse);
                                break;
                            case ("Wrestling"):
                                bMap = BitmapFactory.decodeResource(getResources(), R.drawable.wrestling);
                                break;
                            case ("MMA"):
                                bMap = BitmapFactory.decodeResource(getResources(), R.drawable.mma);
                                break;
                            default:
                                bMap = BitmapFactory.decodeResource(getResources(), R.drawable.mma);
                        }

                        mMap.addMarker(new MarkerOptions().position(location).title(poi.getName()).icon(BitmapDescriptorFactory.fromBitmap(bMap)));
                    }
                    userInterestsList = new ArrayList<String>();
                    userInterestsList.add("ALL");
                    for (String s : userInterests) {
                        Log.d("userINterests contains", Integer.toString(userInterests.size()));

                        userInterestsList.add(s);
                    }

                }
                @Override
                public void onCancelled(DatabaseError error) {
                    Log.d("onCancelled", "triggered in the event that this listener either failed at the server, or is removed as a result of the security and Firebase Database rules.");
                }
            });

        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        currentInterestIndex = 0;
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        interestSelect = (Button)view.findViewById(R.id.interestToggle);
        interestSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("onClick", Integer.toString(userInterestsList.size()));
                for (String s : userInterestsList) {
                    Log.d("onClick", s);
                }
                mMap.clear();
                mMap.addMarker(new MarkerOptions()
                        .position(userLoc)
                        .title("You are here!")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                currentInterestIndex = (currentInterestIndex  + 1);
                if (currentInterestIndex == userInterestsList.size()) {
                    currentInterestIndex = 0;
                }
                Log.d("onClickIndex", Integer.toString(currentInterestIndex));
                interestSelect.setText(userInterestsList.get(currentInterestIndex));
                final String cInterest = interestSelect.getText().toString();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference dbRef = database.getReference();
                //update markers to only show those with relevant interest
                dbRef.child("users").child(username).child("locations").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.'

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            //snapshot.getKey(); = name of location
                            if (snapshot.getKey().equals("count")) {
                                continue;
                            }
                            PointOfInterest poi = snapshot.getValue(PointOfInterest.class);
                            LatLng location = new LatLng(poi.getLatitude(), poi.getLongitude());
                            int numInterests = snapshot.child("interests").child("count").getValue(Integer.class);
                            Bitmap bMap;

                            for (int i = 0; i < numInterests; i++) {
                                String interest = snapshot.child("interests").child(Integer.toString(i+1)).getValue(String.class);

                                if (cInterest.equals(interest) || cInterest == "ALL") {
                                    switch (interest){
                                        case ("Walking"):
                                            bMap = BitmapFactory.decodeResource(getResources(), R.drawable.walking);
                                            break;
                                        case ("Running"):
                                            bMap = BitmapFactory.decodeResource(getResources(), R.drawable.running);
                                            break;
                                        case ("Swimming"):
                                            bMap = BitmapFactory.decodeResource(getResources(), R.drawable.swimming);
                                            break;
                                        case ("Climbing"):
                                            bMap = BitmapFactory.decodeResource(getResources(), R.drawable.climbing);
                                            break;
                                        case ("Yoga"):
                                            bMap = BitmapFactory.decodeResource(getResources(), R.drawable.yoga);
                                            break;
                                        case ("Badminton"):
                                            bMap = BitmapFactory.decodeResource(getResources(), R.drawable.badminton);
                                            break;
                                        case ("Hockey"):
                                            bMap = BitmapFactory.decodeResource(getResources(), R.drawable.hockey);
                                            break;
                                        case ("Tennis"):
                                            bMap = BitmapFactory.decodeResource(getResources(), R.drawable.tennis);
                                            break;
                                        case ("Basketball"):
                                            bMap = BitmapFactory.decodeResource(getResources(), R.drawable.basketball);
                                            break;
                                        case ("Soccer"):
                                            bMap = BitmapFactory.decodeResource(getResources(), R.drawable.soccer);
                                            break;
                                        case ("Football"):
                                            bMap = BitmapFactory.decodeResource(getResources(), R.drawable.football);
                                            break;
                                        case ("Baseball"):
                                            bMap = BitmapFactory.decodeResource(getResources(), R.drawable.baseball);
                                            break;
                                        case ("Golf"):
                                            bMap = BitmapFactory.decodeResource(getResources(), R.drawable.golf);
                                            break;
                                        case ("Pilates"):
                                            bMap = BitmapFactory.decodeResource(getResources(), R.drawable.pilates);
                                            break;
                                        case ("Parkour"):
                                            bMap = BitmapFactory.decodeResource(getResources(), R.drawable.parkour);
                                            break;
                                        case ("Dancing"):
                                            bMap = BitmapFactory.decodeResource(getResources(), R.drawable.dancing);
                                            break;
                                        case ("Lacrosse"):
                                            bMap = BitmapFactory.decodeResource(getResources(), R.drawable.lacrosse);
                                            break;
                                        case ("Wrestling"):
                                            bMap = BitmapFactory.decodeResource(getResources(), R.drawable.wrestling);
                                            break;
                                        case ("MMA"):
                                            bMap = BitmapFactory.decodeResource(getResources(), R.drawable.mma);
                                            break;
                                        default:
                                            bMap = BitmapFactory.decodeResource(getResources(), R.drawable.mma);
                                    }
                                    mMap.addMarker(new MarkerOptions().position(location).title(poi.getName()).icon(BitmapDescriptorFactory.fromBitmap(bMap)));
                                    break;
                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.d("onCancelled", "triggered in the event that this listener either failed at the server, or is removed as a result of the security and Firebase Database rules.");
                    }
                });
            }
        });



        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //get username through shared preferences, check if it already exists in firebase
        SharedPreferences sharedPref = view.getContext().getSharedPreferences(getString(R.string.username_shared_preference_key), view.getContext().MODE_PRIVATE);
        username = sharedPref.getString("username", "noUsername");
        DatabaseReference dbRef = database.getReference();
        dbRef.child("users").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
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

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

}