package com.amigos.sachin.Application;

import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.SharedPreferences;

import com.amigos.sachin.Utils.FontsOverride;
import com.firebase.client.Firebase;
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
        context = getApplicationContext();
        Firebase.setAndroidContext(context);
        /*DatabaseReference userRef = FirebaseDatabase.getInstance().getReference();
        userRef.keepSynced(true);*/
    }
    public static Context getAppContext() {
        return Amigos.context;
    }

    public void onTrimMemory(final int level) {
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            if(myId != null) {
                Firebase activeRef = new Firebase("https://new-amigos.firebaseio.com/users/" + myId + "/active/");
                activeRef.setValue("0");
            }
        }
    }
}
