package com.gocalsd.bliss.camellia.Settings.Preferences;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.preference.PreferenceCategory;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gocalsd.bliss.camellia.Base.BaseActivity;
import com.gocalsd.bliss.camellia.Utils.ColorProvider;

public class NxPreferenceCategory extends PreferenceCategory {

    final float[] radias = {0, 0, 0, 0, 0, 0, 0, 0};

    public NxPreferenceCategory(Context context) {
        super(context);
    }

    public NxPreferenceCategory(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        super.onCreateView(parent);

        BaseActivity activity = (BaseActivity) getContext();
        int categoryColor = activity.getAccentColor();

        int categoryTitleColor;
        if(ColorProvider.isDark(categoryColor)){
            categoryTitleColor = Color.WHITE;
        }else{
            categoryTitleColor = ColorProvider.darkenColor(categoryColor);
        }

        int[] colors1 = new int[]{categoryColor, categoryColor};
        GradientDrawable.Orientation drawableState = GradientDrawable.Orientation.TOP_BOTTOM;
        GradientDrawable titleDrawable = new GradientDrawable(drawableState, colors1);
        titleDrawable.setCornerRadii(radias);
        titleDrawable.setBounds(0,0,32,0);

        TextView categoryTitle = (TextView) super.onCreateView(parent);
        categoryTitle.setTextColor(categoryTitleColor);
        categoryTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        categoryTitle.setPadding(42, 16, 42, 16);

        ViewGroup.LayoutParams params = categoryTitle.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        categoryTitle.requestLayout();

        Typeface font = Typeface.createFromAsset(parent.getContext().getAssets(), "fonts/Inter-Medium.ttf");
        categoryTitle.setTypeface(font);

        categoryTitle.setBackground(titleDrawable);

        return categoryTitle;
    }
}