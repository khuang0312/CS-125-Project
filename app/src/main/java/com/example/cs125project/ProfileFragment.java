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
    String username = "";
    User currentUser;

    // global variables to save selected profile location
    double latitude;
    double longitude;

    String address = "";
    String city = "";
    String state = "";
    String country = "";

    TextView locationDisplay;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        SharedPreferences sharedPref = getContext().getSharedPreferences(getString(R.string.username_shared_preference_key), Context.MODE_PRIVATE);
        username = sharedPref.getString("username", null);

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
        locationDisplay = (TextView) view.findViewById(R.id.location_display);
        // set up for Places API
        Places.initialize(getActivity().getApplicationContext(), "AIzaSyDmgABoOuT2Fy_LEq-QEHK9T1y3Ff6NPxQ");
        PlacesClient placesClient = Places.createClient(getActivity().getApplicationContext());
        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.profile_location);

        // autocompleteFragment.setTypeFilter(TypeFilter.ADDRESS);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ADDRESS, Place.Field.ADDRESS_COMPONENTS, Place.Field.LAT_LNG));
        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NotNull Place place) {
                // Saves info to global variables...
                address = place.getAddress();
                latitude = place.getLatLng().latitude;
                longitude = place.getLatLng().longitude;

                List<AddressComponent> addrComps = place.getAddressComponents().asList();
                for (AddressComponent ac : addrComps) {
                    if (ac.getTypes().contains("country")) {
                        country = ac.getName();
                    } else if (ac.getTypes().contains("administrative_area_level_1")) {
                        state = ac.getName();
                    } else if (ac.getTypes().contains("locality")) {
                        city = ac.getName();
                    }
                }


                locationDisplay.setText(address);

                Log.i("PLACE_SELECT", "Place: " + address);
            }
            @Override
            public void onError(@NotNull Status status) {
                // TODO: Handle the error.
                Log.i("PLACE_ERR", "An error occurred: " + status);
            }
        });



        // Update text fields if corresponding user entry
        // has changed in database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //get username through shared preferences, check if it already exists in firebase
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
                    String locationData;
                    address = user.getAddress();
                    state = user.getState();
                    country = user.getCountry();
                    latitude = user.getLatitude();
                    longitude = user.getLongitude();

                    // address include city, country, and state if provided
                    locationDisplay.setText(address);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("onCancelled", "triggered in the event that this listener either failed at the server, or is removed as a result of the security and Firebase Database rules.");
            }
        });




        // puts a listener on the "Submit" button
        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* When the "Submit" button is clicked, save user changes
                to their profile in Firebase
                 */
                User updatedUser = new User();

                EditText name = view.findViewById(R.id.profile_name);
                EditText email = view.findViewById(R.id.profile_email);
                EditText age = view.findViewById(R.id.profile_age);
                TextView location = view.findViewById(R.id.location_display);

                updatedUser.setUsername(username);
                updatedUser.setName(name.getText().toString());
                updatedUser.setEmail(email.getText().toString());
                updatedUser.setAge(age.getText().toString());
                updatedUser.setAddress(address);
                updatedUser.setState(state);
                updatedUser.setCountry(country);
                updatedUser.setLatitude(latitude);
                updatedUser.setLongitude(longitude);

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference dbRef = database.getReference();
                dbRef.child("users").child(username).setValue(updatedUser);
            }
        });
        Log.d("ProfileFragment", "End initialization");
        return view;

    }
}

