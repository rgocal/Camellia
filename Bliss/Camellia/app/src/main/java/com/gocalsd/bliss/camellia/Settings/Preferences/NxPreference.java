package com.gocalsd.bliss.camellia.Settings.Preferences;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.gocalsd.bliss.camellia.Base.BaseActivity;
import com.gocalsd.bliss.camellia.Utils.ColorProvider;

public class NxPreference extends Preference {

    public NxPreference(Context context) {
        super(context);
    }

    public NxPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onBindView(View view){
        super.onBindView(view);
        BaseActivity activity = (BaseActivity) getContext();
        Typeface font = Typeface.createFromAsset(activity.getAssets(), "fonts/Inter-Medium.ttf");

        int textColor;
        if(ColorProvider.isDark(activity.getPrimaryColor())){
            textColor = Color.WHITE;
        }else{
            textColor = ColorProvider.darkenColor(activity.getPrimaryColor());
        }

        TextView prefTitle = view.findViewById(android.R.id.title);
        prefTitle.setTextColor(textColor);
        prefTitle.setTypeface(font);

        TextView prefSummary = view.findViewById(android.R.id.summary);
        prefSummary.setTextColor(textColor);
        prefSummary.setTypeface(font);

    }

}
