package com.example.android.popularmovies;

import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

public class DetailsActivity extends AppCompatActivity {
    MovieItem movie_data;

    ImageView poster;
    TextView main_title;
    TextView release_year;
    TextView running_time;
    TextView rating;
    TextView overview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        movie_data = Parcels.unwrap(getIntent().getParcelableExtra("movie_data"));

        poster = (ImageView) findViewById(R.id.iv_poster);
        main_title = (TextView) findViewById(R.id.tv_details_header);
        release_year = (TextView) findViewById(R.id.tv_release_year);
        running_time = (TextView) findViewById(R.id.tv_running_time);
        rating = (TextView) findViewById(R.id.tv_rating);
        overview = (TextView) findViewById(R.id.tv_overview);

        Picasso.with(this)
                .load("http://image.tmdb.org/t/p/w500"+movie_data.poster_path)
                .into(poster);
        main_title.setText(movie_data.title);

        String [] dateParts = movie_data.release_date.split("-");

        release_year.setText(dateParts[0]);
        running_time.setText(getString(R.string.details_date_label)+" "+movie_data.release_date); // movie_data.running_time;
        rating.setText(getString(R.string.details_rating_label)+" "+movie_data.vote_average + "/10");
        overview.setText(movie_data.overview);

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
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }
}
