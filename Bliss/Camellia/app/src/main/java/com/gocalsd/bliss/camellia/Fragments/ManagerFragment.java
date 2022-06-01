package com.gocalsd.bliss.camellia.Fragments;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.gocalsd.bliss.camellia.Activities.ManagerActivity;
import com.gocalsd.bliss.camellia.Base.BaseActivity;
import com.gocalsd.bliss.camellia.R;
import com.gocalsd.bliss.camellia.Utils.ColorProvider;
import com.gocalsd.bliss.camellia.Utils.FragmentManagerUtils;
import com.gocalsd.bliss.camellia.Utils.Utilities;
import com.gocalsd.bliss.camellia.Views.HourTextView;
import com.gocalsd.bliss.camellia.Views.MinuteTextView;
import com.gocalsd.bliss.camellia.Views.VerticalTextView;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.kieronquinn.monetcompat.app.MonetFragment;
import com.kieronquinn.monetcompat.view.MonetSwitch;

public class ManagerFragment extends MonetFragment {

    private Context mContext;
    private WallpaperManager wallpaperManager;

    private final int HOMESCREEN = 0;
    private final int LOCKSCREEN = 1;

    private int lockScreenColor;
    private String homeScreenInfo, lockScreenInfo;
    private BaseActivity activity;

    public static ManagerFragment newInstance() {
        return new ManagerFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.manager_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                new FragmentManagerUtils(mContext).openFragment(activity, SettingsFragment.newInstance(), ManagerActivity.fragmentContainerView.getId(), true);
                return false;
            case R.id.about:
                return true;
            case R.id.submit:
                //find a way to submit an issue to the github repository for content eval
                return true;
            case R.id.license:
                return true;

            default:
                break;
        }

        return false;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_wallpaper_manager, container, false);

        wallpaperManager = WallpaperManager.getInstance(mContext);

        setHasOptionsMenu(true);

        activity = (BaseActivity) getActivity();

        LinearLayoutCompat wallpaperHolder = rootView.findViewById(R.id.wallpaper_holder);
        FrameLayout lockscreenHolder = rootView.findViewById(R.id.lockscreen_frame);
        AppCompatImageView lockscreen = rootView.findViewById(R.id.wallpaper_lockscreen);
        AppCompatImageView homescreen = rootView.findViewById(R.id.wallpaper_homescreen);
        VerticalTextView homescreenLabel = rootView.findViewById(R.id.wallpaper_homescreen_label);
        HourTextView hourView = rootView.findViewById(R.id.hourView);
        MinuteTextView minView = rootView.findViewById(R.id.minuteView);
        MonetSwitch darkThemeSwitch = rootView.findViewById(R.id.nightModeSwitch);

        darkThemeSwitch.setChecked(BaseActivity.NIGHTMODE);
        darkThemeSwitch.setEnabled(false);

        MaterialShapeDrawable shapeDrawable = new MaterialShapeDrawable();
        shapeDrawable.setCornerSize(28);
        shapeDrawable.setTint(ManagerActivity.PRIMARY_COLOR);
        wallpaperHolder.setBackground(shapeDrawable);

        if (Utilities.ATLEAST_NOUGAT && Utilities.checkPermissionForReadExtertalStorage(mContext)) {
            lockscreen.setImageDrawable(getWallpaper(WallpaperManager.FLAG_LOCK));
            if(ColorProvider.isDark(lockScreenColor)){
                hourView.setTextColor(Color.WHITE);
                minView.setTextColor(Color.WHITE);
                hourView.setupShadow(lockScreenColor);
                minView.setupShadow(lockScreenColor);
            }else{
                hourView.setTextColor(lockScreenColor);
                minView.setTextColor(lockScreenColor);
                hourView.setupShadow(ColorProvider.darkenColor(lockScreenColor));
                minView.setupShadow(ColorProvider.darkenColor(lockScreenColor));
            }

        } else {
            lockscreenHolder.setVisibility(View.GONE);
        }

        if (Utilities.checkPermissionForReadExtertalStorage(mContext)) {
            if(Utilities.ATLEAST_NOUGAT){
                homescreen.setImageDrawable(getWallpaper(WallpaperManager.FLAG_SYSTEM));
            }else{
                homescreen.setImageDrawable(getHomeScreenWallpaper());
            }
            homescreenLabel.setText(homeScreenInfo);

            if(ColorProvider.isDark(activity.getPrimaryColor())){
                homescreenLabel.setTextColor(Color.WHITE);
                homescreenLabel.setTextColor(activity.getPrimaryColor());
            }else{
                homescreenLabel.setTextColor(activity.getPrimaryColor());
                homescreenLabel.setTextColor(activity.getSecondaryColor());
            }
        }

        return rootView;
    }

    public Drawable getHomeScreenWallpaper () {
        @SuppressLint("MissingPermission") Drawable wallpaperDrawable = wallpaperManager.getDrawable();
        wallpaperDrawable.mutate();
        wallpaperDrawable.invalidateSelf();
        homeScreenInfo = generateImageInfo(wallpaperManager, HOMESCREEN);
        return wallpaperDrawable;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("MissingPermission")
    public Drawable getWallpaper (int wallpaperType) {
        //WallpaperManager.FLAG_SYSTEM == 1
        //WallpaperManager.FLAG_LOCK == 2
        ParcelFileDescriptor pfd = wallpaperManager.getWallpaperFile(wallpaperType);
        if (pfd == null) {
            return null;
        }else{
            Bitmap result = BitmapFactory.decodeFileDescriptor(pfd.getFileDescriptor());
            if(wallpaperType == WallpaperManager.FLAG_SYSTEM){
                homeScreenInfo = generateImageInfo(wallpaperManager, HOMESCREEN);
            }else if(wallpaperType == WallpaperManager.FLAG_LOCK){
                lockScreenColor = generateImageColor(result);
                lockScreenInfo = generateImageInfo(wallpaperManager, LOCKSCREEN);
            }
            try {
                pfd.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new BitmapDrawable(getResources(), result);
        }
    }

    private int generateImageColor (Bitmap result){
        return ColorProvider.getDominantColor(result);
    }

    private String generateImageInfo (WallpaperManager wallpaperManager,int wallpaperType){
        if (!String.valueOf(wallpaperManager.getWallpaperInfo()).equals("null")) {
            return String.valueOf(wallpaperManager.getWallpaperInfo());
        } else {
            if (wallpaperType == LOCKSCREEN) {
                return "Lockscreen";
            } else if (wallpaperType == HOMESCREEN) {
                return "Homescreen";
            }
        }
        return null;
    }

}
