package com.gocalsd.bliss.camellia.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.gocalsd.bliss.camellia.R;
import com.gocalsd.bliss.camellia.Settings.SettingsProvider;
import com.gocalsd.bliss.camellia.Utils.ColorProvider;
import com.gocalsd.bliss.camellia.Utils.CompatImageViewLoader;
import com.gocalsd.bliss.camellia.Utils.ImageUtils;
import com.gocalsd.bliss.camellia.Utils.ItemClickListener;
import com.gocalsd.bliss.camellia.Views.InterTextView;

import java.util.ArrayList;
import java.util.HashMap;

import glimpse.glide.GlimpseTransformation;


public class GenericCategoryAdapter extends RecyclerView.Adapter<GenericCategoryAdapter.MyViewHolder>  {

    ArrayList<HashMap<String, String>> mDataSet;
    private final Context mContext;
    private final ItemClickListener clickListener;
    private final int priColor;

    private Bitmap bitmap;
    private int bitmapColor = 0;
    private int newBitmapColor = 0;
    private Drawable placeHolderDrawable;

    public GenericCategoryAdapter(Context context, ArrayList<HashMap<String, String>> list, ItemClickListener clickListener, int primaryColor) {
        this.mContext = context;
        this.mDataSet = list;
        this.clickListener = clickListener;
        this.priColor = primaryColor;
        setHasStableIds(true);
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
        final View itemView = LayoutInflater.from(mContext).inflate(R.layout.category_list_item, null);
        final MyViewHolder mvh = new MyViewHolder(itemView);
        itemView.setOnClickListener(v -> clickListener.onItemClick(v, mvh.getBindingAdapterPosition()));
        return mvh;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.name.setText(getCategoryLabel(position));

        if(position == 0){
            holder.itemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 510));
        }

        Glide.with(mContext)
                .load(getCustomUrl(mContext, position))
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .transform(new GlimpseTransformation())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        newBitmapColor = ColorUtils.setAlphaComponent(priColor, 128);

                        holder.name.setBackgroundColor(newBitmapColor);

                        if (ColorProvider.isDark(bitmapColor)) {
                            holder.name.setTextColor(Color.WHITE);
                        } else {
                            holder.name.setTextColor(ColorProvider.darkenColor(bitmapColor));
                        }

                        if(getCategoryBanner(position) == null) {
                            placeHolderDrawable = ContextCompat.getDrawable(mContext, R.drawable.ic_terrain);
                            placeHolderDrawable.setTint(priColor);
                            holder.imageViewCompat.setBackgroundDrawable(placeHolderDrawable);
                            return false;
                        }else {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    placeHolderDrawable = ContextCompat.getDrawable(mContext, R.drawable.ic_lotus);
                                    placeHolderDrawable.setTint(priColor);
                                    holder.imageViewCompat.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,510));
                                    holder.imageViewCompat.setImageDrawable(placeHolderDrawable);
                                }
                            }, 1200);
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        CompatImageViewLoader.animate(holder.imageViewCompat, resource).setDuration(2000).start();
                        bitmap = ImageUtils.drawableToBitmap(resource);
                        bitmapColor = ColorProvider.getVibrantColor(bitmap);
                        newBitmapColor = ColorUtils.setAlphaComponent(bitmapColor, 128);
                        holder.name.setBackgroundColor(newBitmapColor);

                        if (ColorProvider.isDark(bitmapColor)) {
                            holder.name.setTextColor(Color.WHITE);
                        } else {
                            holder.name.setTextColor(ColorProvider.darkenColor(bitmapColor));
                        }
                        return false;
                    }
                })
                .into(holder.imageViewCompat);
    }

    public String getCategoryLabel(int position){
        return mDataSet.get(position).get("category");
    }

    public String getCategoryBanner(int position){
        return mDataSet.get(position).get("category_default_banner");
    }

    public String getCustomUrl(Context context, int pos) {
        return SettingsProvider.get(context).getString(getCategoryLabel(pos) + ".url", getCategoryBanner(pos));
    }

    public void setCustomUrl(Context context, int pos, String customUrl){
        SettingsProvider.get(context).edit().putString(getCategoryLabel(pos) + ".url", customUrl).apply();
    }

    @Override
    public int getItemCount() {
        return (null != mDataSet ? mDataSet.size() : 0);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        InterTextView name;
        AppCompatImageView imageViewCompat;

        MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.category_title);
            imageViewCompat = view.findViewById(R.id.category_image);
        }
    }
}