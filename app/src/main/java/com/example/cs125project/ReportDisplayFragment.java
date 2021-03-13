package com.example.cs125project;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

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

public class ReportDisplayFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_report_display, container, false);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        //Comment out to connect to the real database
        //database.useEmulator("10.0.2.2", 9000);
        Log.d("ReportDisplayFragment", "fragment launched");
        DatabaseReference dbRef = database.getReference();
        SharedPreferences sharedPref = view.getContext().getSharedPreferences(getString(R.string.username_shared_preference_key), view.getContext().MODE_PRIVATE);
        String username = sharedPref.getString("username", "noUsername");


        dbRef.child("users").child(username).child("reports").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.'
                final LinearLayout linearLayout = view.findViewById(R.id.report_display_linear_layout);
                try {
                    int reportCount = dataSnapshot.child("count").getValue(Integer.class);
                }
                catch (NullPointerException e) {
                    return;
                }
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //snapshot.getKey(); = name of location
                    if (!snapshot.getKey().equals("count")) {
                        TextView tvReport = new TextView(view.getContext());
                        String reportText = "Report: " + snapshot.getKey() +
                                "\nCategory: " + snapshot.child("category").getValue(String.class) +
                                "\nIntensity " + snapshot.child("intensity").getValue(String.class) +
                                "\nDate " + snapshot.child("dayReported").getValue(Integer.class) + "/" + snapshot.child("monthReported").getValue(Integer.class) + "/" + snapshot.child("yearReported").getValue(Integer.class) + "\n";
                        tvReport.setText(reportText);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.setMargins(0, 0, 0, 0);
                        params.gravity = Gravity.CENTER;
                        tvReport.setLayoutParams(params);
                        linearLayout.addView(tvReport);
                        Log.d("ReportDisplayFragment", "view added");
                    }
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
