package com.gocalsd.bliss.camellia.Views;

import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kieronquinn.monetcompat.view.MonetToolbar;

public class NxToolbar extends MonetToolbar {

    private int itemColor;

    public NxToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public NxToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NxToolbar(Context context) {
        super(context);
    }

    public void centerToolbar(boolean centerTitle){
        // TODO: 5/20/22 cannot find kotlin class, why?
        //CenterToolbarUtil.centerTitle(this, centerTitle);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        colorizeToolbar(NxToolbar.this, itemColor);
    }

    public void setItemColor(int color){
        itemColor = color;
        colorizeToolbar(this, itemColor);
    }

    public static void colorizeToolbar(NxToolbar toolbarView, int toolbarIconsColor) {
        final PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(toolbarIconsColor, PorterDuff.Mode.SRC_IN);
        for(int i = 0; i < toolbarView.getChildCount(); i++) {
            final View v = toolbarView.getChildAt(i);
            doColorizing(v, colorFilter, toolbarIconsColor);
        }
        toolbarView.setTitleTextColor(toolbarIconsColor);
        toolbarView.setSubtitleTextColor(toolbarIconsColor);
    }

    public static void doColorizing(View v, final ColorFilter colorFilter, int toolbarIconsColor){
        if(v instanceof ImageView) {
            ((ImageView)v).getDrawable().setAlpha(255);
            ((ImageView)v).getDrawable().setColorFilter(colorFilter);
        } else if(v instanceof TextView) {
            TextView tv = ((TextView)v);
            tv.setTextColor(toolbarIconsColor);
            tv.setHintTextColor(toolbarIconsColor);

            int drawablesCount = tv.getCompoundDrawables().length;
            for(int k = 0; k < drawablesCount; k++) {
                if(tv.getCompoundDrawables()[k] != null) {
                    tv.getCompoundDrawables()[k].setColorFilter(colorFilter);
                }
            }
        } else if (v instanceof ViewGroup){
            for (int lli = 0; lli< ((ViewGroup)v).getChildCount(); lli ++) {
                doColorizing(((ViewGroup) v).getChildAt(lli), colorFilter, toolbarIconsColor);
            }
        } else {
            Log.d("CustomToolbar", "Unknown class: " + v.getClass().getSimpleName());
        }
    }

}
