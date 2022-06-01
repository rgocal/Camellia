package com.gocalsd.bliss.camellia.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gocalsd.bliss.camellia.Base.BaseActivity;
import com.gocalsd.bliss.camellia.Models.WallpaperDataInfo;
import com.gocalsd.bliss.camellia.R;
import com.gocalsd.bliss.camellia.Utils.ColorProvider;
import com.gocalsd.bliss.camellia.Utils.ImageUtils;
import com.gocalsd.bliss.camellia.Utils.ItemClickListener;
import com.gocalsd.bliss.camellia.Utils.PackageUtilities;
import com.gocalsd.bliss.camellia.Utils.Utilities;
import com.gocalsd.bliss.camellia.Views.InterTextView;
import com.gocalsd.bliss.camellia.Views.NxToolbar;
import com.gocalsd.bliss.camellia.Views.VerticalTextView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.shape.MaterialShapeDrawable;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private WallpaperManager wallpaperManager;
    private int lockscreenColor, homescreenColor;
    private String lockscreenInfo, homescreenInfo;
    private List<WallpaperDataInfo> mDataSet = new ArrayList<>();
    private Intent intent = new Intent();

    private final int EXTERNAL_STORAGE_PERMISSION_CODE = 23;

    public static final int IMAGE_PICK = 5;
    public static final int PICK_WALLPAPER_THIRD_PARTY_ACTIVITY = 6;

    private final int HOMESCREEN = 0;
    private final int LOCKSCREEN = 1;

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                //Select image from photos
                Uri wallpaperUri = data.getData();
                if (wallpaperUri != null) {
                    Uri imageUri = data.getData();
                    try {
                        if (Utilities.ATLEAST_NOUGAT) {
                            Rect rect = new Rect();
                            wallpaperManager.setBitmap(getThumbnail(imageUri), rect, true, WallpaperManager.FLAG_SYSTEM);
                        } else {
                            wallpaperManager.setBitmap(getThumbnail(imageUri));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (requestCode == PICK_WALLPAPER_THIRD_PARTY_ACTIVITY
                && resultCode == Activity.RESULT_OK) {
            //Use third-party activity.
            setResult(Activity.RESULT_OK);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wallpaper_manager);

        NxToolbar nxToolbar = findViewById(R.id.toolbar);
        LinearLayoutCompat wallpaperHolder = findViewById(R.id.wallpaper_holder);
        FrameLayout lockscreenHolder = findViewById(R.id.lockscreen_frame);
        AppCompatImageView lockscreen = findViewById(R.id.wallpaper_lockscreen);
        AppCompatImageView homescreen = findViewById(R.id.wallpaper_homescreen);
        VerticalTextView lockscreenLabel = findViewById(R.id.wallpaper_lockscreen_label);
        VerticalTextView homescreenLabel = findViewById(R.id.wallpaper_homescreen_label);
        MaterialButton subTitle = findViewById(R.id.wallpaper_subtitle);
        RecyclerView wallpaperList = findViewById(R.id.wallpaper_list);

        wallpaperManager = WallpaperManager.getInstance(this);

        //mDataSet = WallpaperProviderData.getAppInfo(this);

        if (Utilities.ATLEAST_NOUGAT && Utilities.checkPermissionForReadExtertalStorage(this)) {
            lockscreen.setImageDrawable(getLockScreenWallpaper());
            lockscreenLabel.setText(lockscreenInfo);
            lockscreenLabel.setTextColor(lockscreenColor);
        } else {
            lockscreenHolder.setVisibility(View.GONE);
        }

        if (Utilities.checkPermissionForReadExtertalStorage(this)) {
            homescreen.setImageDrawable(getHomeScreenWallpaper());
            homescreenLabel.setText(homescreenInfo);
            homescreenLabel.setTextColor(homescreenColor);
        }

        if (Utilities.ATLEAST_MARSHMALLOW){
            if (this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Bliss Styles requires additional properties enabled");
                builder.setMessage("Please grant access to external memory.  This allows the app to have access to wallpaper services and management properties.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CODE);
                    }
                });
                builder.show();
            }
        }

        MaterialShapeDrawable shapeDrawable = new MaterialShapeDrawable();
        shapeDrawable.setCornerSize(28);
        shapeDrawable.setTint(getPrimaryColor());

        nxToolbar.setTitle("Wallpaper Management");
        nxToolbar.centerToolbar(true);
        nxToolbar.setBackgroundColor(getBackgroundColor());
        nxToolbar.setItemColor(getTextColor());

        subTitle.setTextColor(getTextColor());
        Drawable wallpaperIcon = ContextCompat.getDrawable(this, R.drawable.ic_baseline_wallpaper);
        subTitle.setIcon(wallpaperIcon);
        subTitle.setIconGravity(MaterialButton.ICON_GRAVITY_TEXT_START);
        subTitle.setIconTint(ColorStateList.valueOf(getTextColor()));

        wallpaperHolder.setBackground(shapeDrawable);

        AppListAdapter appAdapter = new AppListAdapter(new ItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                if (mDataSet.get(position).getAction() == 0) {
                    intent = getPackageManager().getLaunchIntentForPackage(mDataSet.get(position).getCategoryPackage());
                    Log.d("Package", " : " + mDataSet.get(position).getCategoryPackage());
                    try {
                        startActivity(intent);
                    } catch (NullPointerException ignored) {
                        //activity not found
                        Toast.makeText(v.getContext(), "Device could not access specific activity", Toast.LENGTH_SHORT).show();
                    }
                } else if (mDataSet.get(position).getAction() == -1) {
                    intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
                    intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                            mDataSet.get(position).getLaunchIntent());
                    startActivityForResult(intent, PICK_WALLPAPER_THIRD_PARTY_ACTIVITY);

                } else if (mDataSet.get(position).getAction() == 9) {
                    intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    String[] mimeTypes = {"image/jpeg", "image/png"};
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                    startActivityForResult(intent, IMAGE_PICK);
                } else if (mDataSet.get(position).getAction() == 12) {
                    Intent intent = new Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
                    startActivityForResult(intent, 155);
                }

            }

            @Override
            public void onItemLongClick(View v, int position) {

            }
        }) {
        };

        wallpaperList.setAdapter(appAdapter);
        wallpaperList.setClipToPadding(true);
        wallpaperList.setHasFixedSize(true);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3, RecyclerView.VERTICAL, false);
        wallpaperList.setLayoutManager(gridLayoutManager);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case EXTERNAL_STORAGE_PERMISSION_CODE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    recreate();
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("This app requires Read External Permission enabled to modify the Wallpaper services in any way.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }

        public Bitmap getThumbnail (Uri uri) throws IOException {
            InputStream input = this.getContentResolver().openInputStream(uri);

            BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
            onlyBoundsOptions.inJustDecodeBounds = true;
            onlyBoundsOptions.inDither = true;//optional
            onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
            BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
            input.close();

            if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1)) {
                return null;
            }

            int originalSize = Math.max(onlyBoundsOptions.outHeight, onlyBoundsOptions.outWidth);

            double ratio = (originalSize > getResources().getDisplayMetrics().heightPixels) ? (originalSize / getResources().getDisplayMetrics().heightPixels) : 1.0;

            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
            bitmapOptions.inDither = true; //optional
            bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//
            input = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
            input.close();
            return bitmap;
        }

        private int getPowerOfTwoForSampleRatio ( double ratio){
            int k = Integer.highestOneBit((int) Math.floor(ratio));
            if (k == 0) return 1;
            else return k;
        }

        public Drawable getHomeScreenWallpaper () {
            @SuppressLint("MissingPermission") Drawable wallpaperDrawable = wallpaperManager.getDrawable();
            wallpaperDrawable.mutate();
            wallpaperDrawable.invalidateSelf();
            homescreenInfo = generateImageInfo(wallpaperManager, HOMESCREEN);
            homescreenColor = generateImageColor(ImageUtils.drawableToBitmap(wallpaperDrawable));
            return wallpaperDrawable;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @SuppressLint("MissingPermission")
        public Drawable getLockScreenWallpaper () {
            @SuppressLint("MissingPermission") ParcelFileDescriptor pfd = wallpaperManager.getWallpaperFile(WallpaperManager.FLAG_LOCK);
            if (pfd == null)
                pfd = wallpaperManager.getWallpaperFile(WallpaperManager.FLAG_SYSTEM);
            if (pfd != null) {
                Bitmap result = BitmapFactory.decodeFileDescriptor(pfd.getFileDescriptor());
                lockscreenColor = generateImageColor(result);
                lockscreenInfo = generateImageInfo(wallpaperManager, LOCKSCREEN);
                try {
                    pfd.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return new BitmapDrawable(getResources(), result);
            }
            return wallpaperManager.getDrawable();
        }

        private int generateImageColor (Bitmap result){
            int determinedColor = ColorProvider.getDominantColor(result);
            if (ColorProvider.isDark(determinedColor)) {
                return Color.WHITE;
            } else {
                return determinedColor;
            }
        }

        private String generateImageInfo (WallpaperManager wallpaperManager,int wallpaperType){
            if (!String.valueOf(wallpaperManager.getWallpaperInfo()).equals("null")) {
                return String.valueOf(wallpaperManager.getWallpaperInfo());
            } else {
                if (wallpaperType == LOCKSCREEN) {
                    return "Lockscreen";
                } else if (wallpaperType == HOMESCREEN) {
                    return "Homescreen";
                }
            }
            return null;
        }

        public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.MyViewHolder> {

            int bitmapColor;
            int newBitmapColor;
            Bitmap bitmap;

            private final ItemClickListener clickListener;

            protected AppListAdapter(ItemClickListener clickListener) {
                this.clickListener = clickListener;
            }

            @Override
            public long getItemId(int position) {
                return mDataSet.get(position).hashCode();
            }

            @Override
            public int getItemViewType(int position) {
                return position;
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.wallpaper_item, null);
                final MyViewHolder mvh = new MyViewHolder(itemView);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickListener.onItemClick(v, mvh.getAdapterPosition());
                    }
                });

                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        clickListener.onItemLongClick(v, mvh.getAdapterPosition());
                        return true;
                    }
                });

                return mvh;
            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
                holder.name.setText(mDataSet.get(position).getCategoryName());
                holder.imageViewCompat.setBackgroundDrawable(mDataSet.get(position).getBackground());

                if (!mDataSet.get(position).isIcon()) {
                    holder.imageViewCompat.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
                }

                try {
                    bitmap = ImageUtils.drawableToBitmap(mDataSet.get(position).getBackground());
                } catch (NullPointerException ignored) {
                    bitmap = ImageUtils.drawableToBitmap(new PackageUtilities(holder.itemView.getContext()).getPackageIcon(mDataSet.get(position).getCategoryPackage()));
                }

                bitmapColor = ColorProvider.getDominantColor(bitmap);
                newBitmapColor = ColorUtils.setAlphaComponent(bitmapColor, 128);
                holder.name.setBackgroundColor(newBitmapColor);
                if (ColorProvider.isDark(bitmapColor)) {
                    holder.name.setTextColor(Color.WHITE);
                } else {
                    holder.name.setTextColor(ColorProvider.darkenColor(bitmapColor));
                }
            }

            @Override
            public int getItemCount() {
                return (null != mDataSet ? mDataSet.size() : 0);
            }

            class MyViewHolder extends RecyclerView.ViewHolder {
                InterTextView name;
                AppCompatImageView imageViewCompat;

                MyViewHolder(View view) {
                    super(view);
                    name = view.findViewById(R.id.category_title);
                    imageViewCompat = view.findViewById(R.id.category_image);
                }
            }
        }

    }