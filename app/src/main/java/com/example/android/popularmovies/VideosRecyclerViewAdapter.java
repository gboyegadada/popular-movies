package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.fragment.VideosFragment;
import com.squareup.picasso.Picasso;

/**
 * Created by Gboyega.Dada on 5/10/2017.
 */

public class VideosRecyclerViewAdapter extends RecyclerView.Adapter<VideosRecyclerViewAdapter.ItemViewHolder> {

private VideoList mVideos;
private final VideosFragment.OnItemClickListener mClickListener;

public VideosRecyclerViewAdapter(VideosFragment.OnItemClickListener listener) {

        mClickListener = listener;
        }


public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    ImageView imageView;
    TextView name;


    public  ItemViewHolder(View itemView) {
        super(itemView);
        imageView = (ImageView) itemView.findViewById(R.id.movie_thumb);
        name = (TextView) itemView.findViewById(R.id.tv_video_name);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        mClickListener.onClick(view, mVideos.get(getAdapterPosition()));
    }

}

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View videoTileView = inflater.inflate(R.layout.video_tile, parent, shouldAttachToParentImmediately);

        return new ItemViewHolder(videoTileView);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int pos) {
        VideoItem video = mVideos.get(pos);

        holder.name.setText(video.name);
    }

    @Override
    public int getItemCount() {
        if (null == mVideos) return 0;
        return mVideos.size();
    }

    public void setData(VideoList data) {
        mVideos = data;

        notifyDataSetChanged();
    }
}
