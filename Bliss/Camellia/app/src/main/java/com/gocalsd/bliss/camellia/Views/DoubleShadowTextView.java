package com.gocalsd.bliss.camellia.Views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Region;
import androidx.core.graphics.ColorUtils;

import android.util.AttributeSet;
import android.widget.TextView;

import com.gocalsd.bliss.camellia.R;
import com.gocalsd.bliss.camellia.Utils.ColorProvider;

/*
Ported from AOSP Launcher3 Bubbletextview class for general usage
Created by Ryan Gocal
 */

public class DoubleShadowTextView extends InterTextView {

    private final ShadowInfo mShadowInfo;

    public DoubleShadowTextView(Context context) {
        this(context, null);
    }

    public DoubleShadowTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DoubleShadowTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mShadowInfo = new ShadowInfo(context, attrs, defStyle);
    }

    public void setupShadow(int textColor){
        mShadowInfo.ambientShadowBlur = 1;
        if (ColorProvider.isDark(textColor)) {
            mShadowInfo.ambientShadowColor = ColorProvider.lightenColor(textColor);
        } else {
            mShadowInfo.ambientShadowColor = ColorProvider.darkenColor(textColor);
        }
        setShadowLayer(mShadowInfo.ambientShadowBlur, 0, 0, mShadowInfo.ambientShadowColor);
        getPaint().setShadowLayer(mShadowInfo.ambientShadowBlur, 0, 0, mShadowInfo.ambientShadowColor);
    }

    @Override
    public void onDraw(Canvas canvas) {
        // If text is transparent or shadow alpha is 0, don't draw any shadow
        if (mShadowInfo.skipDoubleShadow(this)) {
            super.onDraw(canvas);
            return;
        }
        int alpha = Color.alpha(getCurrentTextColor());

        // We enhance the shadow by drawing the shadow twice
        getPaint().setShadowLayer(mShadowInfo.ambientShadowBlur, 0, 0,
                ColorUtils.setAlphaComponent(mShadowInfo.ambientShadowColor, alpha));

        canvas.save();
        canvas.clipRect(getScrollX(), getScrollY() + getExtendedPaddingTop(),
                getScrollX() + getWidth(),
                getScrollY() + getHeight(), Region.Op.INTERSECT);

        getPaint().setShadowLayer(mShadowInfo.keyShadowBlur, 0.0f, mShadowInfo.keyShadowOffset,
                ColorUtils.setAlphaComponent(mShadowInfo.keyShadowColor, alpha));
        canvas.restore();
    }

    public static class ShadowInfo {
        public float ambientShadowBlur;
        public int ambientShadowColor;

        public final float keyShadowBlur;
        public final float keyShadowOffset;
        public final int keyShadowColor;

        public ShadowInfo(Context c, AttributeSet attrs, int defStyle) {

            TypedArray a = c.obtainStyledAttributes(
                    attrs, R.styleable.ShadowInfo, defStyle, 0);

            ambientShadowBlur = a.getDimension(R.styleable.ShadowInfo_ambientShadowBlur, 0);
            ambientShadowColor = a.getColor(R.styleable.ShadowInfo_ambientShadowColor, 0);

            keyShadowBlur = a.getDimension(R.styleable.ShadowInfo_keyShadowBlur, 0);
            keyShadowOffset = a.getDimension(R.styleable.ShadowInfo_keyShadowOffset, 0);
            keyShadowColor = a.getColor(R.styleable.ShadowInfo_keyShadowColor, 0);
            a.recycle();
        }

        public boolean skipDoubleShadow(TextView textView) {
            int textAlpha = Color.alpha(textView.getCurrentTextColor());
            int keyShadowAlpha = Color.alpha(keyShadowColor);
            int ambientShadowAlpha = Color.alpha(ambientShadowColor);
            if (textAlpha == 0 || (keyShadowAlpha == 0 && ambientShadowAlpha == 0)) {
                textView.getPaint().clearShadowLayer();
                return true;
            } else if (ambientShadowAlpha > 0) {
                textView.getPaint().setShadowLayer(ambientShadowBlur, 0, 0,
                        ColorUtils.setAlphaComponent(ambientShadowColor, textAlpha));
                return true;
            } else if (keyShadowAlpha > 0) {
                textView.getPaint().setShadowLayer(keyShadowBlur, 0.0f, keyShadowOffset,
                        ColorUtils.setAlphaComponent(keyShadowColor, textAlpha));
                return true;
            } else {
                return false;
            }
        }
    }
}
