package com.example.cs125project;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class MapsFragment extends Fragment {
    String username;
    String icon_str = "";
    double userLat = 0;
    double userLong = 0;
    GoogleMap mMap;
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
            mMap = googleMap;
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
                        LatLng sydney = new LatLng(userLat, userLong);

                        //make http request of relevant locations nearby

                        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

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
                    // whenever data at this location is updated.
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        //snapshot.getKey(); = name of location
                        if (snapshot.getKey().equals("count")) {
                            continue;
                        }
                        PointOfInterest poi = snapshot.getValue(PointOfInterest.class);
                        LatLng location = new LatLng(poi.getLatitude(), poi.getLongitude());

                        //make http request of relevant locations nearby
                        switch (poi.getInterest()){
                            case ("Walking"):
                                icon_str = "baseline_directions_walk_black_18dp.png";
                                break;
                            case ("Running"):
                                icon_str = "directions_run-24px.svg";
                                break;
                            case ("Swimming"):
                                icon_str = "pool-24px.svg";
                                break;
                            case ("Climbing"):
                                icon_str = "hiking-24px.svg";
                                break;
                            case ("Yoga"):
                                icon_str = "self_improvement-24px.svg";
                                break;
                            case ("Badminton"):
                                icon_str = "sports_handball-24px.svg";
                                break;
                            case ("Hockey"):
                                icon_str = "sports_hockey-24px.svg";
                                break;
                            case ("Tennis"):
                                icon_str = "sports_tennis-24px.svg";
                                break;
                            case ("Basketball"):
                                icon_str = "sports_basketball-24px.svg";
                                break;
                            case ("Soccer"):
                                icon_str = "sports_soccer-24px.svg";
                                break;
                            case ("Football"):
                                icon_str = "sports_football-24px.svg";
                                break;
                            case ("Baseball"):
                                icon_str = "sports_baseball-24px.svg";
                                break;
                            case ("Golf"):
                                icon_str = "sports_golf-24px.svg";
                                break;
                            case ("Pilates"):
                                icon_str = "fitness_center-24px.svg";
                                break;
                            case ("Parkour"):
                                icon_str = "domain-24px.svg";
                                break;
                            case ("Dancing"):
                                icon_str = "nightlife-24px.svg";
                                break;
                            case ("Lacrosse"):
                                icon_str = "sports_cricket-24px.svg";
                                break;
                            case ("Wrestling"):
                                icon_str = "sports_kabaddi-24px.svg";
                                break;
                            case ("MMA"):
                                icon_str = "sports_mma-24px.svg";
                                break;
                        }

                        mMap.addMarker(new MarkerOptions().position(location).title(snapshot.getKey()).icon(BitmapDescriptorFactory.fromAsset(icon_str)));
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
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
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