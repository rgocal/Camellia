package com.gocalsd.bliss.camellia.Utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.gocalsd.bliss.camellia.Icons.AdaptiveIconBitmap;
import com.gocalsd.bliss.camellia.Icons.IconPackHelper;

import org.xmlpull.v1.XmlPullParser;

import java.lang.reflect.InvocationTargetException;

public class PackageUtilities {

    Context mContext;
    private final Object sInstanceLock = new Object();

    private final String ANDROID_SCHEMA = "http://schemas.android.com/apk/res/android";
    private final String[] IC_DIRS = new String[]{"mipmap", "drawable"};
    private final String[] IC_CONFIGS = new String[]{"-anydpi-v26", "-v26", ""};

    public PackageUtilities(Context context) {
        mContext = context;
    }

    public PackageUtilities getInstance(Context context) {
        PackageUtilities sInstance = null;
        synchronized (sInstanceLock) {
            if (sInstance == null) {
                sInstance = new PackageUtilities(context);
            }
            return sInstance;
        }
    }

    public Drawable getPackageIcon(final Context context, final ComponentName componentName) {
        if (componentName.getPackageName() != null) {
            PackageManager packageManager = context.getPackageManager();

            Intent intent = new Intent();
            intent.setComponent(new ComponentName(componentName.getPackageName(), componentName.getClassName()));
            ResolveInfo resolveInfo = packageManager.resolveActivity(intent, 0);

            if (resolveInfo != null) {
                return resolveInfo.loadIcon(packageManager);
            }
        }
        return null;
    }

    public Drawable getPackageIcon(String packageName){
        Drawable appIcon;
        PackageManager packageManager = mContext.getPackageManager();
        try{
            appIcon = packageManager.getApplicationIcon(packageName);
        }catch (PackageManager.NameNotFoundException e){
            appIcon = null;
        }

        if(appIcon == null) {
            try {
                PackageInfo p = packageManager.getPackageInfo(packageName, 0);
                appIcon = p.applicationInfo.loadIcon(packageManager);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                appIcon = ContextCompat.getDrawable(mContext, android.R.drawable.sym_def_app_icon);
            }
        }

        return appIcon;
    }

    public int getIconColor(String packageLabel) {
        Drawable icon = getPackageThemedIcon(packageLabel);
        Bitmap iconBm = ImageUtils.drawableToBitmap(icon);
        try {
            return ColorProvider.getDominantColor(iconBm);
        } catch (IllegalArgumentException e) {
            return Color.WHITE;
        }
    }

    public Drawable getPackageThemedIcon(String packageName) {
        final IconPackHelper iconPackHelper = IconPackHelper.getInstance(mContext);

        ActivityInfo activityInfo = new ActivityInfo();
        activityInfo.packageName = packageName;

        if (iconPackHelper.isIconPackLoaded() && iconPackHelper.getThemedIcon(mContext, packageName)) {
            int iconId = iconPackHelper.getResourceIdForActivityIcon(activityInfo);{
                if (iconId != 0) {
                    return iconPackHelper.getIconPackResources(iconId, mContext);
                }
            }
        }
        return getPackageIcon(packageName);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Drawable getAdaptiveFgIcon(Drawable drawable) {
        if (drawable instanceof AdaptiveIconDrawable) {
            return ((AdaptiveIconDrawable) drawable).getForeground();
        } else return drawable;
    }

    public String getAppLabel(String packageName) {
        ApplicationInfo ai;
        PackageManager pm = mContext.getPackageManager();
        try {
            ai = pm.getApplicationInfo(packageName, 0);
        } catch (final PackageManager.NameNotFoundException e) {
            ai = null;
        }

        //get the application label
        String applicationLabel;
        if(ai != null){
            applicationLabel = String.valueOf(mContext.getPackageManager().getApplicationLabel(ai));
            StringBuilder sb = new StringBuilder(applicationLabel);
            sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
            return sb.toString();
        }else{
            try {
                applicationLabel = pm.getPackageInfo(packageName, 0).packageName;
                StringBuilder sb = new StringBuilder(applicationLabel);
                sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
                return sb.toString();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public boolean areDrawablesIdentical(Drawable drawableA, Drawable drawableB) {
        if(drawableA != null) {
            Drawable.ConstantState stateA = drawableA.getConstantState();
            Drawable.ConstantState stateB = drawableB.getConstantState();
            // If the constant state is identical, they are using the same drawable resource.
            // However, the opposite is not necessarily true.
            /*
            return (stateA == null || !stateA.equals(stateB))
                    && !getBitmap(drawableA).sameAs(getBitmap(drawableB));

             */
            return (stateA == null || !stateA.equals(stateB))
                    && !getBitmap(drawableA).sameAs(getBitmap(drawableB));
        }else{
            return false;
        }
    }

    public Bitmap getBitmap(Drawable drawable) {
        Bitmap result;
        if (drawable instanceof BitmapDrawable) {
            result = ((BitmapDrawable) drawable).getBitmap();
        } else {
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            // Some drawables have no intrinsic width - e.g. solid colours.
            if (width <= 0) {
                width = 1;
            }
            if (height <= 0) {
                height = 1;
            }

            result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        }
        return result;
    }

    public Drawable getAdaptiveForegroundIcon(String packageName) {
        if (mContext == null)
            throw new IllegalStateException("Loader.with(Context) must be called before loading an icon.");

        PackageManager packageManager = mContext.getPackageManager();
        Drawable foreground = null;

        try {
            Resources resources = packageManager.getResourcesForApplication(packageName);
            Resources.Theme theme = resources.newTheme();
            setFakeConfig(resources, Build.VERSION_CODES.O);
            AssetManager assetManager = resources.getAssets();

            XmlResourceParser manifestParser = null;
            String iconName = null;
            try {
                manifestParser = assetManager.openXmlResourceParser("AndroidManifest.xml");
            } catch (Exception e) {
                //ignored
            }

            if (manifestParser != null) {
                int event;
                while ((event = manifestParser.getEventType()) != XmlPullParser.END_DOCUMENT) {
                    if (event == XmlPullParser.START_TAG && manifestParser.getName().equals("application")) {
                        iconName = resources.getResourceName(manifestParser.getAttributeResourceValue(ANDROID_SCHEMA, "icon", 0));
                        if (iconName.contains("/"))
                            iconName = iconName.split("/")[1];
                        break;
                    }

                    manifestParser.next();
                }

                manifestParser.close();
            }

            XmlResourceParser parser = null;
            for (int dir = 0; dir < IC_DIRS.length && parser == null; dir++) {
                for (int config = 0; config < IC_CONFIGS.length && parser == null; config++) {
                    for (String name : iconName != null && !iconName.equals("ic_launcher") ? new String[]{iconName, "ic_launcher"} : new String[]{"ic_launcher"}) {
                        try {
                            parser = assetManager.openXmlResourceParser("res/" + IC_DIRS[dir] + IC_CONFIGS[config] + "/" + name + ".xml");
                        } catch (Exception e) {
                            continue;
                        }

                        if (parser != null)
                            break;
                    }
                }
            }

            int foregroundRes = -1;
            if (parser != null) {
                int event;
                while ((event = parser.getEventType()) != XmlPullParser.END_DOCUMENT) {
                    if (event == XmlPullParser.START_TAG) {
                        if ("foreground".equals(parser.getName())) {
                            try {
                                foregroundRes = parser.getAttributeResourceValue("http://schemas.android.com/apk/res/android", "drawable", 0);
                            } catch (Exception e) {
                                try {
                                    foregroundRes = parser.getAttributeResourceValue("http://schemas.android.com/apk/res/android", "mipmap", 0);
                                } catch (Exception e1) {
                                    //ignored
                                }
                            }
                        }
                    }
                    parser.next();
                }
                parser.close();
            }

                try {
                    foreground = ResourcesCompat.getDrawable(resources, foregroundRes, theme);
                } catch (Resources.NotFoundException e) {
                    try {
                        foreground = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_launcher_foreground", "mipmap", packageName), theme);
                    } catch (Resources.NotFoundException e1) {
                        try {
                            foreground = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_launcher_foreground", "drawable", packageName), theme);
                        } catch (Resources.NotFoundException e2) {
                            //ignored
                        }
                    }
                }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(foreground != null) {
            return foreground;
        }else if(Utilities.ATLEAST_OREO){
            return getAdaptiveFgIcon(getPackageThemedIcon(packageName));
        }else{
            return getCircleIcon(packageName);
        }
    }

    public Drawable getCircleIcon(String packageName) {
        PackageManager packageManager = mContext.getPackageManager();
        Drawable roundIcon;

        try {
            Resources resources = packageManager.getResourcesForApplication(packageName);
            Resources.Theme theme = resources.newTheme();
            setFakeConfig(resources, Build.VERSION_CODES.O);
            AssetManager assetManager = resources.getAssets();

            XmlResourceParser manifestParser = null;
            String iconName = null;
            try {
                manifestParser = assetManager.openXmlResourceParser("AndroidManifest.xml");
            } catch (Exception e) {
            }

            if (manifestParser != null) {
                int event;
                while ((event = manifestParser.getEventType()) != XmlPullParser.END_DOCUMENT) {
                    if (event == XmlPullParser.START_TAG && manifestParser.getName().equals("application")) {
                        iconName = resources.getResourceName(manifestParser.getAttributeResourceValue(ANDROID_SCHEMA, "roundIcon", 0));
                        if (iconName.contains("/"))
                            iconName = iconName.split("/")[1];
                        break;
                    }

                    manifestParser.next();
                }

                manifestParser.close();
            }

            if (iconName != null)
                Log.d("AdaptiveIcon", "Found a round icon for " + packageName + "! " + iconName);

            try {
                roundIcon = ResourcesCompat.getDrawable(resources, resources.getIdentifier(iconName, "mipmap", packageName), theme);
            } catch (Resources.NotFoundException e1) {
                try {
                    roundIcon = ResourcesCompat.getDrawable(resources, resources.getIdentifier(iconName, "drawable", packageName), theme);
                } catch (Resources.NotFoundException e2) {
                    try {
                        roundIcon = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_launcher_round", "mipmap", packageName), theme);
                    } catch (Resources.NotFoundException e3) {
                        roundIcon = ResourcesCompat.getDrawable(resources, resources.getIdentifier("ic_launcher_round", "drawable", packageName), theme);
                    }
                }
            }
        } catch (Exception e) {
            AdaptiveIconBitmap iconBitmap = new AdaptiveIconBitmap();
            iconBitmap.setDrawables(getPackageThemedIcon(packageName), new ColorDrawable(Color.WHITE));
            iconBitmap.setPath(AdaptiveIconBitmap.PATH_CIRCLE);
            iconBitmap.setScale(1.25);
            iconBitmap.setSize(256);

            roundIcon = new BitmapDrawable(mContext.getResources(), iconBitmap.render());
            return roundIcon;
            }
        return roundIcon;
    }

    public void setFakeConfig(Resources resources, int sdk) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        int width, height;
        DisplayMetrics metrics = resources.getDisplayMetrics();
        if (metrics.widthPixels >= metrics.heightPixels) {
            width = metrics.widthPixels;
            height = metrics.heightPixels;
        } else {
            width = metrics.heightPixels;
            height = metrics.widthPixels;
        }

        Configuration configuration = resources.getConfiguration();

        if (Utilities.ATLEAST_OREO) {
            AssetManager.class.getDeclaredMethod("setConfiguration", int.class, int.class, String.class, int.class, int.class,
                    int.class, int.class, int.class, int.class, int.class, int.class, int.class, int.class, int.class,
                    int.class, int.class, int.class, int.class)
                    .invoke(resources.getAssets(), configuration.mcc, configuration.mnc,
                            Utilities.ATLEAST_LOLLIPOP ? configuration.locale.toLanguageTag() : null,
                            configuration.orientation, configuration.touchscreen, configuration.densityDpi,
                            configuration.keyboard, configuration.keyboardHidden, configuration.navigation,
                            width, height, configuration.smallestScreenWidthDp,
                            configuration.screenWidthDp, configuration.screenHeightDp, configuration.screenLayout,
                            configuration.uiMode, configuration.colorMode, sdk);
        } else {
            AssetManager.class.getDeclaredMethod("setConfiguration", int.class, int.class, String.class, int.class, int.class,
                    int.class, int.class, int.class, int.class, int.class, int.class, int.class, int.class, int.class,
                    int.class, int.class, int.class)
                    .invoke(resources.getAssets(), configuration.mcc, configuration.mnc,
                            Utilities.ATLEAST_LOLLIPOP ? configuration.locale.toLanguageTag() : null,
                            configuration.orientation, configuration.touchscreen, configuration.densityDpi,
                            configuration.keyboard, configuration.keyboardHidden, configuration.navigation,
                            width, height, configuration.smallestScreenWidthDp,
                            configuration.screenWidthDp, configuration.screenHeightDp, configuration.screenLayout,
                            configuration.uiMode, sdk);
        }
    }

}
