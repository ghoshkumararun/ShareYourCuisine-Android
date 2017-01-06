package com.toe.shareyourcuisine.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.squareup.picasso.Picasso;
import com.toe.shareyourcuisine.R;
import com.toe.shareyourcuisine.model.Post;
import com.toe.shareyourcuisine.model.PostItem;
import com.toe.shareyourcuisine.model.Recipe;
import com.toe.shareyourcuisine.utils.SYCUtils;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by HQu on 12/27/2016.
 */

public class PostRecyclerViewAdapter extends RecyclerView.Adapter<PostRecyclerViewAdapter.PostViewHolder> {

    private Context mContext;
    private List<PostItem> mPostItems;
    private static PostItemClickListener mPostItemClickListener;

    public PostRecyclerViewAdapter(Context context, List<PostItem> postItems) {
        mContext = context;
        mPostItems = postItems;
    }

    public interface PostItemClickListener {
        public void onItemClick(int position, View v);
    }

    public void setPostItemClickListener(PostItemClickListener postItemClickListener) {
        mPostItemClickListener = postItemClickListener;
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public CircleImageView mAvatarIV;
        public TextView mNameTV;
        public TextView mContentTV;
        public ImageView mImgIV;
        public TextView mCreatedAtTV;
        public TextView mTotalLikesNumTV;
        public TextView mTotalCommentsNumTV;
        public PostViewHolder(View itemView) {
            super(itemView);
            mAvatarIV = (CircleImageView)itemView.findViewById(R.id.avatar_iv);
            mNameTV = (TextView)itemView.findViewById(R.id.name_tv);
            mContentTV = (TextView)itemView.findViewById(R.id.content_tv);
            mImgIV = (ImageView)itemView.findViewById(R.id.img_iv);
            mCreatedAtTV = (TextView)itemView.findViewById(R.id.createdAt_tv);
            mTotalLikesNumTV = (TextView)itemView.findViewById(R.id.total_likes_num_tv);
            mTotalCommentsNumTV = (TextView)itemView.findViewById(R.id.total_comments_num_tv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mPostItemClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        PostViewHolder postViewHolder = new PostViewHolder(view);
        return postViewHolder;
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        String postImgUrl = mPostItems.get(position).getImgUrl();
        if(postImgUrl != null && postImgUrl != "") {
            holder.mImgIV.getLayoutParams().width = (int) mContext.getResources().getDimension(R.dimen.img_dimen);
            holder.mImgIV.getLayoutParams().height = (int) mContext.getResources().getDimension(R.dimen.img_dimen);
            Picasso.with(mContext).load(postImgUrl).fit().centerCrop().into(holder.mImgIV);
        }
        holder.mNameTV.setText(mPostItems.get(position).getCreatedUserName());
        holder.mContentTV.setText(mPostItems.get(position).getContent());
        Picasso.with(mContext).load(mPostItems.get(position).getCreatedUserAvatarUrl()).fit().centerCrop().into(holder.mAvatarIV);
        holder.mCreatedAtTV.setText(SYCUtils.convertMillisecondsToDateTime(mPostItems.get(position).getCreatedAt()));
        holder.mTotalLikesNumTV.setText(mPostItems.get(position).getTotalLikes() + " likes");
    }

    @Override
    public int getItemCount() {
        return mPostItems.size();
    }


}