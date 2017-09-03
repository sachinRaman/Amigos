package com.amigos.sachin.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.amigos.sachin.R;

public class SettingsActivity extends AppCompatActivity {

    TextView tvBlockedUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        tvBlockedUsers = (TextView) findViewById(R.id.tv_blocked_users);
        tvBlockedUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this,BlockListActivity.class);
                startActivity(intent);
            }
        });
    }
}
