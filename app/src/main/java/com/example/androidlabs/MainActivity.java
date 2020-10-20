package com.example.androidlabs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    EditText email = findViewById(R.id.editTextTextEmailAddress);
    Button loginButton = findViewById(R.id.button5);
    SharedPreferences prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_profile);

        prefs = getSharedPreferences("FileName", Context.MODE_PRIVATE);
        String savedString = prefs.getString("ReserveName", "Default Value");
        email.setHint(savedString);

        //Creating a transition to load ProfileActivity
        Intent nextActivity = new Intent(this, ProfileActivity.class);
        //activating the button listener to start associated activity
        loginButton.setOnClickListener(click-> startActivity(nextActivity));

        }


    @Override
    protected void onPause() {
        super.onPause();

        //get an editor object
        SharedPreferences.Editor editor = prefs.edit();
        //save what was typed under the name "Reserve Name"
        editor.putString(s: "ReserveName", )
    }
}