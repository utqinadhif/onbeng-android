package com.nadhif.onbeng;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UserActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {
    TextView logout;
    Button updateProfile;
    EditText name, email, password, contact, location;
    Gps gps;
    double latitude, longitude;
    LatLng pos;
    SharedPreferences sp, slatlng;
    SharedPreferences.Editor editor, listOrder;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_navigate_before);

        progressBar = (ProgressBar) findViewById(R.id.progress_spinner);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);

        slatlng = getSharedPreferences("LOCATION", MODE_PRIVATE);
        String lt = slatlng.getString("latitude", null);
        String lg = slatlng.getString("longitude", null);
        if (lt != null && lg != null) {
            latitude = Double.parseDouble(lt);
            longitude = Double.parseDouble(lg);
        } else {
            latitude = longitude = 0;
        }

        logout = (TextView) findViewById(R.id.logout);
        logout.setOnClickListener(this);

        updateProfile = (Button) findViewById(R.id.saveProfile);
        updateProfile.setOnClickListener(this);

        name = (EditText) findViewById(R.id.yourName);
        email = (EditText) findViewById(R.id.yourEmail);
        password = (EditText) findViewById(R.id.yourPassword);
        contact = (EditText) findViewById(R.id.yourContact);
        location = (EditText) findViewById(R.id.yourLocation);
        location.setOnClickListener(this);
        location.setOnFocusChangeListener(this);

        sp = getSharedPreferences("SESSION", MODE_PRIVATE);
        String restoredText = sp.getString("login", null);
        if (restoredText != null) {
            name.setText(sp.getString("name", null));
            email.setText(sp.getString("username", null));
            password.setText(sp.getString("password", null));
            contact.setText(sp.getString("contact", null));
            location.setText(sp.getString("location", null));
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.pleaseLogout), Toast.LENGTH_LONG).show();

            this.finish();
            startActivity(new Intent(getApplicationContext(), LogActivity.class));
        }
    }

    private void searchLocation() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(this);
            startActivityForResult(intent, 2);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                location.setText(place.getName());
            }
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


    @Override
    public void onClick(View v) {
        editor = getSharedPreferences("SESSION", MODE_PRIVATE).edit();
        listOrder = getSharedPreferences("LIST", MODE_PRIVATE).edit();

        String n = name.getText().toString();
        String e = email.getText().toString();
        String p = password.getText().toString();
        String c = contact.getText().toString();
        String l = location.getText().toString();

        if (v == logout) {
            editor.putString("login", null);
            editor.putString("username", null);
            editor.putString("password", null);
            editor.putString("passwordh", null);
            editor.putString("contact", null);
            editor.putString("location", null);
            editor.putString("lat", null);
            editor.putString("lng", null);
            editor.commit();

            listOrder.putString("data list order", null);
            listOrder.commit();

            this.finish();
            HomeActivity.welcome.setText("");
            if (HomeActivity.welcome.getVisibility() == View.VISIBLE) {
                HomeActivity.welcome.setVisibility(View.GONE);
            }
            startActivity(new Intent(getApplicationContext(), LogActivity.class));
        } else if (v == updateProfile) {
            String restoredText = sp.getString("login", null);
            if (latitude != 0 || longitude != 0) {
                pos = new LatLng(latitude, longitude);
            } else if (restoredText != null) {
                double la = Double.parseDouble(sp.getString("lat", null));
                double lo = Double.parseDouble(sp.getString("lng", null));
                pos = new LatLng(la, lo);
            } else {
                pos = new LatLng(-6.7449933, 111.0460305);
            }
            if (!n.equals("") && !e.equals("") && !p.equals("") && !c.equals("") && !l.equals("")) {
                ContentValues cv = new ContentValues();
                cv.put("id", sp.getString("id", null));
                cv.put("name", n);
                cv.put("username", e);
                cv.put("password", Config.createHash(p));
                cv.put("contact", c);
                cv.put("location", l);
                cv.put("latlng", "(" + pos.latitude + ", " + pos.longitude + ")");
                new CurlUpdate(this, Config.url + "log_user/update", cv).execute();
            }
        } else if (v == location) {
            searchLocation();
        }

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v == location && hasFocus) {
            searchLocation();
        }
    }

    private class CurlUpdate extends Curl {
        public CurlUpdate(Context c, String url, ContentValues post) {
            super(c, url, post);
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            progressBar.setVisibility(View.GONE);
            try {
                JSONObject json = new JSONObject(s);
                Log.d("nadhif", json.toString());
                if (json.getString("ok").equals("1")) {
                    JSONArray college = json.getJSONArray("result");
                    for (int i = 0; i < college.length(); i++) {
                        JSONObject c = college.getJSONObject(i);

                        SharedPreferences.Editor editor = getSharedPreferences("SESSION", Context.MODE_PRIVATE).edit();
                        editor.putString("login", "1");
                        editor.putString("id", c.getString("id"));
                        editor.putString("name", c.getString("name"));
                        editor.putString("username", c.getString("username"));
                        editor.putString("password", password.getText().toString());
                        editor.putString("passwordh", c.getString("pass"));
                        editor.putString("contact", c.getString("contact"));
                        editor.putString("location", c.getString("location"));
                        editor.putString("lat", String.valueOf(pos.latitude));
                        editor.putString("lng", String.valueOf(pos.longitude));
                        editor.commit();

                        Toast.makeText(getApplicationContext(), "Data Updated", Toast.LENGTH_LONG).show();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
