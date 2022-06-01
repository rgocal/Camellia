package com.gocalsd.bliss.camellia.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.RippleDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.gocalsd.bliss.camellia.R;
import com.kieronquinn.monetcompat.app.MonetFragment;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * A PreferenceFragment for the support library. Based on the platform's code with some removed features and a basic ListView layout.
 *
 * @author Christophe Beyls
 * AndroidX Modifications By Ryan Gocal
 */

public abstract class PreferenceFragmentCompat extends MonetFragment {

    private static final int FIRST_REQUEST_CODE = 100;
    private static final int MSG_BIND_PREFERENCES = 1;
    private static final int MSG_REQUEST_FOCUS = 2;
    private static final String PREFERENCES_TAG = "android:preferences";
    private static final double HC_HORIZONTAL_PADDING = 0.8; //5.33

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_BIND_PREFERENCES:
                    bindPreferences();
                    break;
                case MSG_REQUEST_FOCUS:
                    mList.focusableViewAvailable(mList);
                    break;
            }
        }
    };

    private boolean mHavePrefs;
    private boolean mInitDone;
    private ListView mList;
    private PreferenceManager mPreferenceManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Constructor<PreferenceManager> c = PreferenceManager.class.getDeclaredConstructor(Activity.class, int.class);
            c.setAccessible(true);
            mPreferenceManager = c.newInstance(this.getActivity(), FIRST_REQUEST_CODE);
        } catch (Exception ignored) {
        }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        super.onCreateView(layoutInflater, viewGroup, savedInstanceState);
        ListView listView = new ListView(getActivity());
        listView.setId(android.R.id.list);
        listView.setDividerHeight(0);

        listView.setSelector(R.drawable.fluent_ripple);
        RippleDrawable rippleDrawable = (RippleDrawable) listView.getSelector().getCurrent(); // It will assume bg is a RippleDrawable
        rippleDrawable.setColor(ColorStateList.valueOf(getMonet().getAccentColor(listView.getContext(), false)));

        final int horizontalPadding = (int) (HC_HORIZONTAL_PADDING * getResources().getDisplayMetrics().density);
        listView.setPadding(0, 0, horizontalPadding, 0);
        return listView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (mHavePrefs) {
            bindPreferences();
        }

        mInitDone = true;

        if (savedInstanceState != null) {
            Bundle container = savedInstanceState.getBundle(PREFERENCES_TAG);
            if (container != null) {
                final PreferenceScreen preferenceScreen = getPreferenceScreen();
                if (preferenceScreen != null) {
                    preferenceScreen.restoreHierarchyState(container);
                }
            }
        }
    }

    public void onStop() {
        super.onStop();
        try {
            @SuppressLint("PrivateApi") Method m = PreferenceManager.class.getDeclaredMethod("dispatchActivityStop");
            m.setAccessible(true);
            m.invoke(mPreferenceManager);
        } catch (Exception ignored) {
        }
    }

    public void onDestroyView() {
        mList = null;
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroyView();
    }

    public void onDestroy() {
        super.onDestroy();
        try {
            @SuppressLint("PrivateApi") Method m = PreferenceManager.class.getDeclaredMethod("dispatchActivityDestroy");
            m.setAccessible(true);
            m.invoke(mPreferenceManager);
        } catch (Exception ignored) {
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (preferenceScreen != null) {
            Bundle container = new Bundle();
            preferenceScreen.saveHierarchyState(container);
            outState.putBundle(PREFERENCES_TAG, container);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            @SuppressLint("PrivateApi") Method m = PreferenceManager.class.getDeclaredMethod("dispatchActivityResult", int.class, int.class, Intent.class);
            m.setAccessible(true);
            m.invoke(mPreferenceManager, requestCode, resultCode, data);
        } catch (Exception ignored) {
        }
    }

    public PreferenceManager getPreferenceManager() {
        return mPreferenceManager;
    }

    public void setPreferenceScreen(PreferenceScreen screen) {
        try {
            @SuppressLint("PrivateApi") Method m = PreferenceManager.class.getDeclaredMethod("setPreferences", PreferenceScreen.class);
            m.setAccessible(true);
            boolean result = (Boolean) m.invoke(mPreferenceManager, screen);
            if (result && (screen != null)) {
                mHavePrefs = true;
                if (mInitDone) {
                    postBindPreferences();
                }
            }
        } catch (Exception ignored) {
        }
    }

    public PreferenceScreen getPreferenceScreen() {
        try {
            @SuppressLint("PrivateApi") Method m = PreferenceManager.class.getDeclaredMethod("getPreferenceScreen");
            m.setAccessible(true);
            return (PreferenceScreen) m.invoke(mPreferenceManager);
        } catch (Exception e) {
            return null;
        }
    }

    public void addPreferencesFromIntent(Intent intent) {
        requirePreferenceManager();
        try {
            @SuppressLint("PrivateApi") Method m = PreferenceManager.class.getDeclaredMethod("inflateFromIntent", Intent.class, PreferenceScreen.class);
            m.setAccessible(true);
            PreferenceScreen screen = (PreferenceScreen) m.invoke(mPreferenceManager, intent, getPreferenceScreen());
            setPreferenceScreen(screen);
        } catch (Exception ignored) {
        }
    }

    public void addPreferencesFromResource(int resId) {
        requirePreferenceManager();
        try {
            @SuppressLint("PrivateApi") Method m = PreferenceManager.class.getDeclaredMethod("inflateFromResource", Context.class, int.class, PreferenceScreen.class);
            m.setAccessible(true);
            PreferenceScreen screen = (PreferenceScreen) m.invoke(mPreferenceManager, getActivity(), resId, getPreferenceScreen());
            setPreferenceScreen(screen);
        } catch (Exception ignored) {
        }
    }

    public Preference findPreference(CharSequence key) {
        if (mPreferenceManager == null) {
            return null;
        }
        return mPreferenceManager.findPreference(key);
    }

    private void requirePreferenceManager() {
        if (this.mPreferenceManager == null) {
            throw new RuntimeException("This should be called after super.onCreate.");
        }
    }

    private void postBindPreferences() {
        if (!mHandler.hasMessages(MSG_BIND_PREFERENCES)) {
            mHandler.sendEmptyMessage(MSG_BIND_PREFERENCES);
        }
    }

    private void bindPreferences() {
        final PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (preferenceScreen != null) {
            preferenceScreen.bind(getListView());
        }
    }

    public ListView getListView() {
        ensureList();
        return mList;
    }

    private void ensureList() {
        if (mList != null) {
            return;
        }
        View layout = getView();
        if (layout == null) {
            throw new IllegalStateException("Content view not yet created");
        }
        View rawListView = layout.findViewById(android.R.id.list);
        if (rawListView == null) {
            throw new RuntimeException("Your content must have a ListView whose id attribute is 'android.R.id.list'");
        }
        if (!(rawListView instanceof ListView)) {
            throw new RuntimeException("Content has view with id attribute 'android.R.id.list' that is not a ListView class");
        }
        mList = (ListView) rawListView;
        mHandler.sendEmptyMessage(MSG_REQUEST_FOCUS);
    }
}