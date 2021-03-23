package com.example.instagramclone.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

@ParseClassName("Post")
public class Post extends ParseObject {
    public static final String KEY_CAPTION = "caption";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "user";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_OBJECT_ID = "objectId";
    public static final String KEY_LIKES = "likesCount";

    public String getCaption() {
        return getString(KEY_CAPTION);
    }

    public void setCaption(String caption) {
        put(KEY_CAPTION, caption);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile image) {
        put(KEY_IMAGE, image);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public String getObjectId() {
        return  getString(KEY_OBJECT_ID);
    }

    public int getLikeCount() {
        return getInt(KEY_LIKES);
    }

    public void setLikeCount(int likes) {
        put(KEY_LIKES, likes);
    }

    public Date getCreatedAt() {
        return getDate(KEY_CREATED_AT);
    }
}
