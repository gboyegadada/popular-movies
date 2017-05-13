package com.example.android.popularmovies;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.android.popularmovies.fragment.OverviewFragment;
import com.example.android.popularmovies.fragment.ReviewsFragment;
import com.example.android.popularmovies.fragment.VideosFragment;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class DetailsActivity extends AppCompatActivity {
    MovieItem mMovieData;
    private DetailsPagerAdapter mPagerAdapter;
    private ViewPager mPager;
    private TabLayout mTabs;

    private static final String TAG = DetailsActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mMovieData = Parcels.unwrap(getIntent().getParcelableExtra("movie_data"));

        initialisePaging();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.details_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
            overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }



    private void initialisePaging() {
        List<Fragment> fragments = new Vector<Fragment>();
        List<String> labels = new ArrayList<>();

        // Tab Labels
        labels.add(getString(R.string.tab_label_overview));
        labels.add(getString(R.string.tab_label_reviews));
        labels.add(getString(R.string.tab_label_videos));

        // Fragments
        Bundle args = new Bundle();
        args.putParcelable("movie_data", Parcels.wrap(mMovieData));

        Fragment overviewFragment = Fragment.instantiate(this, OverviewFragment.class.getName());
        overviewFragment.setArguments(args);
        fragments.add(overviewFragment);


        Fragment reviewsFragment = Fragment.instantiate(this, ReviewsFragment.class.getName());
        reviewsFragment.setArguments(args);
        fragments.add(reviewsFragment);

        Fragment videosFragment = Fragment.instantiate(this, VideosFragment.class.getName());
        videosFragment.setArguments(args);
        fragments.add(videosFragment);

        this.mPagerAdapter = new DetailsPagerAdapter(super.getSupportFragmentManager(), fragments, labels);
        mPager = (ViewPager) super.findViewById(R.id.vp_movie_details);
        mPager.setAdapter(this.mPagerAdapter);
        mPager.setCurrentItem(0);

        mTabs = (TabLayout) findViewById(R.id.tl_movie_details);
        mTabs.setupWithViewPager(mPager);
    }
}
