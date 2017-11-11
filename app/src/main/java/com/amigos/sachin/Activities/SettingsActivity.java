package com.amigos.sachin.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amigos.sachin.ApplicationCache.ApplicationCache;
import com.amigos.sachin.R;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

public class SettingsActivity extends AppCompatActivity {

    LinearLayout layout_preview_profile, layout_blocked_users, layout_search_people, layout_facebook_logout;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        context = getApplicationContext();
        //layout_preview_profile = (LinearLayout) findViewById(R.id.layout_preview_profile);
        layout_blocked_users = (LinearLayout) findViewById(R.id.layout_blocked_users);
        //layout_search_people = (LinearLayout) findViewById(R.id.layout_search_people);
        layout_facebook_logout = (LinearLayout) findViewById(R.id.layout_facebook_logout);
        //getSupportActionBar().setTitle("Settings");

        /*layout_preview_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this,UserProfileActivity.class);
                intent.putExtra("userId", ApplicationCache.myUserVO.getId());
                startActivity(intent);
            }
        });*/

        layout_blocked_users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this,BlockListActivity.class);
                startActivity(intent);
            }
        });

        /*layout_search_people.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this,DragActivity.class);
                startActivity(intent);
            }
        });*/

        layout_facebook_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder2 = new AlertDialog.Builder(SettingsActivity.this,AlertDialog.THEME_HOLO_DARK);
                builder2.setTitle("Logout!!!");
                builder2.setMessage("Are you sure you want to logout from Amigos?");
                builder2.setCancelable(true);

                builder2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FacebookSdk.sdkInitialize(context);
                        LoginManager.getInstance().logOut();
                        Intent intent = new Intent(SettingsActivity.this,MainActivity.class);
                        startActivity(intent);
                    }
                });

                builder2.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder2.create();
                alert11.show();
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
