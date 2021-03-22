package com.example.instagramclone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagramclone.models.Post;
import com.parse.ParseFile;
import com.parse.ParseQuery;

import java.util.List;

public class HomeFeedAdapter extends RecyclerView.Adapter<HomeFeedAdapter.ViewHolder> {
    private Context context;
    private List<Post> posts;

    public HomeFeedAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
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

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView usernameTextView;
        private ImageView postImageView;
        private TextView captionTextView;
        private TextView captionUsernameTextView;
//        private ImageView userImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            usernameTextView = itemView.findViewById(R.id.username_text_view);
            postImageView = itemView.findViewById(R.id.post_image_view);
            captionTextView = itemView.findViewById(R.id.caption_text_view);
            captionUsernameTextView = itemView.findViewById(R.id.caption_username_text_view);
//            userImageView = itemView.findViewById(R.id.user_profile_image_view);
        }

        public void bind(Post post) {
            usernameTextView.setText(post.getUser().getUsername());

            if (!post.getCaption().isEmpty()) {
                captionTextView.setText(post.getCaption());
                captionUsernameTextView.setText(post.getUser().getUsername());
            }

            ParseFile image = post.getImage();
//            ParseFile userImage =

            if (image != null) {
                Glide.with(context).load(post.getImage().getUrl()).into(postImageView);
            }
        }
    }
}
