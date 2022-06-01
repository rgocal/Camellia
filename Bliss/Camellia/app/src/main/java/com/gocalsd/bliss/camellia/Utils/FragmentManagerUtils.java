package com.gocalsd.bliss.camellia.Utils;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.gocalsd.bliss.camellia.R;
import com.kieronquinn.monetcompat.app.MonetCompatActivity;
import com.kieronquinn.monetcompat.app.MonetFragment;

public class FragmentManagerUtils {

    Context mContext;
    private static final Object sInstanceLock = new Object();

    public FragmentManagerUtils(Context context) {
        mContext = context;
    }

    public FragmentManagerUtils getInstance(Context context) {
        FragmentManagerUtils sInstance = null;
        synchronized (sInstanceLock) {
            if (sInstance == null) {
                sInstance = new FragmentManagerUtils(context);
            }
            return sInstance;
        }
    }

    public Context getContext(){
        return this.mContext;
    }

    public void openFragment(final MonetFragment fragment, final int sheetId, final String tag){
        FragmentManager fragmentManager = ((MonetCompatActivity) getContext()).getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(sheetId, fragment, tag);
        transaction.commit();
    }

    public void openFragment(final MonetCompatActivity activity, final MonetFragment fragment, final int sheetId, boolean animation)   {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if(animation) {
            transaction.setCustomAnimations(R.anim.no_animation, android.R.anim.slide_out_right);
        }
        transaction.replace(sheetId, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void openFragment(final MonetCompatActivity activity, final Fragment fragment, final int sheetId)   {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(sheetId, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }



}
