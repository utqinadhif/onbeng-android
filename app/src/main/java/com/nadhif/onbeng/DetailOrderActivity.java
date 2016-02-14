package com.nadhif.onbeng;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.json.JSONException;
import org.json.JSONObject;

public class DetailOrderActivity extends AppCompatActivity {

    Intent intent;
    String id, detail_bengkel, detail_order;
    JSONObject json_detail_bengkel, json_detail_order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intent = getIntent();
        id = intent.getStringExtra("id").split(" ")[0].replace("-", "");
        id += intent.getStringExtra("id").split(" ")[1].replace(":", "");
        id += intent.getStringExtra("id").split(" ")[2];
        detail_bengkel = intent.getStringExtra("detail bengkel");
        try {
            json_detail_bengkel = new JSONObject(detail_bengkel);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        detail_order = intent.getStringExtra("detail order");
        try {
            json_detail_order = new JSONObject(detail_order);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_detail_order);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(id);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_navigate_before);
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
}
