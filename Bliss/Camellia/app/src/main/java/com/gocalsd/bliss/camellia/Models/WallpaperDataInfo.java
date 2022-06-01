package com.gocalsd.bliss.camellia.Models;

import android.content.ComponentName;
import android.graphics.drawable.Drawable;

public class WallpaperDataInfo {

    private String category;
    private String packageName;
    private ComponentName launchIntent;
    private int action;
    private Drawable background;
    private boolean isIcon;

    public WallpaperDataInfo() {
       
    }

    public WallpaperDataInfo(String category, String packageName, ComponentName launchIntent, Drawable background, boolean isIcon, int action) {
        this.category = category;
        this.packageName = packageName;
        this.launchIntent = launchIntent;
        this.background = background;
        this.isIcon = isIcon;
        this.action = action;
    }

    public String getCategoryName() {
        return category;
    }

    public String getCategoryPackage() {
        return packageName;
    }

    public Drawable getBackground() {
        return background;
    }

    public Boolean isIcon() {
        return isIcon;
    }

    public int getAction(){
        return action;
    }

    public ComponentName getLaunchIntent(){
        return launchIntent;
    }
}