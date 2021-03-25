package com.example.instagramclone.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.instagramclone.HomeFeedAdapter;
import com.example.instagramclone.R;
import com.example.instagramclone.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment implements HomeFeedAdapter.CustomItemClickListener{
    public static final String FRAGMENT_TAG = "ProfileFragment";

    private RecyclerView profileFeedRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    protected HomeFeedAdapter feedAdapter;
    protected List<Post> userPosts;

    private TextView profileUsernameTextView;
    private TextView profileIntroTextView;
    private Button editProfileButton;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profileUsernameTextView = view.findViewById(R.id.profile_username_text_view);
        profileIntroTextView = view.findViewById(R.id.profile_intro_text_view);
        editProfileButton = view.findViewById(R.id.edit_profile_button);

        profileFeedRecyclerView = view.findViewById(R.id.profile_feed_recycler_view);
        swipeRefreshLayout = view.findViewById(R.id.profile_swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light);

        ParseUser currentUser = ParseUser.getCurrentUser();
        profileUsernameTextView.setText(currentUser.getUsername());

        if (currentUser.getString("intro") != null) {
            profileIntroTextView.setText(currentUser.getString("intro"));
        }

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(FRAGMENT_TAG, "Editing profile");
            }
        });

        // create data source
        userPosts = new ArrayList<>();
        feedAdapter = new HomeFeedAdapter(getContext(), userPosts, this);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(FRAGMENT_TAG, "onSwipeRefresh in ProfileFragment: success");
                feedAdapter.clear();
                queryPosts();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        // set adapter on the recycler view
        profileFeedRecyclerView.setAdapter(feedAdapter);
        // set layout manager on the recycler view
        profileFeedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        queryPosts();
    }

    protected void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        query.setLimit(20);
        query.addDescendingOrder(Post.KEY_CREATED_AT);

        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(FRAGMENT_TAG, "Issue retrieving posts", e);
                    return;
                }

                for (Post post : posts) {
                    Log.i(FRAGMENT_TAG, "Post: " + post.getCaption() + ", Username: " + post.getUser().getUsername() + ", Likes: " + post.getLikeCount());
                }

                userPosts.addAll(posts);
                feedAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Log.i(FRAGMENT_TAG, position + " was clicked");
        feedAdapter.notifyDataSetChanged();
    }
}