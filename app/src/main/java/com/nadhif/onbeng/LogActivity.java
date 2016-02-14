package com.nadhif.onbeng;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class LogActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private PagerAdapterMain PagerAdapterMain;
    private String[] tabs = {"Log In Now", "Sign Up Now"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Log In or Sign Up");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_navigate_before);

        PagerAdapterMain = new PagerAdapterMain(getSupportFragmentManager(), this);
        PagerAdapterMain.getItem(0);
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(PagerAdapterMain);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        setupTabLayout(tabLayout);
        mViewPager.setCurrentItem(1);
        mViewPager.setCurrentItem(0);

    }

    public void setupTabLayout(TabLayout upTabLayout) {
        upTabLayout.setTabMode(TabLayout.MODE_FIXED);
        upTabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        upTabLayout.setupWithViewPager(mViewPager);
        for (int i = 0; i < tabs.length; i++) {
            TextView tab = (TextView) LayoutInflater.from(this).inflate(R.layout.main_tab, null);
            tab.setText(tabs[i]);
            tab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_lock_outline, 0, 0);
            upTabLayout.getTabAt(i).setCustomView(tab);
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
}