package com.reryde.provider.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.reryde.provider.Fragment.EarningsFragment;
import com.reryde.provider.Fragment.OnGoingTrips;
import com.reryde.provider.Fragment.PastTrips;
import com.reryde.provider.Fragment.SummaryFragment;
import com.reryde.provider.R;

public class SummaryActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ImageView backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getActionBar().setDisplayHomeAsUpEnabled(true);
//            getActionBar().setHomeButtonEnabled(true);
        }catch (Exception e){
            e.printStackTrace();
        }
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        backArrow = (ImageView) findViewById(R.id.backArrow);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(new SampleFragmentPagerAdapter(getSupportFragmentManager(),
                this));
        setupTabIcons();
    }

    /**
     * Adding custom view to tab
     */
    private void setupTabIcons() {

        TextView tabOne = (TextView) LayoutInflater.from(SummaryActivity.this).inflate(R.layout.custom_tab, null);
        tabOne.setText(getResources().getString(R.string.summary));
        tabLayout.getTabAt(0).setCustomView(tabOne);


        TextView tabTwo = (TextView) LayoutInflater.from(SummaryActivity.this).inflate(R.layout.custom_tab, null);
        tabTwo.setText(getResources().getString(R.string.today_earnings));
        tabLayout.getTabAt(1).setCustomView(tabTwo);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
        final int PAGE_COUNT = 2;
        private String tabTitles[] = new String[]{getResources().getString(R.string.summary),getResources().getString(R.string.today_earnings)};
        private Context context;

        public SampleFragmentPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new SummaryFragment();
                case 1:
                    return new EarningsFragment();
                default:
                    return new SummaryFragment();
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            return tabTitles[position];
        }
    }

}
