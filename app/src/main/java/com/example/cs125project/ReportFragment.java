package com.example.cs125project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ReportFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view =  inflater.inflate(R.layout.fragment_report, container, false);
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
//        Button saveData = (Button) view.findViewById(R.id.save_button);
//        saveData.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //save users changes to profile to firebase
//                //Map<String, User> userMap = new HashMap<>();
//                Report newReport = new Report();
//                FirebaseDatabase database = FirebaseDatabase.getInstance();
//                DatabaseReference dbRef = database.getReference();
//                EditText name = view.findViewById(R.id.profile_name);
//                EditText email = view.findViewById(R.id.profile_email);
//                EditText age = view.findViewById(R.id.profile_age);
//                EditText location = view.findViewById(R.id.profile_location);
//                updateUser.setName(name.getText().toString());
//                updateUser.setEmail(email.getText().toString());
//                updateUser.setAge(age.getText().toString());
//                String[] cityState = location.getText().toString().split(", ");
//                updateUser.setAddress(cityState[0]);
//                updateUser.setCity(cityState[1]);
//                updateUser.setState(cityState[2]);
//
//
//                dbRef.child("users").child(username).setValue(updateUser);
//                //dbRef.setValue(userMap);
//
//
//            }
//        });
        return view;
    }
    //exercise time spent, exercise level, exercise activity category
}
