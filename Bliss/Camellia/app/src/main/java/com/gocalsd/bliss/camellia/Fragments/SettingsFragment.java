package com.gocalsd.bliss.camellia.Fragments;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.gocalsd.bliss.camellia.R;
import com.gocalsd.bliss.camellia.Settings.Preferences.NxPreference;
import com.gocalsd.bliss.camellia.Utils.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

public class SettingsFragment extends PreferenceFragmentCompat {

    /*
    Add in GCA Launchers Nx PreferenceCategories and Preferences to match the Material You theme
    call in static colors from the main activity for the preferences
     */

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    private Context context;
    public PackageInfo info;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_preferences);

        context = getContext();
        NxPreference versionPref = (NxPreference) findPreference("version");
        versionPref.setTitle("Checking For Updates...");

        RequestQueue queue = Volley.newRequestQueue(context);
        PackageManager pm = context.getPackageManager();
        try {
            info = pm.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String currentVersionCode = String.valueOf(info.versionCode);
        String url = context.getString(R.string.ota_checker);

        if(Utilities.isNetworkAvailable(context)) {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String versionCode = response.getString("version");
                        if (versionCode.equals(currentVersionCode)) {
                            versionPref.setTitle("No Updates Available");
                            versionPref.setSummary("Installed : " + currentVersionCode + System.getProperty("line.separator") + "Latest : " + versionCode);
                        } else {
                            versionPref.setTitle("New Update Available!");
                            versionPref.setSummary("Download Version : " + versionCode);
                            versionPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                                @Override
                                public boolean onPreferenceClick(Preference preference) {
                                    //create interface to download new version
                                    return false;
                                }
                            });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context, "Fail to receive information..", Toast.LENGTH_SHORT).show();
                }
            });
            queue.add(jsonObjectRequest);
        }else{
            versionPref.setTitle("No Updates Available");
            versionPref.setSummary("Installed : " + currentVersionCode + System.getProperty("line.separator") + "Error : " + "No Connection");
        }


    }

}
