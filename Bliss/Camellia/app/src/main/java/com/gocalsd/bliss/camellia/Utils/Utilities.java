package com.gocalsd.bliss.camellia.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.view.View;

import androidx.annotation.NonNull;

import com.gocalsd.bliss.camellia.Camellia;

import java.io.File;

public class Utilities {

    public static final boolean ATLEAST_OREO_MR1 =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1;

    public static final boolean ATLEAST_OREO =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;

    public static final boolean ATLEAST_NOUGAT_MR1 =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1;

    public static final boolean ATLEAST_NOUGAT =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;

    public static final boolean ATLEAST_LOLLIPOP_MR1 =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1;

    public static final boolean ATLEAST_LOLLIPOP =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;


    public static final boolean ATLEAST_MARSHMALLOW =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;

    public static final boolean ATLEAST_P = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P;

    public static final boolean ATLEAST_Q = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;

    public static final boolean ATLEAST_R = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R;

    public static boolean checkPermissionForReadExtertalStorage(Context context) {
        if (Utilities.ATLEAST_MARSHMALLOW) {
            int result = context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    public static boolean isRooted(Context context) {
        boolean isEmulator = isEmulator(context);
        String buildTags = Build.TAGS;
        if (!isEmulator && buildTags != null && buildTags.contains("test-keys")) {
            return true;
        } else {
            File file = new File("/system/app/Superuser.apk");
            if (file.exists()) {
                return true;
            } else {
                file = new File("/system/xbin/su");
                return !isEmulator && file.exists();
            }
        }
    }

    public static boolean isEmulator(Context context) {
        String androidId = Settings.Secure.getString(context.getContentResolver(), "android_id");
        return "sdk".equals(Build.PRODUCT) || "google_sdk".equals(Build.PRODUCT) || androidId == null;
    }

    public static int isEdgeToEdgeEnabled(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("config_navBarInteractionMode", "integer", "android");
        if (resourceId > 0) {
            return resources.getInteger(resourceId);
        }
        return 0;
    }

    public static void setNavigationBarSafeArea(@NonNull View view, @NonNull Activity activity) {
        // Check whether the app is in multi-mode or not
        boolean isInMultiWindowMode = false;
        if (Utilities.ATLEAST_NOUGAT) {
            try {
                isInMultiWindowMode = activity.isInMultiWindowMode();
            } catch (NullPointerException e) {
                //ignored
            }
        }

        if (!isInMultiWindowMode) {
            // Only when app is not in multi-mode
            Context context = Camellia.getContext().getApplicationContext();

            int showNavigationBarInt = context.getResources()
                    .getIdentifier("config_showNavigationBar", "bool", "android");

            // Check if the navigation bar is showing or not.
            // This would be true only if the application is showing the navigation bar in translucent mode
            boolean showNavigationBar = showNavigationBarInt > 0 && context.getResources().getBoolean(showNavigationBarInt);

            // This is specific for the emulator since it always gives as false for showNavigationBar
            // Especially for Google Devices
            if (Build.FINGERPRINT.contains("generic"))
                showNavigationBar = true;

            if (showNavigationBar) {
                // Once we know navigation bar is showing we need to identify the height of navigation bar
                int navigationBarHeight = context.getResources()
                        .getIdentifier("navigation_bar_height", "dimen", "android");

                if (navigationBarHeight > 0) {
                    navigationBarHeight = context.getResources().getDimensionPixelSize(navigationBarHeight);
                }

                // Apply the height of navigation bar as a padding to the view which is the bottom-most view in your screen
                // Very important to do this in onCreate of the view else it would end up applying more padding from bottom
                view.setPaddingRelative(view.getPaddingStart(),
                        view.getPaddingTop(),
                        view.getPaddingEnd(),
                        view.getPaddingBottom() + navigationBarHeight);
            }
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

}
