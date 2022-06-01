package com.gocalsd.bliss.camellia.Settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class SettingsProvider extends SettingKeys {

    public static SettingsProvider settings;

    public SharedPreferences sharedPreferences;

    public static SettingsProvider getInstance(Context context) {
        if (settings != null) {
            return settings;
        } else {
            SettingsProvider.settings = new SettingsProvider();
            return SettingsProvider.settings;
        }
    }

    public static SettingsProvider assumeNotNull() {
        return settings;
    }

    public static void invalidate() {
        SettingsProvider.settings = null;
    }

    public static void invalidate(Context context) {
        SettingsProvider.settings = new SettingsProvider();
    }

    public static SharedPreferences get(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static SharedPreferences.Editor put(Context context) {
        return get(context).edit();
    }

    public static String getString(Context context, String key, String defValue) {
        return get(context).getString(key, defValue);
    }

    public static void putString(Context context, String key, String value) {
        put(context).putString(key, value).apply();
    }

    public static boolean getBoolean(Context context, String key, boolean defValue) {
        return get(context).getBoolean(key, defValue);
    }

    public static void putBoolean(Context context, String key, boolean value) {
        put(context).putBoolean(key, value).apply();
    }

    public static long getLong(Context context, String key, long defValue) {
        return get(context).getLong(key, defValue);
    }

    public static void putLong(Context context, String key, long value) {
        put(context).putLong(key, value).apply();
    }

    public static void putDouble(Context context, String key, double value) {
        put(context).putLong(key, Double.doubleToRawLongBits(value)).apply();
    }

    public static double getDouble(Context context, String key, double defaultValue) {
        return Double.longBitsToDouble(get(context).getLong(key, Double.doubleToLongBits(defaultValue)));
    }

    public static int getInt(Context context, String key, int defValue) {
        return get(context).getInt(key, defValue);
    }

    public static void putInt(Context context, String key, int value) {
        put(context).putInt(key, value).apply();
    }

    public static void restart() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public static boolean shouldFinish(String key) {
        return !key.equals(DEFAULT_KEY);
    }

    public static String encodeTobase64(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 98, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public static Bitmap decodeBase64(String input) {
        if(!input.equals("")) {
            byte[] decodedByte = Base64.decode(input, Base64.DEFAULT);
            return BitmapFactory.decodeStream(new ByteArrayInputStream(decodedByte));
        }else{
            return null;
        }
        //return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

}
