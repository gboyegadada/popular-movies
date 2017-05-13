package com.example.android.popularmovies;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.SpannedString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.fragment.ReviewsFragment;

import org.w3c.dom.Text;

/**
 * Created by Gboyega.Dada on 5/8/2017.
 */

public class ReviewsRecyclerViewAdapter extends RecyclerView.Adapter<ReviewsRecyclerViewAdapter.ItemViewHolder> {

    private ReviewList mList;
    private final ReviewsFragment.OnItemClickListener mClickListener;


    public ReviewsRecyclerViewAdapter(ReviewsFragment.OnItemClickListener listener) {
        this.mClickListener = listener;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View itemView = inflater.inflate(R.layout.review_tile, parent, shouldAttachToParentImmediately);

        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int pos) {

        ReviewItem review = mList.get(pos);

        holder.author.setText(review.author);
        Spanned content = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                ? Html.fromHtml(review.content, Html.FROM_HTML_MODE_COMPACT)
                : Html.fromHtml(review.content);

        holder.content.setText(content);
    }

    @Override
    public int getItemCount() {
        if (null == mList) return 0;
        return mList.size();
    }

    public void setData(ReviewList data) {
        mList = data;

        notifyDataSetChanged();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView author;
        TextView content;

        public  ItemViewHolder(View itemView) {
            super(itemView);
            author = (TextView) itemView.findViewById(R.id.tv_review_author);
            content = (TextView) itemView.findViewById(R.id.tv_review_content);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mClickListener.onClick(view, mList.get(getAdapterPosition()));
        }

    }
}
