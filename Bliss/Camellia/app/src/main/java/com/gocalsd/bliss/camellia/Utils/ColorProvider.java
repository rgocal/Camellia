package com.gocalsd.bliss.camellia.Utils;

import android.graphics.Bitmap;
import android.graphics.Color;

import androidx.core.graphics.ColorUtils;
import androidx.palette.graphics.Palette;

public class ColorProvider {

    public static boolean isDark(int color){
        return ColorUtils.calculateLuminance(color) < 0.25;
    }

    public static int getDominantColor(Bitmap bitmap){
        int defaultValue = 0x000000;
        Palette palette = Palette.from(bitmap).generate();
        return palette.getDominantColor(defaultValue);
    }

    public static int getVibrantColor(Bitmap bitmap) {
        int defaultValue = 0x000000;
        Palette palette = Palette.from(bitmap).generate();
        return palette.getVibrantColor(defaultValue);
    }

    public static int getLightVibrantColor(Bitmap bitmap){
        int defaultValue = 0x000000;
        Palette palette = Palette.from(bitmap).generate();
        return palette.getLightVibrantColor(defaultValue);
    }

    public static int getDarkVibrantColor(Bitmap bitmap){
        int defaultValue = 0x000000;
        Palette palette = Palette.from(bitmap).generate();
        return palette.getDarkVibrantColor(defaultValue);
    }

    public static int getMutedColor(Bitmap bitmap){
        int defaultValue = 0x000000;
        Palette palette = Palette.from(bitmap).generate();
        return palette.getMutedColor(defaultValue);
    }

    public static int getLightMutedColor(Bitmap bitmap){
        int defaultValue = 0x000000;
        Palette palette = Palette.from(bitmap).generate();
        return palette.getLightMutedColor(defaultValue);
    }

    public static int getDarkMutedColor(Bitmap bitmap){
        int defaultValue = 0x000000;
        Palette palette = Palette.from(bitmap).generate();
        return palette.getDarkMutedColor(defaultValue);
    }

    public static int darkenColor(int color){
        return ColorUtils.blendARGB(color, Color.BLACK, 0.6f);
    }

    public static int lightenColor(int color){
        return ColorUtils.blendARGB(color, Color.WHITE, 0.6f);
    }

    public static int getTransparentColor(int color, double intensity){
        int alpha = Color.alpha(color);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);

        // Set alpha based on your logic, here I'm making it 25% of it's initial value.
        alpha *= intensity;

        return Color.argb(alpha, red, green, blue);
    }

}