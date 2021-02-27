package com.example.cs125project;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AddressComponent;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileFragment extends Fragment {
    private final Uri selectedImage = null;
    String username;
    User currentUser;
    //Map<String, User> userMap = new HashMap<>();

    // global variables to save selected profile location
    double latitude;
    double longitude;

    String address;
    String country;
    String state;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("ProfileFragment", "Begin initialization");
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ImageButton profileImage = view.findViewById(R.id.profile_avatar);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //upload image using firebase
            }
        });
        Button saveData = view.findViewById(R.id.save_button);

        // set up for Places API
        Places.initialize(getActivity().getApplicationContext(), "AIzaSyDmgABoOuT2Fy_LEq-QEHK9T1y3Ff6NPxQ");
        PlacesClient placesClient = Places.createClient(getActivity().getApplicationContext());
        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.profile_location);

        // autocompleteFragment.setTypeFilter(TypeFilter.CITIES);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ADDRESS, Place.Field.ADDRESS_COMPONENTS, Place.Field.LAT_LNG));
        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NotNull Place place) {
                // Saves info to global variables...
                latitude = place.getLatLng().latitude;
                longitude = place.getLatLng().longitude;

                List<AddressComponent> addrComps = place.getAddressComponents().asList();
                int addrCompsLen = addrComps.size();

                address = place.getAddress();
                state = addrComps.get(addrCompsLen - 3).getName();
                country = addrComps.get(addrCompsLen - 2).getName();

                Log.i("PLACE_SELECT", "Place: " + address + " " + " " + state + " " + country);
            }
            @Override
            public void onError(@NotNull Status status) {
                // TODO: Handle the error.
                Log.i("PLACE_ERR", "An error occurred: " + status);
            }
        });




        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //get username through shared preferences, check if it already exists in firebase
        SharedPreferences sharedPref = view.getContext().getSharedPreferences(getString(R.string.username_shared_preference_key), view.getContext().MODE_PRIVATE);
        username = sharedPref.getString("username", "noUsername");
        Log.d("ProfileFragment", username);
        DatabaseReference dbRef = database.getReference();


        dbRef.child("users").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    Log.d("OnDataChange", "called");
                    EditText name = view.findViewById(R.id.profile_name);
                    name.setText(user.getName());
                    EditText email = view.findViewById(R.id.profile_email);
                    email.setText(user.getEmail());
                    EditText age = view.findViewById(R.id.profile_age);
                    age.setText(user.getAge());

                    // EditText location = view.findViewById(R.id.profile_location);


                    /*
                    EditText location = view.findViewById(R.id.profile_location);
                    String locationData;
                    locationData = user.getAddress() + ", " + user.getCity() + ", " + user.getState();
                    location.setText(locationData);
                    */

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("onCancelled", "triggered in the event that this listener either failed at the server, or is removed as a result of the security and Firebase Database rules.");
            }
        });




        //email number age
        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //save users changes to profile to firebase
                //Map<String, User> userMap = new HashMap<>();
                User updateUser = new User();

                EditText name = view.findViewById(R.id.profile_name);
                EditText email = view.findViewById(R.id.profile_email);
                EditText age = view.findViewById(R.id.profile_age);

                /*
                EditText location = view.findViewById(R.id.profile_location);
                updateUser.setName(name.getText().toString());
                updateUser.setEmail(email.getText().toString());
                updateUser.setAge(age.getText().toString());
                String[] cityState = location.getText().toString().split(", ");
                updateUser.setAddress(cityState[0]);
                updateUser.setCity(cityState[1]);
                updateUser.setState(cityState[2]);
                */


                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference dbRef = database.getReference();
                dbRef.child("users").child(username).setValue(updateUser);
                // dbRef.setValue(userMap);


            }
        });
        Log.d("ProfileFragment", "End initialization");
        return view;

    }
}

