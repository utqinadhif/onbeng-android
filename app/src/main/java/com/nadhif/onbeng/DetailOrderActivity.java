package com.nadhif.onbeng;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class DetailOrderActivity extends AppCompatActivity {

    Intent intent;
    String id, detail_bengkel, detail_order, status, status_text;
    JSONObject json_detail_bengkel, json_detail_order;
    TextView od_status, od_company, od_contact, od_email, od_location, od_price, od_latlang, od_y_location, od_y_latlng, od_y_totalDistance, od_y_total_price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intent = getIntent();
        id = intent.getStringExtra("id").split(" ")[0].replace("-", "");
        id += intent.getStringExtra("id").split(" ")[1].replace(":", "");
        id += intent.getStringExtra("id").split(" ")[2];
        status = intent.getStringExtra("status order");
        status_text = intent.getStringExtra("status order text");
        detail_bengkel = intent.getStringExtra("detail bengkel");
        detail_order = intent.getStringExtra("detail order");

        setContentView(R.layout.activity_detail_order);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(id);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_navigate_before);

        od_status = (TextView) findViewById(R.id.od_status);

        od_company = (TextView) findViewById(R.id.od_company);
        od_contact = (TextView) findViewById(R.id.od_contact);
        od_email = (TextView) findViewById(R.id.od_email);
        od_location = (TextView) findViewById(R.id.od_location);
        od_price = (TextView) findViewById(R.id.od_price);
        od_latlang = (TextView) findViewById(R.id.od_latlng);

        od_y_location = (TextView) findViewById(R.id.od_y_location);
        od_y_latlng = (TextView) findViewById(R.id.od_y_latlng);
        od_y_totalDistance = (TextView) findViewById(R.id.od_y_totalDistance);
        od_y_total_price = (TextView) findViewById(R.id.od_y_totalPrice);

        od_status.setText(status_text.substring(0, 1).toUpperCase() + status_text.substring(1));
        int sts = Integer.parseInt(status);
        switch (sts){
            case 0:
                od_status.setTextColor(ContextCompat.getColor(this, R.color.yellow));
                break;
            case 1:
                od_status.setTextColor(ContextCompat.getColor(this, R.color.blue));
                break;
            case 2:
                od_status.setTextColor(ContextCompat.getColor(this, R.color.green));
                break;
            case 3:
                od_status.setTextColor(ContextCompat.getColor(this, R.color.red));
                break;
            case 4:
                od_status.setTextColor(ContextCompat.getColor(this, R.color.purple));
                break;
            default:
                break;
        }

        try {
            json_detail_bengkel = new JSONObject(detail_bengkel);
            od_company.setText(json_detail_bengkel.getString("company"));
            od_contact.setText(json_detail_bengkel.getString("contact"));
            od_email.setText(json_detail_bengkel.getString("email"));
            od_location.setText(json_detail_bengkel.getString("location"));
            od_price.setText(json_detail_bengkel.getString("price"));
            od_latlang.setText(json_detail_bengkel.getString("lat") + ", " + json_detail_bengkel.getString("lng"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json_detail_order = new JSONObject(detail_order);
            od_y_location.setText(json_detail_order.getString("detail_location"));
            od_y_latlng.setText(json_detail_order.getString("lat") + ", " + json_detail_order.getString("lng"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
