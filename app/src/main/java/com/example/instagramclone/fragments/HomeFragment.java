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
import android.widget.Toast;

import com.example.instagramclone.HomeFeedAdapter;
import com.example.instagramclone.R;
import com.example.instagramclone.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements HomeFeedAdapter.CustomItemClickListener{
    public static final String FRAGMENT_TAG = "HomeFragment";

    private RecyclerView homeFeedRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    protected HomeFeedAdapter homeFeedAdapter;
    protected List<Post> allPosts;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        homeFeedRecyclerView = view.findViewById(R.id.home_feed_recycler_view);
        swipeRefreshLayout = view.findViewById(R.id.home_swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light);

        // create data source
        allPosts = new ArrayList<>();
        homeFeedAdapter = new HomeFeedAdapter(getContext(), allPosts, this);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(FRAGMENT_TAG, "onSwipeRefresh in HomeFragment: success");
                homeFeedAdapter.clear();
                queryPosts();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        // set adapter on the recycler view
        homeFeedRecyclerView.setAdapter(homeFeedAdapter);
        // set layout manager on the recycler view
        homeFeedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        queryPosts();
    }

    protected void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
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

                allPosts.addAll(posts);
                homeFeedAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Log.i(FRAGMENT_TAG, position + " was clicked");
        homeFeedAdapter.notifyDataSetChanged();
    }
}