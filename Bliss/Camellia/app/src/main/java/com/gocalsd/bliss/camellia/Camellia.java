package com.gocalsd.bliss.camellia;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;

import com.kieronquinn.monetcompat.core.MonetCompat;

public class Camellia extends Application {

    public static final String TAG = Camellia.class.getSimpleName();
    private static Application sApplication;

    public static Application getApplication() {
        return sApplication;
    }

    public static Context getContext() {
        return getApplication().getApplicationContext();
    }

    public static Camellia get(Activity activity) {
        return (Camellia) activity.getApplication();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        MonetCompat.enablePaletteCompat();
    }

}