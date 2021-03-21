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

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {
    private Context context;
    private List<Post> posts;

    public PostsAdapter(Context context, List<Post> posts) {
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            usernameTextView = itemView.findViewById(R.id.username_text_view);
            postImageView = itemView.findViewById(R.id.post_image_view);
            captionTextView = itemView.findViewById(R.id.caption_text_view);
        }

        public void bind(Post post) {
            usernameTextView.setText(post.getUser().getUsername());
            captionTextView.setText(post.getCaption());

            ParseFile image = post.getImage();

            if (image != null) {
                Glide.with(context).load(post.getImage().getUrl()).into(postImageView);
            }
        }
    }
}
