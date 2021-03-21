package com.example.instagramclone.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.instagramclone.MainActivity;
import com.example.instagramclone.R;
import com.example.instagramclone.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ComposeFragment extends Fragment {
    public static final String FRAGMENT_TAG = "ComposeFragment";

    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    private File photoFile;
    private String photoFileName = "photo.jpg";

    private Button captureImageButton;
    private Button submitPostButton;
    private EditText captionEditText;
    private ImageView uploadedPictureImageView;
    private ProgressBar postUploadingBar;

    public ComposeFragment() {
        // Required empty public constructor
    }

    // called when fragment should create its view object hierarchy
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose, container, false);
    }

    // event is triggered soon after onCreateView()
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        captureImageButton = view.findViewById(R.id.take_picture_button);
        uploadedPictureImageView = view.findViewById(R.id.uploaded_picture_image_view);
        captionEditText = view.findViewById(R.id.caption_edittext_view);
        submitPostButton = view.findViewById(R.id.submit_post_button);
        postUploadingBar = view.findViewById(R.id.posting_loading_bar);

        captureImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });

        submitPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String caption = captionEditText.getText().toString();

                if (photoFile == null || uploadedPictureImageView.getDrawable() == null) {
                    Toast.makeText(getContext(), "Image cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (caption.isEmpty()) {
                    Toast.makeText(getContext(), "Caption cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                ParseUser currentUser = ParseUser.getCurrentUser();

                saveInstagramPost(photoFile, caption, currentUser);
            }
        });
    }

    private void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = getPhotoFileUri(photoFileName);

        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", photoFile);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    private File getPhotoFileUri(String photoFileName) {
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), FRAGMENT_TAG);

        // create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(FRAGMENT_TAG, "Failed to create directory");
        }

        // return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + photoFileName);

        return file;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // load the taken image into a preview
                uploadedPictureImageView.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't captured", Toast.LENGTH_SHORT).show();
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
                    Log.e(FRAGMENT_TAG, "Error while saving post", e);
                    Toast.makeText(getContext(), "Error saving the post", Toast.LENGTH_SHORT).show();
                }

                postUploadingBar.setVisibility(ProgressBar.VISIBLE);
//                postUploadingBar.setIndeterminate(true);
                Log.i(FRAGMENT_TAG, "Posted successfully");
                captionEditText.setText("");
                uploadedPictureImageView.setImageResource(0);
                postUploadingBar.setVisibility(ProgressBar.INVISIBLE);
//                postUploadingBar.setIndeterminate(false);
            }
        });
    }
}