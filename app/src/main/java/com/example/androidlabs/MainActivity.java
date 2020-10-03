package com.example.androidlabs;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_linear);

        Button btn = findViewById(R.id.button2); //CHANGE THIS EVERY COMMIT FOR EACH LAYOUT
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.toast_message), Toast.LENGTH_LONG).show();
            }
        });

        Switch swt = findViewById(R.id.switch1); //CHANGE THIS EVERY COMMIT FOR EACH LAYOUT
        swt.setOnClickListener((v) -> swt.setChecked(false));
        swt.setOnCheckedChangeListener((whatClicked, newState) -> {
            if(!newState){
                Snackbar.make(swt, "The switch is now off", Snackbar.LENGTH_LONG)
                        .setAction("Undo",click ->swt.setChecked(true)).show();
            }else{
                Snackbar.make(swt, "The switch is now on", Snackbar.LENGTH_LONG)
                        .setAction("Undo",click ->swt.setChecked(false)).show();
            }
        });
        }

    }