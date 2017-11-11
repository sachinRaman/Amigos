package com.amigos.sachin.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.amigos.sachin.R;

public class MyInterestsUsageDirections extends AppCompatActivity {

    Button button_usage_directions;
    SharedPreferences sp;
    Context context;
    String interestsUsageDirections;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_interests_usage_directions);

        getSupportActionBar().hide();

        context = getApplicationContext();



        button_usage_directions = (Button) findViewById(R.id.button_usage_directions);
        button_usage_directions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}