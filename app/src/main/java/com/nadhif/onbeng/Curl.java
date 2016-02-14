package com.nadhif.onbeng;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by nadhif on 28/11/2015.
 */
public class Curl extends AsyncTask<Void, Void, String> {

    ProgressDialog pg;
    String post = "";
    String url = "";

    public Curl(Context context, String url) {
        populatePost(context, url, null);
    }

    public Curl(Context context, String url, ContentValues cv) {
        populatePost(context, url, cv);
    }

    private void populatePost(Context c, String _url, ContentValues cv) {
        url = _url;
        pg = new ProgressDialog(c);
        pg.setMessage("Please waiting . . .");
        pg.setCancelable(false);
        pg.setCanceledOnTouchOutside(false);
        if (cv != null) {
            for (String key : cv.keySet()) {
                if (!post.isEmpty()) {
                    post += "&";
                }
                try {
                    post += URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(cv.getAsString(key), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onPreExecute() {
        pg.show();
    }

    @Override
    protected String doInBackground(Void... params) {
        String output = "";
        // Defined URL  where to send data
        URLConnection conn = null;
        try {
            conn = (new URL(url)).openConnection();
            // Send POST data request
            if (!post.isEmpty()) {
                conn.setDoOutput(true);
                try {
                    OutputStreamWriter osw = null;
                    osw = new OutputStreamWriter(conn.getOutputStream());
                    osw.write(post);
                    osw.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                // Get the server response
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();

                // Read Server Response
                String line = "";

                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                output = sb.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output;
    }

    @Override
    protected void onPostExecute(String s) {
        pg.hide();
    }
}
