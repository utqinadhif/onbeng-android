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
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nadhif on 05/12/2015.
 */
public class FragmentSignUp extends Fragment implements View.OnClickListener {
    Button signup;
    EditText name, email, password, contact, location;
    TextView latlng;
    public static double latitude, longitude;
    LatLng pos;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

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

        name = (EditText) view.findViewById(R.id.firstName);
        email = (EditText) view.findViewById(R.id.firstEmail);
        password = (EditText) view.findViewById(R.id.firstPassword);
        contact = (EditText) view.findViewById(R.id.firstContact);
        location = (EditText) view.findViewById(R.id.firstLocation);

        latlng = (TextView) view.findViewById(R.id.firstLatLng);
        latlng.setText("(" + pos.latitude + ", " + pos.longitude + ")");

        signup = (Button) view.findViewById(R.id.saveProfile);
        signup.setOnClickListener(this);

        return view;
    }

    public static Fragment newInstance(Context context, double lat, double lng) {
        FragmentSignUp fa = new FragmentSignUp();
        latitude = lat;
        longitude = lng;
        return fa;
    }

    @Override
    public void onClick(View v) {
        String n = String.valueOf(name.getText());
        String e = String.valueOf(email.getText());
        String p = String.valueOf(password.getText());
        String c = String.valueOf(contact.getText());
        String l = String.valueOf(location.getText());

        if (v == signup) {
            if (!n.equals("") && !e.equals("") && !p.equals("") && !c.equals("") && !l.equals("")) {
                ContentValues cv = new ContentValues();
                cv.put("name", n);
                cv.put("username", e);
                cv.put("password", p);
                cv.put("contact", c);
                cv.put("location", l);
                cv.put("latlng", "(" + pos.latitude + ", " + pos.longitude + ")");
                new CurlSignUp(getContext(), Config.url + "log_user/signup", cv).execute();
            }
        }
    }

    private class CurlSignUp extends Curl {
        public CurlSignUp(Context c, String url, ContentValues post) {
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
                        editor.putString("password", c.getString("pass"));
                        editor.putString("contact", c.getString("contact"));
                        editor.putString("location", c.getString("location"));
                        editor.putString("lat", String.valueOf(pos.latitude));
                        editor.putString("lng", String.valueOf(pos.longitude));
                        editor.commit();

                        getActivity().finish();
                        HomeActivity.welcome.setText("Welcome " + c.getString("name"));
                        startActivity(new Intent(getActivity().getApplicationContext(), UserActivity.class));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
