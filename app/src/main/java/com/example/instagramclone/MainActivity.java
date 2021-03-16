package com.example.instagramclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.instagramclone.models.Post;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String ACTIVITY_TAG = "MainActivity";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    private File photoFile;
    private String photoFileName = "photo.jpg";

    private Button captureImageButton;
    private Button submitPostButton;
    private EditText captionEditText;
    private ImageView uploadedPictureImageView;
    private ProgressBar postUploadingBar;

    private Button signoutButton;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        captureImageButton = findViewById(R.id.take_picture_button);
        uploadedPictureImageView = findViewById(R.id.uploaded_picture_image_view);
        captionEditText = findViewById(R.id.caption_edittext_view);
        submitPostButton = findViewById(R.id.submit_post_button);
        signoutButton = findViewById(R.id.signout_appbar_button);
        postUploadingBar = findViewById(R.id.posting_loading_bar);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
//        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.action_home_feed:
//                        item.setIcon(R.drawable.ic_instagram_home_filled);
//                        return true;
//                    case R.id.action_search:
//                        return true;
//                    case R.id.action_create:
//                        item.setIcon(R.drawable.ic_instagram_new_post_filled);
//                        return true;
//                    case R.id.action_favorites:
//                        item.setIcon(R.drawable.ic_instagram_heart_filled);
//                        return true;
//                    case R.id.action_profile:
//                        item.setIcon(R.drawable.ic_instagram_user_filled);
//                        return true;
//                    default: return true;
//                }
//            }
//        });
//        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
//            @Override
//            public void onNavigationItemReselected(@NonNull MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.action_home_feed:
//                        item.setIcon(R.drawable.ic_instagram_home_stroke);
//                        break;
//                    case R.id.action_search:
//                        break;
//                    case R.id.action_create:
//                        item.setIcon(R.drawable.ic_instagram_new_post_stroke);
//                        break;
//                    case R.id.action_favorites:
//                        item.setIcon(R.drawable.ic_instagram_heart_stroke);
//                        break;
//                    case R.id.action_profile:
//                        item.setIcon(R.drawable.ic_instagram_user_stroke);
//                        break;
//                    default: break;
//                }
//            }
//        });

        captureImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });

//        queryPosts();

        submitPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String caption = captionEditText.getText().toString();

                if (photoFile == null || uploadedPictureImageView.getDrawable() == null) {
                    Toast.makeText(MainActivity.this, "Image cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (caption.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Caption cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                ParseUser currentUser = ParseUser.getCurrentUser();

                saveInstagramPost(photoFile, caption, currentUser);
            }
        });

        signoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                Log.i(ACTIVITY_TAG, "Logged out");

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    private void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = getPhotoFileUri(photoFileName);

        Uri fileProvider = FileProvider.getUriForFile(MainActivity.this, "com.codepath.fileprovider", photoFile);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
        if (intent.resolveActivity(getPackageManager()) != null) {
            // start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    private File getPhotoFileUri(String photoFileName) {
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), ACTIVITY_TAG);

        // create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(ACTIVITY_TAG, "Failed to create directory");
        }

        // return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + photoFileName);

        return file;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // load the taken image into a preview
                uploadedPictureImageView.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't captured", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveInstagramPost(File photoFile, String caption, ParseUser currentUser) {
        Post post = new Post();
        post.setCaption(caption);
        post.setUser(currentUser);
        post.setImage(new ParseFile(photoFile));

        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(ACTIVITY_TAG, "Error while saving post", e);
                    Toast.makeText(MainActivity.this, "Error saving the post", Toast.LENGTH_SHORT).show();
                }

                postUploadingBar.setVisibility(ProgressBar.VISIBLE);
//                postUploadingBar.setIndeterminate(true);
                Log.i(ACTIVITY_TAG, "Posted successfully");
                captionEditText.setText("");
                uploadedPictureImageView.setImageResource(0);
                postUploadingBar.setVisibility(ProgressBar.INVISIBLE);
//                postUploadingBar.setIndeterminate(false);
            }
        });
    }

    private void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(ACTIVITY_TAG, "Issue retrieving posts", e);
                    return;
                }

                for (Post post : posts) {
                    Log.i(ACTIVITY_TAG, "Post: " + post.getCaption() + ", Username: " + post.getUser().getUsername());
                }
            }
        });
    }
}