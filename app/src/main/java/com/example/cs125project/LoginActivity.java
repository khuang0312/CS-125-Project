package com.example.cs125project;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText Name;
    private EditText Password;
    private TextView Attempts;
    private Button Login;
    private int counter = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Name = findViewById(R.id.etName);
        Password = findViewById(R.id.etPassword);
        Attempts = findViewById(R.id.tvAttempts);
        Login = findViewById(R.id.btnLogin);

        Attempts.setText("No. of attemps remaining: 5");

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate(Name.getText().toString(), Password.getText().toString());
            }
        });
    }

    private void validate(String userName, String userPassword) {
        Log.d("userName, userPassword", userName + userPassword);
        if ( ((userName.equals("a")) && (userPassword.equals("a"))) || ((userName.equals("b")) && (userPassword.equals("b")))) {
            //save username to sharedPref for firebase access later

            SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.username_shared_preference_key), this.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("username", userName);
            editor.apply();

            this.finish();
            Intent myIntent = new Intent(this, MainActivity.class);
            startActivity(myIntent);
        } else {
            counter--;
            Attempts.setText("No. of attempts remaining: " + counter);
            if (counter == 0) {
                Login.setEnabled(false);
            }
        }
    }
}