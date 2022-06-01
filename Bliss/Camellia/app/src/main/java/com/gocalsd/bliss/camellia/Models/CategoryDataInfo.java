package com.gocalsd.bliss.camellia.Models;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.gocalsd.bliss.camellia.Settings.SettingsProvider;

public class CategoryDataInfo {

    private final String catLabel;
    private String catUrl;
    private Drawable catBackground;
    private final int action;

    public CategoryDataInfo(String label, Drawable background, int action) {
        this.catLabel = label;
        this.catBackground = background;
        this.action = action;
    }

    public CategoryDataInfo(String category, String url, int action) {
        this.catLabel = category;
        this.catUrl = url;
        this.action = action;
    }

    public String getCategoryName() {
        return this.catLabel;
    }

    public int getAction(){
        return this.action;
    }

    public String getCustomUrl(Context context, String categoryName) {
        this.catUrl = SettingsProvider.get(context).getString(categoryName + ".url", catUrl);
        return SettingsProvider.get(context).getString(categoryName + ".url", catUrl);
    }

    public void setCustomUrl(Context context, String categoryName, String customUrl){
        this.catUrl = customUrl;
        SettingsProvider.get(context).edit().putString(categoryName + ".url", customUrl).apply();
    }

    public Drawable getCategoryBackground(){
        return this.catBackground;
    }

}