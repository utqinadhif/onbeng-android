package com.nadhif.onbeng;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by nadhif on 01/12/2015.
 */
public class Config {
    String googleUrl, sensor, originLat, originLng, destinationLat, destinationLng;

    public static String url;

    public String googleUrl(LatLng o, LatLng d) {
        originLat = Double.toString(o.latitude);
        originLng = Double.toString(o.longitude);

        destinationLat = Double.toString(d.latitude);
        destinationLng = Double.toString(d.longitude);

        sensor = "false";

        googleUrl = "https://maps.googleapis.com/maps/api/directions/json" +
                "?origin=" + originLat + "," + originLng +
                "&destination=" + destinationLat + "," + destinationLng +
                "&sensor=" + sensor;
        return googleUrl;
    }

    public static String hash(String str, String type) {
        try {
            MessageDigest digest = MessageDigest
                    .getInstance(type);
            digest.update(str.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String createHash(String str) {
        String origin = hash(str, "MD5");
        String newOr = hash(new StringBuilder(str).reverse().toString(), "MD5");
        String originmd5 = new StringBuilder(origin).reverse().toString();
        String newOrmd5 = new StringBuilder(newOr).reverse().toString();
        String pass = 38 + originmd5.substring(0, 7) + origin.substring(8, 15) + newOr.substring(16, 23) + newOrmd5.substring(24, 31);
        return pass;
    }

    public static void log(String s) {
        Log.d("--nadhif", s);
    }
}
