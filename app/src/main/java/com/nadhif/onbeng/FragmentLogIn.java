package com.nadhif.onbeng;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nadhif on 05/12/2015.
 */
public class FragmentLogIn extends Fragment implements View.OnClickListener {
    EditText email, password;
    Button login;
    static double latitude, longitude;
    LatLng pos;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log_in, container, false);

        SharedPreferences sp = getActivity().getSharedPreferences("SESSION", Context.MODE_PRIVATE);
        String restoredText = sp.getString("login", null);
        if (latitude != 0.0 || longitude != 0.0) {
            pos = new LatLng(latitude, longitude);
        } else if (restoredText != null) {
            double la = Double.parseDouble(sp.getString("lat", null));
            double lo = Double.parseDouble(sp.getString("lng", null));
            pos = new LatLng(la, lo);
        } else {
            pos = new LatLng(-6.7449933, 111.0460305);
        }

        email = (EditText) view.findViewById(R.id.firstEmail);
        password = (EditText) view.findViewById(R.id.firstPassword);
        login = (Button) view.findViewById(R.id.login);
        login.setOnClickListener(this);

        return view;
    }

    public static Fragment newInstance(Context context, double lat, double lng) {
        FragmentLogIn fa = new FragmentLogIn();
        latitude = lat;
        longitude = lng;
        return fa;
    }

    @Override
    public void onClick(View v) {
        String e = email.getText().toString();
        String p = password.getText().toString();
        if (v == login) {
            if (!e.equals("") && !p.equals("")) {
                ContentValues cv = new ContentValues();
                cv.put("username", e);
                cv.put("password", Config.createHash(p));
                new CurlLogin(getContext(), Config.url + "log_user/login", cv).execute();
            } else {
                Config.toast(getContext(), "One of data is empty.");
            }

        }
    }

    private class CurlLogin extends Curl {
        public CurlLogin(Context c, String url, ContentValues post) {
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

                        SharedPreferences.Editor editor = getActivity().getSharedPreferences("SESSION", Context.MODE_PRIVATE).edit();
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

                        Config.toast(getContext(), "Data Found\nWelcome " + c.getString("name"));
                        if(HomeActivity.welcome.getVisibility() == View.GONE){
                            HomeActivity.welcome.setVisibility(View.VISIBLE);
                        }
                        HomeActivity.welcome.setText("Welcome " + c.getString("name"));
                        getActivity().finish();
                        startActivity(new Intent(getActivity().getApplicationContext(), UserActivity.class));
                    }
                } else {
                    email.setText("");
                    password.setText("");
                    email.requestFocus();
                    Config.toast(getContext(), json.getString("msg"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
