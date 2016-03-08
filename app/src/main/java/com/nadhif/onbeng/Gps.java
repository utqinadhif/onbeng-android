package com.nadhif.onbeng;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by nadhif on 10/12/2015.
 */
public class Gps implements LocationListener {
    private final Context context;

    // Flag for GPS status
    boolean isGPSEnabled = false;
    // Flag for network status
    boolean isNetworkEnabled = false;

    boolean canGetLocation = false; // Can or can not
    Location location; // Location
    double latitude; // Latitude
    double longitude; // Longitude

    // The minimum distance to change Updates in meters
    protected static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 0 meters
    // The minimum time between updates in milliseconds
    protected static final long MIN_TIME_BW_UPDATES = 1000; // 1 second

    // Declaring a Location Manager
    protected LocationManager locationManager;

    // Constructor
    public Gps(Context context) {
        this.context = context;
        getLocation();
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            // getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (isGPSEnabled || isNetworkEnabled) {
                this.canGetLocation = true;
                if (isGPSEnabled) {
                    this.useGPS();
                }
                if (isNetworkEnabled) {
                    this.useNetwork();
                }
            } else {
                toast("Can not use location service");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    public void useGPS() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            toast("No permission granted");
        } else {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
            }
        }
    }

    public void useNetwork() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            toast("No permission granted");
        } else {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
            }
        }
    }

    public void stopUsingGPS() {
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                toast("Error");
            }
            locationManager.removeUpdates(Gps.this);
        } else {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            this.stopUsingGPS();
        }
    }

    public double getLat() {
        if (location != null) {
            latitude = location.getLatitude();
        }
        return latitude;
    }

    public double getLng() {
        if (location != null) {
            longitude = location.getLongitude();
        }
        return longitude;
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Information");
        alertDialog.setMessage("Some features are unavailable because Location Services are turned off. To turn on Location Services, open Setting.");
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Activity activity = (Activity) context;
                activity.finish();
            }
        });
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener(){

            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                Activity activity = (Activity) context;
                activity.finish();
            }
        });
        alertDialog.show();
    }

    public void toast(String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        toast("Location changed " + location.getLatitude() + ", " + location.getLongitude());
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        toast("Status changed " + provider + " " + status);
    }

    @Override
    public void onProviderDisabled(String provider) {
        toast(provider + " dissabled");
    }

    @Override
    public void onProviderEnabled(String provider) {
        toast(provider + " enabled");
    }
}
