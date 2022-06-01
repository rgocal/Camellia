package com.gocalsd.bliss.camellia.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.gocalsd.bliss.camellia.Adapters.GenericWallpapersAdapter;
import com.gocalsd.bliss.camellia.Base.BaseActivity;
import com.gocalsd.bliss.camellia.Utils.ItemClickListener;
import com.gocalsd.bliss.camellia.Views.NxToolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ViewCategoryActivity extends BaseActivity {


    ArrayList<HashMap<String, String>> arraylist = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayoutCompat rootView = new LinearLayoutCompat(this);
        NxToolbar toolbar = new NxToolbar(this);
        RecyclerView categoryListView = new RecyclerView(this);

        rootView.setOrientation(LinearLayoutCompat.VERTICAL);
        rootView.addView(toolbar, 0);
        rootView.addView(categoryListView, 1);

        setContentView(rootView);

        String categoryLabel = getIntent().getStringExtra("CAT_TITLE");
        String categoryList = getIntent().getStringExtra("CAT_LIST");

        toolbar.setBackgroundColor(getBackgroundColor());
        toolbar.setItemColor(getTextColor());
        toolbar.centerToolbar(true);
        toolbar.setTitle("Loading Category");

        RequestQueue queue = Volley.newRequestQueue(this);

        //show some sort of progressview
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, categoryList, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //hide some sort of progressview
                toolbar.setTitle(categoryLabel);

                try {
                    JSONArray jsonarray = new JSONObject(response.toString()).getJSONArray("wallpaper");

                    for (int i = 0; i < jsonarray.length(); i++) {
                        HashMap<String, String> map = new HashMap<>();
                        JSONObject jsonobject = jsonarray.getJSONObject(i);

                        map.put("label", jsonobject.getString("name"));
                        map.put("url", jsonobject.getString("wall_url"));
                        map.put("author", jsonobject.getString("author"));

                        arraylist.add(map);
                    }

                    GenericWallpapersAdapter listAdapter = new GenericWallpapersAdapter(ViewCategoryActivity.this, arraylist, new ItemClickListener() {
                        @Override
                        public void onItemClick(View v, int position) {

                        }

                        @Override
                        public void onItemLongClick(View v, int position) {

                        }
                    }, getPrimaryColor());
                    categoryListView.setAdapter(listAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // below line is use to display a toast message along with our error.
                Toast.makeText(ViewCategoryActivity.this, "Fail to get data..", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
        queue.add(jsonObjectRequest);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(position == 0){
                    return 2;
                }
                return 1;
            }
        });

        categoryListView.setLayoutManager(layoutManager);
        categoryListView.setHasFixedSize(true);
        categoryListView.setClipToPadding(true);
    }

}
