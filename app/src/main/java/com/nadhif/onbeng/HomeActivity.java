package com.nadhif.onbeng;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {
    ImageButton search, user, list, about;
    GpsTracker gps;
    ImageView banner;
    public static TextView welcome;
    SharedPreferences.Editor editor;
    SharedPreferences surl;
    Dialog dialog;
    EditText host;
    Button hostSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        search = (ImageButton) findViewById(R.id.search);
        user = (ImageButton) findViewById(R.id.user);
        list = (ImageButton) findViewById(R.id.list);
        about = (ImageButton) findViewById(R.id.about);

        search.setOnClickListener(this);
        user.setOnClickListener(this);
        list.setOnClickListener(this);
        about.setOnClickListener(this);

        banner = (ImageView) findViewById(R.id.banner);
        banner.setOnLongClickListener(this);

        gps = new GpsTracker(this);
        if (!gps.canGetLocation()) {
            gps.showSettingsAlert();
        }

        SharedPreferences sp = getSharedPreferences("SESSION", MODE_PRIVATE);
        String restoredText = sp.getString("login", null);
        welcome = (TextView) findViewById(R.id.welcome);
        if (restoredText != null) {
            if (this.welcome.getVisibility() == View.GONE) {
                this.welcome.setVisibility(View.VISIBLE);
            }
            this.welcome.setText("Welcome " + sp.getString("name", null));
        } else {
            this.welcome.setText("");
            if (this.welcome.getVisibility() == View.VISIBLE) {
                this.welcome.setVisibility(View.GONE);
            }
        }

        surl = getSharedPreferences("URL", MODE_PRIVATE);
        String url = surl.getString("url", null);
        if (url != null) {
            Config.url = url;
        } else {
            String Url = "http://nadhif.pe.hu/";
            editor = getSharedPreferences("URL", Context.MODE_PRIVATE).edit();
            editor.putString("url", Url);
            editor.commit();
            Config.url = Url;
        }
    }

    private void returnExit() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Confirmation")
                .setMessage("Are you sure to exit from application?")
                .setPositiveButton("Yes, I'm Sure", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Stop the activity
                        finish();
                        System.exit(0);
                    }

                })
                .setNegativeButton("No", null)
                .create()
                .show();
    }

    @Override
    public void onBackPressed() {
        returnExit();
    }

    @Override
    public void onClick(View v) {
        SharedPreferences sp = getSharedPreferences("SESSION", MODE_PRIVATE);
        String restoredText = sp.getString("login", null);
        if (v == search) {
            startActivity(new Intent(getApplicationContext(), SearchActivity.class));
        } else if (v == user) {
            if (restoredText != null) {
                startActivity(new Intent(getApplicationContext(), UserActivity.class));
            } else {
                startActivity(new Intent(getApplicationContext(), LogActivity.class));
            }
        } else if (v == list) {
            if (restoredText != null) {
                startActivity(new Intent(getApplicationContext(), ListActivity.class));
            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.pleaseLogout), Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(), LogActivity.class));
            }
        } else if (v == about) {
            startActivity(new Intent(getApplicationContext(), AboutActivity.class));
        } else if (v == hostSave) {
            String nUrl = host.getText().toString();
            editor = getSharedPreferences("URL", Context.MODE_PRIVATE).edit();
            editor.putString("url", nUrl);
            editor.commit();
            Config.url = nUrl;
            dialog.dismiss();
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (v == banner) {
            dialog = new Dialog(this);
            dialog.setTitle("Change Host");
            dialog.setContentView(R.layout.pop_change_host);

            surl = getSharedPreferences("URL", MODE_PRIVATE);
            String url = surl.getString("url", null);

            host = (EditText) dialog.findViewById(R.id.host);
            hostSave = (Button) dialog.findViewById(R.id.hostSave);

            host.setText(url);

            hostSave.setOnClickListener(this);

            dialog.show();
        }
        return false;
    }
}
