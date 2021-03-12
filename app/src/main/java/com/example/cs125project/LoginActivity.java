package com.example.cs125project;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    //    private EditText Name;
//    private EditText Password;
//    private TextView Attempts;
//    private Button Login;
//    private Button CreateAccount;
//    private ImageView image;
    private int counter = 5;
    ViewGroup sceneRoot;
    Scene LoginScene;
    Scene CreateAccountScene;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_scene);
        sceneRoot = (ViewGroup) findViewById(R.id.login_frame_layout);
        LoginScene = Scene.getSceneForLayout(sceneRoot, R.layout.login_scene, getApplicationContext());
        CreateAccountScene = Scene.getSceneForLayout(sceneRoot, R.layout.create_account_scene, getApplicationContext());
//        Name = findViewById(R.id.etName);
//        Password = findViewById(R.id.etPassword);
//        Attempts = findViewById(R.id.tvAttempts);
//        Login = findViewById(R.id.btnLogin);
//        image = findViewById(R.id.imageView);
//        Attempts.setText("No. of attemps remaining: 5");
//        CreateAccount.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                           }
//        });
//        image.setImageResource(R.drawable.download);
//        Login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                validate(Name.getText().toString(), Password.getText().toString());
//            }
//        });
    }

    private void setLoginScene() {
        Transition trans = new AutoTransition();
        TransitionManager.go(LoginScene, trans);
        EditText Name = findViewById(R.id.etName);
        EditText Password = findViewById(R.id.etPassword);
        TextView Attempts = findViewById(R.id.tvAttempts);
        Button Login = findViewById(R.id.btnLogin);
        Button createAccount = findViewById(R.id.btnCreateAccount);
        Attempts.setText("No. of attemps remaining: 5");
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCreateAccountScene();
            }
        });
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText Name = findViewById(R.id.etName);
                EditText Password = findViewById(R.id.etPassword);
                validate(Name.getText().toString(), Password.getText().toString());
            }
        });
    }

    private void setCreateAccountScene() {
        final EditText etName = findViewById(R.id.etName);
        final EditText etPassword1 = findViewById(R.id.etPassword);
        final EditText etPassword2 = findViewById(R.id.etPassword2);
        final Button btnCreateAccount = findViewById(R.id.btnLogin);
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etName.getText().toString().matches("")
                        || etPassword1.getText().toString().matches("")
                        || etPassword2.getText().toString().matches("")) {
                    return;
                } else {
                    //if passwords match, create new user
                    if (etPassword1.getText().toString() == etPassword2.getText().toString()) {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference dbRef = database.getReference();
                        //locations: key is name, value is pair <double latitude, double longitude>
                        dbRef.child("users").child(etName.getText().toString()).child("password").setValue(etPassword2.getText().toString());
                        SharedPreferences sharedPref = getApplication().getSharedPreferences(getString(R.string.username_shared_preference_key), getApplication().MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("username", etName.getText().toString());
                        editor.apply();

                        Intent myIntent = new Intent(getApplication(), MainActivity.class);
                        startActivity(myIntent);
                    }
                }
            }
        });
    }

    private void validate(final String userName, final String userPassword) {
        final TextView Attempts = findViewById(R.id.tvAttempts);
        final Button Login = findViewById(R.id.btnLogin);
        Log.d("userName, userPassword", userName + userPassword);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReference();
        dbRef.child("users").child(userName).child("password").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String password = dataSnapshot.getValue(String.class);
                if (password != null) {
                    if (password == userPassword) {
                        SharedPreferences sharedPref = getApplication().getSharedPreferences(getString(R.string.username_shared_preference_key), getApplication().MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("username", userName);
                        editor.apply();

                        finish();
                        Intent myIntent = new Intent(getApplication(), MainActivity.class);
                        startActivity(myIntent);
                    }
                }
                //else, data not found or does not match...
                counter--;
                Attempts.setText("No. of attempts remaining: " + counter);
                if (counter == 0) {
                    Login.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("onCancelled", "triggered in the event that this listener either failed at the server, or is removed as a result of the security and Firebase Database rules.");
            }
        });

    }
}