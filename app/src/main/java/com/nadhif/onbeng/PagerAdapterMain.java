package com.nadhif.onbeng;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by nadhif on 05/12/2015.
 */
public class PagerAdapterMain extends FragmentPagerAdapter {
    Context _context;
    GpsTracker gps;
    double latitude, longitude;

    public PagerAdapterMain(FragmentManager fm, Context context) {
        super(fm);

        gps = new GpsTracker(context);
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        } else {
            gps.showSettingsAlert();
        }
        _context = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment f = new Fragment();
        switch (position) {
            case 0:
                f = FragmentLogIn.newInstance(_context, latitude, longitude);
                break;
            case 1:
                f = FragmentSignUp.newInstance(_context, latitude, longitude);
                break;
        }
        return f;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
