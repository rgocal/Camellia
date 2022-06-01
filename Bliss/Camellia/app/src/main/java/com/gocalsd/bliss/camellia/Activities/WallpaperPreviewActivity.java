package com.gocalsd.bliss.camellia.Activities;

import android.os.Bundle;

import com.gocalsd.bliss.camellia.Base.BaseActivity;
import com.gocalsd.bliss.camellia.R;

public class WallpaperPreviewActivity extends BaseActivity {

    //This activity pulls in a wallpaper bitmap and allows the user to preview it before applying it
    //The wallpaper is cached and saved from that category before the activity is opened
    //Full screen mode will hide the activity layouts and present the wallpaper in full res
    //Lockscreen button will show the Clock View ontop
    //Homescreen will show 4 Random Icons on top of the wallpaper (clicking on them will randomize them)
    //App Settings will allow users  to swap icon packs for wallpaper preview

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper_preview);
    }

}
