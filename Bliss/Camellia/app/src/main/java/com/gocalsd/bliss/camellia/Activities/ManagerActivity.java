package com.gocalsd.bliss.camellia.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentContainerView;

import com.gocalsd.bliss.camellia.Base.BaseActivity;
import com.gocalsd.bliss.camellia.Fragments.CategoriesCustomFragment;
import com.gocalsd.bliss.camellia.Fragments.CategoriesFragment;
import com.gocalsd.bliss.camellia.Fragments.CreationsFragment;
import com.gocalsd.bliss.camellia.Fragments.ManagerFragment;
import com.gocalsd.bliss.camellia.Fragments.ProvidersFragment;
import com.gocalsd.bliss.camellia.Fragments.ProvidersLiveFragment;
import com.gocalsd.bliss.camellia.Fragments.SoundsFragment;
import com.gocalsd.bliss.camellia.R;
import com.gocalsd.bliss.camellia.Utils.FragmentManagerUtils;
import com.gocalsd.bliss.camellia.Utils.Utilities;
import com.gocalsd.bliss.camellia.Views.NxToolbar;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigationrail.NavigationRailView;

public class ManagerActivity extends BaseActivity {

    private final int EXTERNAL_STORAGE_PERMISSION_CODE = 23;
    public static FragmentContainerView fragmentContainerView;

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == EXTERNAL_STORAGE_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                recreate();
            } else {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Functionality limited");
                builder.setMessage(R.string.app_name + " requires Permission to Read External Storage to access wallpaper manager settings.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface dialog) {
                    }

                });
                builder.show();
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.navigation_activity);

        NxToolbar toolbar = findViewById(R.id.toolbar);
        fragmentContainerView = findViewById(R.id.fragment_view);
        NavigationRailView navigationRailView = findViewById(R.id.navigation_rail);
        FloatingActionButton fab = navigationRailView.findViewById(R.id.fab);

        if (Utilities.ATLEAST_MARSHMALLOW){
            if (this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getResources().getString(R.string.app_name) + " requires additional permissions");
                builder.setMessage("Please grant access to the external memory permission.  This allows the app to have access to wallpaper management and services available from the system.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CODE);
                    }
                });
                builder.show();
            }
        }

        Drawable blissIcon = ContextCompat.getDrawable(this, R.drawable.ic_bliss_logo);
        blissIcon.setTint(getTextColor());
        toolbar.setNavigationIcon(blissIcon);

        new FragmentManagerUtils(ManagerActivity.this).openFragment(ManagerActivity.this, ManagerFragment.newInstance(), fragmentContainerView.getId());

        /*
        get the count of providers and set it as a badge
         */
        BadgeDrawable providerBadge = navigationRailView.getOrCreateBadge(R.id.providers);
        providerBadge.setNumber(12);
        providerBadge.setBackgroundColor(getPrimaryColor());
        providerBadge.setBadgeTextColor(getTextColor());
        //set visible after after a timer
        providerBadge.setVisible(false);

        toolbar.setTitle(getResources().getString(R.string.app_name));
        toolbar.setBackgroundColor(getBackgroundColor());
        toolbar.setItemColor(getTextColor());
        setSupportActionBar(toolbar);

        navigationRailView.setBackgroundColor(getBackgroundColor());
        navigationRailView.setItemActiveIndicatorColor(ColorStateList.valueOf(getPrimaryColor()));
        navigationRailView.setItemIconTintList(ColorStateList.valueOf(getTextColor()));
        fab.setBackgroundTintList(ColorStateList.valueOf(getPrimaryColor()));
        fab.setSupportImageTintList(ColorStateList.valueOf(getTextColor()));

        navigationRailView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.content:
                        toolbar.setTitle(item.getTitle());
                        new FragmentManagerUtils(ManagerActivity.this).openFragment(ManagerActivity.this, CategoriesFragment.newInstance(), fragmentContainerView.getId(), true);
                        break;
                    case R.id.custom_content:
                        toolbar.setTitle(item.getTitle());
                        new FragmentManagerUtils(ManagerActivity.this).openFragment(ManagerActivity.this, CategoriesCustomFragment.newInstance(), fragmentContainerView.getId(), true);
                        break;
                    case R.id.providers:
                        toolbar.setTitle(item.getTitle());
                        new FragmentManagerUtils(ManagerActivity.this).openFragment(ManagerActivity.this, ProvidersFragment.newInstance(), fragmentContainerView.getId(), true);
                        break;
                    case R.id.live_providers:
                        toolbar.setTitle(item.getTitle());
                        new FragmentManagerUtils(ManagerActivity.this).openFragment(ManagerActivity.this, ProvidersLiveFragment.newInstance(), fragmentContainerView.getId(), true);
                        break;
                    case R.id.sounds:
                        toolbar.setTitle(item.getTitle());
                        new FragmentManagerUtils(ManagerActivity.this).openFragment(ManagerActivity.this, SoundsFragment.newInstance(), fragmentContainerView.getId(), true);
                        break;
                    case R.id.apps:
                        toolbar.setTitle(item.getTitle());
                        new FragmentManagerUtils(ManagerActivity.this).openFragment(ManagerActivity.this, CreationsFragment.newInstance(), fragmentContainerView.getId(), true);
                        break;

                    default:
                        break;
                }
                return false;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toolbar.setTitle(getResources().getString(R.string.app_name));
                new FragmentManagerUtils(ManagerActivity.this).openFragment(ManagerActivity.this, ManagerFragment.newInstance(), fragmentContainerView.getId(), true);
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        //Doesn't allow fragments to backstack
        //should add a timer for exiting
    }


}
