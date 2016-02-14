package com.nadhif.onbeng;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {
    GoogleMap mMap;
    TextView title, bengkelName, detail, direct, name, company, contact, email, location, lat, lng, damage, distance, price, amount;
    Dialog dialog;
    ImageButton resetButton;
    Button ok, ok_confirm, cancel_confirm, btnOrder;
    ScrollView mScrollView;
    ArrayList<DataMap> dataMaps = new ArrayList<>();
    DataMap dataItemMaps;
    LatLng focus, des;
    GpsTracker gps;
    double latitude, longitude, la, lo;
    PolylineOptions poliop;
    Polyline di;
    EditText dLocation, dDamage;
    String id_m;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("View Bengkel");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_navigate_before);

        try {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mMap = mapFragment.getMap();
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);

            mScrollView = (ScrollView) findViewById(R.id.scroll); //parent scrollview in xml, give your scrollview id value

            ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .setListener(new WorkaroundMapFragment.OnTouchListener() {
                        @Override
                        public void onTouch() {
                            mScrollView.requestDisallowInterceptTouchEvent(true);
                        }
                    });
            mapFragment.getMapAsync(this);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        title = (TextView) findViewById(R.id.titleMap);
        bengkelName = (TextView) findViewById(R.id.bengkelName);
        ContentValues cv = new ContentValues();
        cv.put("beo", "038");
        new json(this, Config.url + "json/view", cv).execute();

        detail = (TextView) findViewById(R.id.viewDetail);
        detail.setOnClickListener(this);

        direct = (TextView) findViewById(R.id.viewDirection);
        direct.setOnClickListener(this);

        resetButton = (ImageButton) findViewById(R.id.resetButton);
        resetButton.setVisibility(View.INVISIBLE);
        resetButton.setOnClickListener(this);

        btnOrder = (Button) findViewById(R.id.btnOrder);
        btnOrder.setOnClickListener(this);

        gps = new GpsTracker(this);
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        } else {
            gps.showSettingsAlert();
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
    public void onMapReady(GoogleMap googleMap) {
        SharedPreferences sp = getSharedPreferences("SESSION", MODE_PRIVATE);
        String restoredText = sp.getString("login", null);
        if (latitude != 0.0 || longitude != 0.0) {
            focus = new LatLng(latitude, longitude);
        } else if (restoredText != null) {
            la = Double.parseDouble(sp.getString("lat", null));
            lo = Double.parseDouble(sp.getString("lng", null));
            focus = new LatLng(la, lo);
        } else {
            focus = new LatLng(-6.7449933, 111.0460305);
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(focus, 11.0f));
        mMap.addMarker(new MarkerOptions()
                        .position(focus)
                        .title("Your Location")
                        .snippet("1")
                        .alpha(0.7f)
        );
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String snippet = String.valueOf(marker.getSnippet());
                String titles = String.valueOf(marker.getTitle());
                switch (snippet) {
                    case "1":
                        Toast.makeText(getApplicationContext(), "You in Here", Toast.LENGTH_LONG).show();
                        break;
                    default:
                        if (di != null) {
                            di.remove();
                        }
                        resetButton.setVisibility(View.VISIBLE);
                        title.setText(snippet);
                        bengkelName.setText(titles);
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        String id_marker = (String) title.getText();
        SharedPreferences sp = getSharedPreferences("SESSION", MODE_PRIVATE);
        String restoredText = sp.getString("login", null);
        if (v == detail) {
            if (!id_marker.equals("0")) {
                dialog = new Dialog(this);
                dialog.setContentView(R.layout.pop_detail);
                dialog.setTitle("Data Bengkel");

                bengkelName = (TextView) dialog.findViewById(R.id.bengkelName);
                company = (TextView) dialog.findViewById(R.id.bengkelCompany);
                contact = (TextView) dialog.findViewById(R.id.bengkelContact);
                email = (TextView) dialog.findViewById(R.id.bengkelEmail);
                location = (TextView) dialog.findViewById(R.id.bengkelLocation);
                lat = (TextView) dialog.findViewById(R.id.bengkelLat);
                lng = (TextView) dialog.findViewById(R.id.bengkelLng);
                ok = (Button) dialog.findViewById(R.id.ok);

                for (int a = 0; a < dataMaps.size(); a++) {
                    if (dataMaps.get(a).getId_marker().equals(id_marker)) {
                        bengkelName.setText(dataMaps.get(a).getName());
                        company.setText(dataMaps.get(a).getCompany());
                        contact.setText(dataMaps.get(a).getContact());
                        email.setText(dataMaps.get(a).getEmail());
                        location.setText(dataMaps.get(a).getLocation());
                        lat.setText(dataMaps.get(a).getLat());
                        lng.setText(dataMaps.get(a).getLng());
                    }
                }

                ok.setOnClickListener(this);

                dialog.show();
            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.fastOrder) + " selected", Toast.LENGTH_LONG).show();
            }
        } else if (v == resetButton) {
            ImageButton resetButton = (ImageButton) v;
            resetButton.setVisibility(View.INVISIBLE);
            title.setText("0");
            bengkelName.setText(getResources().getString(R.string.fastOrder));
            if (di != null) {
                di.remove();
            }
        } else if (v == ok) {
            dialog.dismiss();
        } else if (v == direct) {
            if (!id_marker.equals("0")) {
                if (di != null) {
                    di.remove();
                }
                for (int a = 0; a < dataMaps.size(); a++) {
                    if (dataMaps.get(a).getId_marker().equals(id_marker)) {
                        String lt = dataMaps.get(a).getLat();
                        String lg = dataMaps.get(a).getLng();
                        des = new LatLng(Double.parseDouble(lt), Double.parseDouble(lg));
                    }
                }

                String googleUrl = new Config().googleUrl(focus, des);
                new Direction(this, googleUrl).execute();
            } else {
                Toast.makeText(getApplicationContext(), "No bengkel selected", Toast.LENGTH_LONG).show();
            }
        } else if (v == btnOrder) {
            dDamage = (EditText) findViewById(R.id.damage);
            dLocation = (EditText) findViewById(R.id.detailLocation);
            if (!dDamage.getText().toString().matches("") && !dLocation.getText().toString().matches("")) {
                if (restoredText != null) {
                    ContentValues cv = new ContentValues();
                    cv.put("username", sp.getString("username", null));
                    cv.put("password", sp.getString("password", null));
                    cv.put("latlng", "(" + sp.getString("lat", null) + ", " + sp.getString("lng", null) + ")");
                    if (!id_marker.equals("0")) {
                        cv.put("id_marker", id_marker);
                    }
                    new searchPrice(this, Config.url + "form/search_price", cv).execute();
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.pleaseLogout), Toast.LENGTH_LONG).show();

                    this.finish();
                    startActivity(new Intent(getApplicationContext(), LogActivity.class));
                }
            } else {
                Toast.makeText(getApplicationContext(), "Your detail is empty", Toast.LENGTH_LONG).show();
            }
        } else if (v == ok_confirm) {
            ContentValues cv = new ContentValues();
            String id_markers;
            int type;
            if (!id_marker.equals("0")) {
                id_markers = id_marker;
                type = 2;
            } else {
                id_markers = id_m;
                type = 1;
            }
            cv.put("username", sp.getString("username", null));
            cv.put("password", sp.getString("password", null));

            cv.put("id_marker", id_markers);
            cv.put("damage", dDamage.getText().toString());
            cv.put("detail_location", dLocation.getText().toString());
            cv.put("latlng", "(" + sp.getString("lat", null) + ", " + sp.getString("lng", null) + ")");
            cv.put("type", type);

            new order(this, Config.url + "form/request", cv).execute();
        } else if (v == cancel_confirm) {
            dialog.dismiss();
        }
    }

    private void returnExit() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Confirmation")
                .setMessage("Order again?")
                .setPositiveButton("Yes, I want", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                    }

                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        startActivity(new Intent(getApplicationContext(), ListActivity.class));
                    }

                })
                .create()
                .show();
    }

    private class json extends Curl {
        public json(Context c, String url, ContentValues post) {
            super(c, url, post);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject json = new JSONObject(s);
                if (json.getString("ok").equals("1")) {
                    JSONArray college = json.getJSONArray("result");
                    for (int i = 0; i < college.length(); i++) {
                        JSONObject c = college.getJSONObject(i);
                        double latC = Double.parseDouble(c.getString("lat"));
                        double lngC = Double.parseDouble(c.getString("lng"));
                        LatLng marker = new LatLng(latC, lngC);
                        mMap.addMarker(new MarkerOptions()
                                        .position(marker)
                                        .title(c.getString("name"))
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon))
                                        .snippet(c.getString("id_marker"))
                        );
                        dataItemMaps = new DataMap(
                                c.getString("id").toString(),
                                c.getString("id_marker").toString(),
                                c.getString("name").toString(),
                                c.getString("company").toString(),
                                c.getString("contact").toString(),
                                c.getString("email").toString(),
                                c.getString("location").toString(),
                                c.getString("lat").toString(),
                                c.getString("lng").toString());
                        dataMaps.add(dataItemMaps);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class Direction extends Curl {
        ArrayList<LatLng> puntos;

        public Direction(Context c, String url) {
            super(c, url);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject json = new JSONObject(s);
                JSONArray routeObject = json.getJSONArray("routes");
                JSONObject routes = routeObject.getJSONObject(0);
                JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
                String encodedString = overviewPolylines.getString("points");
                puntos = decodePoly(encodedString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            poliop = new PolylineOptions();
            for (int i = 0; i < puntos.size(); i++) {
                poliop.add(new LatLng(puntos.get(i).latitude, puntos.get(i).longitude));
            }
            //puntos is an array where the array returned by the decodePoly method are stored
            poliop.color(Color.RED).width(2);
            di = mMap.addPolyline(poliop);
        }

        private ArrayList<LatLng> decodePoly(String encoded) {
            ArrayList<LatLng> poly = new ArrayList<>();
            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;

            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                LatLng p = new LatLng((((double) lat / 1E5)), (((double) lng / 1E5)));
                poly.add(p);
            }

//            for (int i = 0; i < poly.size(); i++) {
//                Log.i("Location", "Point sent: Latitude: " + poly.get(i).latitude + " Longitude: " + poly.get(i).longitude);
//            }
            return poly;
        }
    }

    private class searchPrice extends Curl {
        Context context;

        public searchPrice(Context c, String url, ContentValues post) {
            super(c, url, post);
            context = c;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject json = new JSONObject(s);
                if (json.getString("ok").equals("1")) {
                    SharedPreferences sp = getSharedPreferences("SESSION", MODE_PRIVATE);
                    dialog = new Dialog(context);
                    dialog.setContentView(R.layout.pop_confirm);
                    dialog.setTitle("Detail Order");

                    name = (TextView) dialog.findViewById(R.id.yourName);
                    damage = (TextView) dialog.findViewById(R.id.yourDamage);
                    location = (TextView) dialog.findViewById(R.id.yourLocation);

                    bengkelName = (TextView) dialog.findViewById(R.id.bengkelName);
                    company = (TextView) dialog.findViewById(R.id.bengkelCompany);
                    contact = (TextView) dialog.findViewById(R.id.bengkelContact);

                    distance = (TextView) dialog.findViewById(R.id.orderDistance);
                    price = (TextView) dialog.findViewById(R.id.orderPrice);
                    amount = (TextView) dialog.findViewById(R.id.orderAmount);

                    ok_confirm = (Button) dialog.findViewById(R.id.ok_confirm);
                    cancel_confirm = (Button) dialog.findViewById(R.id.cancel_confirm);

                    name.setText(sp.getString("name", null));
                    damage.setText(dDamage.getText().toString());
                    location.setText(dLocation.getText().toString());

                    String id_marker = (String) title.getText();

                    if (!id_marker.equals("0")) {
                        for (int a = 0; a < dataMaps.size(); a++) {
                            if (dataMaps.get(a).getId_marker().equals(id_marker)) {
                                bengkelName.setText(dataMaps.get(a).getName());
                                company.setText(dataMaps.get(a).getCompany());
                                contact.setText(dataMaps.get(a).getContact());
                            }
                        }
                    } else {
                        bengkelName.setText(getResources().getString(R.string.fastOrder));
                        company.setText("-");
                        contact.setText("-");
                    }

                    JSONArray college = json.getJSONArray("result");
                    for (int i = 0; i < college.length(); i++) {
                        JSONObject c = college.getJSONObject(i);
                        distance.setText(c.getString("distance"));
                        price.setText(c.getString("price"));
                        amount.setText(c.getString("amount"));
                        id_m = c.getString("id_marker");
                    }

                    ok_confirm.setOnClickListener((View.OnClickListener) context);
                    cancel_confirm.setOnClickListener((View.OnClickListener) context);

                    dialog.show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class order extends Curl {
        public order(Context c, String url, ContentValues post) {
            super(c, url, post);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject json = new JSONObject(s);
                if (json.getString("ok").equals("1")) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.orderProcessed), Toast.LENGTH_LONG).show();
                    returnExit();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
