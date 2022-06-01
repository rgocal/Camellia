package com.gocalsd.bliss.camellia.Adapters;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;

import com.gocalsd.bliss.camellia.Models.WallpaperDataInfo;
import com.gocalsd.bliss.camellia.R;
import com.gocalsd.bliss.camellia.Utils.ColorProvider;
import com.gocalsd.bliss.camellia.Utils.CompatImageViewLoader;
import com.gocalsd.bliss.camellia.Utils.ImageUtils;
import com.gocalsd.bliss.camellia.Utils.ItemClickListener;
import com.gocalsd.bliss.camellia.Utils.PackageUtilities;
import com.gocalsd.bliss.camellia.Views.InterTextView;

import java.util.ArrayList;

public class WallpaperProviderAdapter extends RecyclerView.Adapter<WallpaperProviderAdapter.MyViewHolder> {

    private final ArrayList<WallpaperDataInfo> mDataSet;
    private final ItemClickListener clickListener;

    public WallpaperProviderAdapter(ItemClickListener clickListener, ArrayList<WallpaperDataInfo> dataInfos) {
        this.clickListener = clickListener;
        this.mDataSet = dataInfos;
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
    public WallpaperProviderAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.wallpaper_item, null);
        final WallpaperProviderAdapter.MyViewHolder mvh = new MyViewHolder(itemView);

        itemView.setOnClickListener(v -> clickListener.onItemClick(v, mvh.getBindingAdapterPosition()));

        itemView.setOnLongClickListener(v -> {
            clickListener.onItemLongClick(v, mvh.getBindingAdapterPosition());
            return true;
        });

        return mvh;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onBindViewHolder(@NonNull final WallpaperProviderAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.name.setText(mDataSet.get(position).getCategoryName());

        holder.imageViewCompat.setBackgroundDrawable(mDataSet.get(position).getBackground());
        CompatImageViewLoader.animate(holder.imageViewCompat, mDataSet.get(position).getBackground()).setDuration(2000).start();

        if (!mDataSet.get(position).isIcon()) {
            holder.imageViewCompat.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        }

        Bitmap bitmap;
        try {
            bitmap = ImageUtils.drawableToBitmap(mDataSet.get(position).getBackground());
        } catch (NullPointerException ignored) {
            bitmap = ImageUtils.drawableToBitmap(new PackageUtilities(holder.itemView.getContext()).getPackageIcon(mDataSet.get(position).getCategoryPackage()));
        }

        int bitmapColor = ColorProvider.getDominantColor(bitmap);
        int newBitmapColor = ColorUtils.setAlphaComponent(bitmapColor, 128);
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

    protected static class MyViewHolder extends RecyclerView.ViewHolder {
        InterTextView name;
        AppCompatImageView imageViewCompat;

        MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.category_title);
            imageViewCompat = view.findViewById(R.id.category_image);
        }
    }
}