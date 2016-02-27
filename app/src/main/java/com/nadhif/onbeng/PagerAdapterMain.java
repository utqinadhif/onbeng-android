package com.nadhif.onbeng;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by nadhif on 05/12/2015.
 */
public class PagerAdapterMain extends FragmentPagerAdapter {
    Context _context;
    Gps gps;
    double latitude, longitude;
    SharedPreferences slatlng;

    public PagerAdapterMain(FragmentManager fm, Context context) {
        super(fm);

        slatlng = context.getSharedPreferences("LOCATION", Context.MODE_PRIVATE);
        String lt = slatlng.getString("latitude", null);
        String lg = slatlng.getString("longitude", null);
        if (lt != null && lg != null) {
            latitude = Double.parseDouble(lt);
            longitude = Double.parseDouble(lg);
        }else{
            latitude = longitude = 0;
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
