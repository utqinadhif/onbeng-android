package com.nadhif.onbeng;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by nadhif on 16/01/2016.
 */
public class ListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, OnLoadMoreListener {

    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    protected Handler handler;

    SwipeRefreshLayout swiper;
    CardView no_data;

    private ArrayList<DataRecycler> dataRecycler = new ArrayList<>();
    ContentValues cv;
    int pageCurrent = -1;
    int pageFetch = -1;
    int pageTotal = 0;
    boolean first = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("List Order");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_navigate_before);

        no_data = (CardView) findViewById(R.id.nodata);
        handler = new Handler();

        SharedPreferences sp = getSharedPreferences("SESSION", MODE_PRIVATE);
        String restoredText = sp.getString("login", null);
        cv = new ContentValues();
        if (restoredText != null) {
            cv.put("username", sp.getString("username", null));
            cv.put("password", sp.getString("password", null));
        }

        recyclerView = (RecyclerView) findViewById(R.id.myRecylerView);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new MyAdapter(dataRecycler, recyclerView);
        recyclerView.setAdapter(adapter);

        swiper = (SwipeRefreshLayout) findViewById(R.id.list_order_swipe);
        swiper.setSize(SwipeRefreshLayout.DEFAULT);
        swiper.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.colorPrimaryDark, R.color.colorNormal);
        swiper.setOnRefreshListener(this);

        loadData();
        adapter.setOnLoadMoreListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadData() {
        pageCurrent++;
        int p = pageCurrent + 1;
        new GetData(this, Config.url + "form/get_data/" + p, cv).execute();
    }

    @Override
    public void onRefresh() {
        dataRecycler.clear();
        pageCurrent = -1;
        loadData();
    }

    @Override
    public void onLoadMore() {
        dataRecycler.add(null);
        adapter.notifyItemInserted(dataRecycler.size() - 1);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dataRecycler.remove(dataRecycler.size() - 1);
                adapter.notifyItemRemoved(dataRecycler.size());
                if (pageFetch == pageCurrent && pageCurrent < pageTotal) {
                    loadData();
                }
                adapter.setLoaded();
            }
        }, 0);
    }

    private class GetData extends Curl {
        public GetData(Context context, String url, ContentValues cv) {
            super(context, url, cv);
        }

        @Override
        protected void onPreExecute() {
            if (swiper.getVisibility() == View.GONE) {
                swiper.setVisibility(View.VISIBLE);
            }
            swiper.setRefreshing(true);
            if (first) {
                pg.show();
                first = false;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject json = new JSONObject(s);
                if (json.getString("ok").equals("1")) {
                    JSONObject result = json.getJSONObject("result");
                    JSONArray college = result.getJSONArray("list");
                    if (college.length() > 0) {
                        no_data.setVisibility(View.GONE);
                        for (int i = 0; i < college.length(); i++) {
                            JSONObject c = college.getJSONObject(i);
                            dataRecycler.add(new DataRecycler(
                                            c.getString("id").toString(),
                                            c.getString("logo_bengkel").toString(),
                                            c.getString("name_bengkel").toString(),
                                            c.getString("date_order").toString(),
                                            c.getString("status_order").toString(),
                                            c.getString("damage_order").toString(),
                                            c.getString("detail_bengkel").toString(),
                                            c.getString("detail_order").toString()
                                    )
                            );
                            adapter.notifyItemInserted(dataRecycler.size());
                        }
                        pageTotal = result.getInt("total_page") - 1;
                        pageFetch = pageCurrent;
                        swiper.setRefreshing(false);
                    } else {
                        no_data.setVisibility(View.VISIBLE);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
