package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.List;


/**
 * Created by Gboyega.Dada on 4/11/2017.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ItemViewHolder> {

    private Context context;
    private List<MovieItem> movies;
    private final OnItemClickListener clickListener;

    private static final String POSTER_PATH = "poster_path";
    private static final String TITLE = "title";
    private static final String OVERVIEW = "overview";

    public RecyclerViewAdapter(Context parent, OnItemClickListener listener) {

        context = parent;
        clickListener = listener;
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;

        public  ItemViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.movie_thumb);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, movies.get(getAdapterPosition()));
        }

    }

    public interface OnItemClickListener {
        void onClick(View view, MovieItem item);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View movieTileView = inflater.inflate(R.layout.movie_tile, parent, shouldAttachToParentImmediately);

        return new ItemViewHolder(movieTileView);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int pos) {
        MovieItem movie = movies.get(pos);

        // holder.title.setText(movie.getString(TITLE));
        // holder.overview.setText(movie.getString(OVERVIEW));
        Picasso.with(context)
                .load("http://image.tmdb.org/t/p/w500"+movie.poster_path)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        if (null == movies) return 0;
        return movies.size();
    }

    public void setMovieData(List<MovieItem> data) {
        movies = data;

        notifyDataSetChanged();
    }
}
