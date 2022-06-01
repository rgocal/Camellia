package com.gocalsd.bliss.camellia.Data;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.service.wallpaper.WallpaperService;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.gocalsd.bliss.camellia.Models.WallpaperDataInfo;
import com.gocalsd.bliss.camellia.Utils.PackageUtilities;
import com.gocalsd.bliss.camellia.Utils.Utilities;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WallpaperProviderData {

    /*
    https://android.googlesource.com/platform/packages/apps/WallpaperPicker2/+/refs/tags/android-11.0.0_r24/src/com/android/wallpaper/model/LiveWallpaperInfo.java
    Update this eventually for Android 11 support for live wallpapers
     */

    private static PackageManager pm;
    private static ArrayList<WallpaperDataInfo> resApps;
    private static Bitmap bm;

    @SuppressLint("Recycle")
    public static ArrayList<WallpaperDataInfo> getWallpaperProviders(Context context) {
        resApps = new ArrayList<>();
        pm = context.getPackageManager();

        // Find the last picture saved
        String[] projection;

        if (Utilities.ATLEAST_Q) {
            Log.d("Media Store", "Gathering : ID,DATA,BUCKET,DATE,TYPE");
            //DATA might be deprecated but without it, the picture doesn't load
            //may need a different method later
            projection = new String[]{
                    MediaStore.Images.ImageColumns._ID,
                    MediaStore.Images.ImageColumns.DATA,
                    MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                    MediaStore.Images.ImageColumns.DATE_TAKEN,
                    MediaStore.Images.ImageColumns.MIME_TYPE
            };
        } else {
            Log.d("Media Store", "Gathering : ID,DATA,TYPE");
            projection = new String[]{
                    MediaStore.Images.ImageColumns._ID,
                    MediaStore.Images.ImageColumns.DATA,
                    MediaStore.Images.ImageColumns.MIME_TYPE
            };
        }

        Cursor cursor = null;
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            if (Utilities.ATLEAST_Q) {
                cursor = context.getContentResolver()
                        .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
                                null, MediaStore.Images.Media.DATE_TAKEN);
            }else{
                cursor = context.getContentResolver()
                        .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
                                null, MediaStore.Images.Media.DEFAULT_SORT_ORDER);
            }
        }

        if(cursor == null) {
            // Do Nothing
        }else{
            // Put it in the view
            if (cursor.moveToFirst()) {
                String imageLocation = cursor.getString(1);
                File imageFile = new File(imageLocation);
                if (imageFile.exists()) {
                    bm = BitmapFactory.decodeFile(imageLocation);
                    if (imageLocation != null) {
                        bm = getThumbnailOfLastPhoto(context);
                    }
                }
                //My Photos
                resApps.add(new WallpaperDataInfo("Gallery", null, null, new BitmapDrawable(context.getResources(), bm), false, 9));            }
        }

            setupWallpaperList();
        return resApps;
    }

    @SuppressLint("Recycle")
    public static ArrayList<WallpaperDataInfo> getLiveWallpaperProviders(Context context) {
        resApps = new ArrayList<>();
        pm = context.getPackageManager();

        setupLiveWallpaperList(context);

        if (Utilities.ATLEAST_R) {
            //Live Wallpapers
            resApps.add(new WallpaperDataInfo("Live Wallpapers", null, null, new PackageUtilities(context).getPackageIcon("com.google.android.apps.wallpaper"), true, 12));
        }

        return resApps;
    }

    private static Bitmap getThumbnailOfLastPhoto(Context context) {
        final Cursor cursor;
        if(Utilities.ATLEAST_R){
            cursor = MediaStore.Images.Media.query(context.getContentResolver(),
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Images.ImageColumns._ID,
                            MediaStore.Images.ImageColumns.DATE_TAKEN},
                    null, null, MediaStore.Images.ImageColumns.DATE_TAKEN);
        }else if (Utilities.ATLEAST_Q) {
            cursor = MediaStore.Images.Media.query(context.getContentResolver(),
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Images.ImageColumns._ID,
                            MediaStore.Images.ImageColumns.DATE_TAKEN},
                    null, null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC LIMIT 1");
        }else{
            cursor = MediaStore.Images.Media.query(context.getContentResolver(),
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Images.ImageColumns._ID,
                            MediaStore.Images.ImageColumns.DATE_ADDED},
                    null, null, MediaStore.Images.ImageColumns.DATE_ADDED + " DESC LIMIT 1");
        }

        Bitmap thumb = null;
        if (cursor != null) {
            if (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                thumb = MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(),
                        id, MediaStore.Images.Thumbnails.MINI_KIND, null);
            }
            cursor.close();
        }
        return thumb;
    }

    private static void setupWallpaperList() {
        Set<String> filteredPackages = new HashSet<>();

        //filter out any packages that may be redundant or broken
        filteredPackages.add("com.android.settings");
        filteredPackages.add("com.android.wallpaper.livepicker");
        filteredPackages.add("com.google.android.googlequicksearchbox");
        filteredPackages.add("com.htc.home.personalize");
        filteredPackages.add("com.gocalsd.bliss.camellia");

        Intent intent = new Intent(Intent.ACTION_SET_WALLPAPER);
        List<ResolveInfo> resolveInfoListWallpapers = pm.queryIntentActivities(intent, 0);
        Collections.sort(resolveInfoListWallpapers, new ResolveInfo.DisplayNameComparator(pm));

        for (ResolveInfo resolveInfo : resolveInfoListWallpapers) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            //filter out packages above
            if (!activityInfo.applicationInfo.packageName.contains("launcher")) {
                if (!filteredPackages.contains(resolveInfo.activityInfo.packageName)) {
                    //filter out any packages that may have Gallery or Photos in it's title
                    if (!activityInfo.applicationInfo.loadLabel(pm).toString().startsWith("Gallery ")) {
                        if (!activityInfo.applicationInfo.loadLabel(pm).toString().startsWith("Photos")) {

                            String appName = getFirstWord(activityInfo.applicationInfo.loadLabel(pm).toString());
                            String packageName = activityInfo.packageName;
                            Drawable icon = activityInfo.applicationInfo.loadIcon(pm);

                            resApps.add(new WallpaperDataInfo(appName, packageName, null, icon, true, 0));
                        }
                    }
                }
            }
        }
    }

    private static int isOneWord(String text) {
        try {
            return text.indexOf(' ');
        }catch (NullPointerException e){
            return 0;
        }
    }

    private static String getFirstWord(String text) {
        int index = text.indexOf(' ');
        if (isOneWord(text) > -1) { // Check if there is more than one word.
            return text.substring(0, index).trim(); // Extract first word.
        } else {
            return text; // Text is the first word itself.
        }
    }

    private static void setupLiveWallpaperList(Context context) {
        List<ResolveInfo> liveWallpaperList = pm.queryIntentServices(
                new Intent(WallpaperService.SERVICE_INTERFACE),
                PackageManager.GET_META_DATA);

        liveWallpaperList.sort(new Comparator<ResolveInfo>() {
            final Collator mCollator = Collator.getInstance();

            public int compare(ResolveInfo info1, ResolveInfo info2) {
                return mCollator.compare(info1.loadLabel(pm), info2.loadLabel(pm));
            }
        });

        for (ResolveInfo resolveInfo : liveWallpaperList) {

            WallpaperInfo info = null;
            try {
                info = new WallpaperInfo(context, resolveInfo);
            } catch (IOException | XmlPullParserException e) {
                e.printStackTrace();
            }

            assert info != null;
            Drawable thumb = info.loadThumbnail(pm);
            ComponentName componentInfo = info.getComponent();
            Intent launchIntent = new Intent(WallpaperService.SERVICE_INTERFACE);
            launchIntent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, componentInfo);
            launchIntent.putExtra(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER, componentInfo);
            launchIntent.putExtra(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER, componentInfo);
            launchIntent.setClassName(info.getPackageName(), info.getServiceName());

            resApps.add(new WallpaperDataInfo(info.loadLabel(pm).toString(), info.getPackageName(), componentInfo, thumb, false, -1 ));
        }
    }

}