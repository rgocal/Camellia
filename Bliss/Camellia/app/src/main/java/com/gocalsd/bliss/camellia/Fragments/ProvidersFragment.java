package com.gocalsd.bliss.camellia.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gocalsd.bliss.camellia.Adapters.WallpaperProviderAdapter;
import com.gocalsd.bliss.camellia.Data.WallpaperProviderData;
import com.gocalsd.bliss.camellia.Models.WallpaperDataInfo;
import com.gocalsd.bliss.camellia.R;
import com.gocalsd.bliss.camellia.Utils.ItemClickListener;
import com.kieronquinn.monetcompat.app.MonetFragment;

import java.util.ArrayList;

public class ProvidersFragment extends MonetFragment {

    private Context mContext;
    private ArrayList<WallpaperDataInfo> wallpaperProviders = new ArrayList<>();

    public static ProvidersFragment newInstance() {
        return new ProvidersFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_list_layout, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.providers_list);

        ItemClickListener itemClickListener = new ItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

            }

            @Override
            public void onItemLongClick(View v, int position) {

            }
        };

        wallpaperProviders = WallpaperProviderData.getWallpaperProviders(mContext);
        WallpaperProviderAdapter wallpaperProviderAdapter = new WallpaperProviderAdapter(itemClickListener, wallpaperProviders);

        recyclerView.setAdapter(wallpaperProviderAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 2, RecyclerView.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setClipToPadding(true);

        return rootView;
    }

}
