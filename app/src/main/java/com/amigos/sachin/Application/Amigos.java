package com.amigos.sachin.Application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.amigos.sachin.Utils.FontsOverride;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Sachin on 8/26/2017.
 */

public class Amigos extends Application {

    public static Context context;
    String myId;

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences sp = getSharedPreferences("com.amigos.sachin",Context.MODE_PRIVATE);
        myId = sp.getString("myId","");
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FontsOverride.setDefaultFont(this, "SERIF", "fonts/Calibri/Calibri.ttf");
        Amigos.context = getApplicationContext();
        /*DatabaseReference userRef = FirebaseDatabase.getInstance().getReference();
        userRef.keepSynced(true);*/
    }
    public static Context getAppContext() {
        return Amigos.context;
    }
}
