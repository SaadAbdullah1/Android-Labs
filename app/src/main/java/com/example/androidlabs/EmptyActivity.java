package com.example.androidlabs;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class EmptyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);
        Bundle dataToPass = getIntent().getExtras();

        DetailsFragment fragment = new DetailsFragment();
        fragment.setArguments(dataToPass);
        getSupportFragmentManager().beginTransaction().replace(R.id.flayout, fragment).commit();
    }
}