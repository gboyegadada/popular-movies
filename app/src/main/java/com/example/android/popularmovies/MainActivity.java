package com.example.android.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.most_popular:
                return showMostPopular();
            case R.id.top_rated:
                return showTopRated();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean showTopRated() {
        return true;
    }

    public boolean showMostPopular() {
        return true;
    }
}
