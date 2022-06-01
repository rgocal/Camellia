package com.gocalsd.bliss.camellia.Views;

import android.content.Context;
import android.util.AttributeSet;


import com.gocalsd.bliss.camellia.Utils.Utilities;

import java.text.SimpleDateFormat;
import java.time.OffsetTime;
import java.util.Date;
import java.util.Locale;

public class MinuteTextView extends DoubleShadowTextView{

    public MinuteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if(Utilities.ATLEAST_OREO){
            OffsetTime offset = OffsetTime.now();
            setText(String.valueOf(offset.getMinute()));
        }else{
            String currentTime = new SimpleDateFormat("mm", Locale.getDefault()).format(new Date());
            setText(currentTime);
        }
    }

}
