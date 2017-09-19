package com.amigos.sachin.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amigos.sachin.ApplicationCache.ApplicationCache;
import com.amigos.sachin.R;
import com.facebook.login.LoginManager;

public class SettingsActivity extends AppCompatActivity {

    LinearLayout layout_preview_profile, layout_blocked_users, layout_facebook_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        layout_preview_profile = (LinearLayout) findViewById(R.id.layout_preview_profile);
        layout_blocked_users = (LinearLayout) findViewById(R.id.layout_blocked_users);
        /*layout_facebook_logout = (LinearLayout) findViewById(R.id.layout_facebook_logout);*/
        //getSupportActionBar().setTitle("Settings");

        layout_preview_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this,UserProfileActivity.class);
                intent.putExtra("userId", ApplicationCache.myUserVO.getId());
                startActivity(intent);
            }
        });

        layout_blocked_users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this,BlockListActivity.class);
                startActivity(intent);
            }
        });
        /*layout_blocked_users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.logOut();
                Intent intent = new Intent(SettingsActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });*/
    }
}
