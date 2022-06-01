package com.gocalsd.bliss.camellia.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.gocalsd.bliss.camellia.Base.BaseActivity;
import com.gocalsd.bliss.camellia.Adapters.GenericItemAdapter;
import com.gocalsd.bliss.camellia.R;
import com.gocalsd.bliss.camellia.Utils.ItemClickListener;
import com.kieronquinn.monetcompat.app.MonetFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class CreationsFragment extends MonetFragment {

    private Context mContext;
    ArrayList<HashMap<String, String>> arraylist = new ArrayList<>();

    public static CreationsFragment newInstance() {
        return new CreationsFragment();
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

        BaseActivity activity = (BaseActivity) getActivity();

        RecyclerView recyclerView = rootView.findViewById(R.id.providers_list);

        String catalogue = mContext.getResources().getString(R.string.camellia_creations);
        RequestQueue queue = Volley.newRequestQueue(mContext);

        //show some sort of progressview
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, catalogue, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //hide some sort of progressview

                try {
                    JSONArray jsonarray = new JSONObject(response.toString()).getJSONArray("creations");

                    for (int i = 0; i < jsonarray.length(); i++) {
                        HashMap<String, String> map = new HashMap<>();
                        JSONObject jsonobject = jsonarray.getJSONObject(i);

                        map.put("creation_item", jsonobject.getString("creation_item"));
                        map.put("creation_icon", jsonobject.getString("creation_item"));
                        map.put("creation_url", jsonobject.getString("creation_url"));

                        arraylist.add(map);
                    }

                    ItemClickListener itemClickListener = new ItemClickListener() {
                        @Override
                        public void onItemClick(View v, int position) {

                        }

                        @Override
                        public void onItemLongClick(View v, int position) {
                            //does nothing
                        }
                    };

                    GenericItemAdapter categoryListAdapter = new GenericItemAdapter(mContext, arraylist, itemClickListener, activity.getPrimaryColor());
                    recyclerView.setAdapter(categoryListAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // below line is use to display a toast message along with our error.
                Toast.makeText(mContext, "Fail to get data..", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
        queue.add(jsonObjectRequest);

        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 2, RecyclerView.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setClipToPadding(true);
        return rootView;
    }
}

