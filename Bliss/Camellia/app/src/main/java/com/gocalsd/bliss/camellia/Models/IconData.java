package com.gocalsd.bliss.camellia.Models;

import android.graphics.drawable.Drawable;

import java.lang.ref.WeakReference;

public class IconData {
        public String title;
        public boolean isHeader = false;
        public boolean isIcon = false;
        public WeakReference<Drawable> drawable;
        public int resource_id;
}
