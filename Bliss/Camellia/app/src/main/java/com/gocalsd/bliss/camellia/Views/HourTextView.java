package com.gocalsd.bliss.camellia.Views;

import android.content.Context;
import android.util.AttributeSet;

import com.gocalsd.bliss.camellia.Utils.Utilities;

import java.text.SimpleDateFormat;
import java.time.OffsetTime;
import java.util.Date;
import java.util.Locale;

public class HourTextView extends DoubleShadowTextView {

    public HourTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if(Utilities.ATLEAST_OREO){
        OffsetTime offset = OffsetTime.now();
        setText(String.valueOf(offset.getHour()));
        }else{
            String currentTime = new SimpleDateFormat("HH", Locale.getDefault()).format(new Date());
            setText(currentTime);
        }
    }

}
