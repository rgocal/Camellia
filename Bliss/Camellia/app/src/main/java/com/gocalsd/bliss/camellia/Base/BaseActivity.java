package com.gocalsd.bliss.camellia.Base;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.core.view.WindowCompat;

import com.gocalsd.bliss.camellia.Utils.ColorProvider;
import com.gocalsd.bliss.camellia.Utils.SystemUiController;
import com.gocalsd.bliss.camellia.Utils.Utilities;
import com.kieronquinn.monetcompat.app.MonetCompatActivity;

public class BaseActivity extends MonetCompatActivity {

    /*
    Base Activity that extends the Monet Activity
    Lazy method so we don't have to pre configure every activity
    Includes methods to extract activity colors easily
    */

    private SystemUiController systemUiController;
    private View decorView;
    private Window window;

    public static int PRIMARY_COLOR;
    public static int BACKGROUND_COLOR;
    public static int SECONDARY_COLOR;
    public static int ACCENT_COLOR;
    public static int TEXT_COLOR;
    public static boolean NIGHTMODE;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        systemUiController = new SystemUiController(getWindow());
        window = getWindow();
        decorView = window.getDecorView();

        WindowCompat.setDecorFitsSystemWindows(window, true);

        if(Utilities.isEdgeToEdgeEnabled(this) != 0) {
            Utilities.setNavigationBarSafeArea(decorView, this);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        window.setNavigationBarColor(getBackgroundColor());
        window.setStatusBarColor(getBackgroundColor());

        systemUiController.updateNavbarUiState(0, isDark());
        systemUiController.updateStatusbarUiState(0, isDark());
        decorView.setBackground(null);
        decorView.setBackgroundColor(ColorProvider.getTransparentColor(getBackgroundColor(), .85f));
    }

    protected void onResume() {
        super.onResume();
        window.setNavigationBarColor(getBackgroundColor());
        window.setStatusBarColor(getBackgroundColor());

        systemUiController.updateNavbarUiState(0, isDark());
        systemUiController.updateStatusbarUiState(0, isDark());
        decorView.setBackground(null);
        decorView.setBackgroundColor(ColorProvider.getTransparentColor(getBackgroundColor(), .85f));
    }

    public boolean isDark(){
        return ColorProvider.isDark(getBackgroundColor());
    }

    public int getTextColor(){
        TEXT_COLOR = ColorProvider.isDark(getPrimaryColor()) ? Color.WHITE : ColorProvider.darkenColor(getPrimaryColor());
        return TEXT_COLOR;
    }

    public int getPrimaryColor(){
        PRIMARY_COLOR = getMonet().getPrimaryColor(this, isNightMode());
        return PRIMARY_COLOR;
    }

    public int getSecondaryColor(){
        SECONDARY_COLOR = getMonet().getSecondaryColor(this, isNightMode());
        return SECONDARY_COLOR;
    }

    public int getBackgroundColor(){
        BACKGROUND_COLOR = getMonet().getBackgroundColor(this, isNightMode());
        return BACKGROUND_COLOR;
    }

    public int getAccentColor(){
        ACCENT_COLOR = getMonet().getAccentColor(this, isNightMode());
        return ACCENT_COLOR;
    }

    public boolean isNightMode(){
        boolean isNight = false;

        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                isNight = true;
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                isNight = false;
                break;
        }
        NIGHTMODE = isNight;
        return isNight;
    }

}
