package com.example.instagramclone;

import android.content.Context;
import android.graphics.Color;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagramclone.models.Post;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFeedAdapter extends RecyclerView.Adapter<HomeFeedAdapter.ViewHolder> {
    private Context context;
    private List<Post> posts;

    final private CustomItemClickListener itemClickListener;
    private static final String ADAPTER_TAG = "HomeFeedAdapter";

    public interface CustomItemClickListener {
        void onItemClick(int position);
    }

    public HomeFeedAdapter(Context context, List<Post> posts, CustomItemClickListener itemClickListener) {
        this.context = context;
        this.posts = posts;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Post> postsList) {
        posts.addAll(postsList);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView usernameTextView;
        private final ImageView postImageView;
        private final TextView captionTextView;
        private final TextView captionUsernameTextView;
        private final TextView likesCountTextView;
        private final ImageView likePostImageView;
        private final TextView createdAgoTextView;
//        private ImageView userImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            usernameTextView = itemView.findViewById(R.id.username_text_view);
            postImageView = itemView.findViewById(R.id.post_image_view);
            captionTextView = itemView.findViewById(R.id.caption_text_view);
            captionUsernameTextView = itemView.findViewById(R.id.caption_username_text_view);
            likesCountTextView = itemView.findViewById(R.id.likes_count_text_view);
            likePostImageView = itemView.findViewById(R.id.like_post_icon_button);
            createdAgoTextView = itemView.findViewById(R.id.created_ago_text_view);
//            userImageView = itemView.findViewById(R.id.user_profile_image_view);

            likePostImageView.setOnClickListener(this);
        }

        public void bind(Post post) {
            usernameTextView.setText(post.getUser().getUsername());

            if (post.getCreatedAt() != null) {
                createdAgoTextView.setText(getRelativeTimeAgo(post.getCreatedAt()));
            }

            if (post.getLikeCount() != 0) {
                String likes = String.valueOf(post.getLikeCount());
                if (post.getLikeCount() == 1)
                    likesCountTextView.setText(likes + " like");
                else
                    likesCountTextView.setText(likes + " likes");
            }
            else {
                likesCountTextView.setText("");
            }

            if (!post.getCaption().isEmpty()) {
                captionTextView.setText(post.getCaption());
                captionUsernameTextView.setText(post.getUser().getUsername());
            }

            ParseFile image = post.getImage();

            if (image != null) {
                Glide.with(context).load(post.getImage().getUrl()).into(postImageView);
            }
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();

            if (v.getId() == likePostImageView.getId()) {
                if (likePostImageView.isSelected()) {
                    decreaseLikes(position);
                    likePostImageView.setSelected(false);
                    likePostImageView.setImageResource(R.drawable.ic_instagram_heart_stroke);
                    likePostImageView.setColorFilter(context.getResources().getColor(R.color.white));
                }
                else {
                    increaseLikes(position);
                    likePostImageView.setSelected(true);
                    likePostImageView.setImageResource(R.drawable.ic_instagram_heart_filled);
                    likePostImageView.setColorFilter(context.getResources().getColor(R.color.darkRed));
                }
            }

            itemClickListener.onItemClick(position);
        }
    }

    private String getRelativeTimeAgo(Date rawDate) {
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd MM yyyy 'at' HH:mm:ss z", Locale.ENGLISH);
        dateTimeFormat.setLenient(true);

        String relativeTime = "";

        try {
            long timeInMillis = dateTimeFormat.parse(String.valueOf(rawDate)).getTime();
            relativeTime = DateUtils.getRelativeTimeSpanString(timeInMillis, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        return relativeTime;
    }

    private void increaseLikes(int position) {
        Post post = posts.get(position);
        int likeCount = post.getLikeCount();
        post.setLikeCount(likeCount + 1);

        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(ADAPTER_TAG, "Error liking the post", e);
                }
            }
        });
    }

    private void decreaseLikes(int position) {
        Post post = posts.get(position);
        int likeCount = post.getLikeCount();
        post.setLikeCount(likeCount - 1);

        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(ADAPTER_TAG, "Error un-liking the post", e);
                }
            }
        });
    }
}
