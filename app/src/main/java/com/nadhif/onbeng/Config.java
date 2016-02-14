package com.nadhif.onbeng;

import com.google.android.gms.maps.model.LatLng;

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
}
