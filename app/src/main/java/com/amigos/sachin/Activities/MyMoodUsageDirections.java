package com.amigos.sachin.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.amigos.sachin.R;

public class MyMoodUsageDirections extends AppCompatActivity {

    Context context;
    SharedPreferences sp;
    String myMoodUsageDirections;
    Button button_usage_directions_my_mood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_mood_usage_directions);
        context = getApplicationContext();
        getSupportActionBar().hide();

        button_usage_directions_my_mood = (Button) findViewById(R.id.button_usage_directions_my_mood);

        sp = context.getSharedPreferences("com.amigos.sachin", Context.MODE_PRIVATE);
        myMoodUsageDirections = sp.getString("myMoodUsageDirections","0");
        sp.edit().putString("myMoodUsageDirections", "1").apply();

        button_usage_directions_my_mood.setOnClickListener(new View.OnClickListener() {
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
