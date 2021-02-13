package com.example.cs125project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

        Name = (EditText) findViewById(R.id.etName);
        Password =  (EditText) findViewById(R.id.etPassword);
        Attempts = (TextView) findViewById(R.id.tvAttempts);
        Login = (Button) findViewById(R.id.btnLogin);

        Attempts.setText("No. of attemps remaining: 5" );

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate(Name.getText().toString(), Password.getText().toString());
            }
        });
    }

    private void validate(String userName, String userPassword)
    {
        Log.d("userName, userPassword", userName + userPassword);
        if((userName.equals("a")) && (userPassword.equals("a"))) {
            Intent myIntent = new Intent(this, MainActivity.class);
            startActivity(myIntent);
        } else {
            counter--;
            Attempts.setText("No. of attemps remaining: " + Integer.toString(counter));
            if (counter == 0) {
                Login.setEnabled(false);
            }
        }
    }
}