package com.amigos.sachin.Application;

import android.app.Application;

import com.amigos.sachin.Utils.FontsOverride;

/**
 * Created by Sachin on 8/26/2017.
 */

public class Amigos extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FontsOverride.setDefaultFont(this, "SERIF", "fonts/Calibri/Calibri.ttf");
    }
}
