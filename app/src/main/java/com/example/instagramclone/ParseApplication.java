package com.example.instagramclone;

import com.parse.Parse;
import com.parse.ParseObject;

import android.app.Application;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // register parse models
        ParseObject.registerSubclass(Post.class);
        // initialize Parse backend
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("HaDxsMD1wf99qDQOYJ4SuwsUdlCt1ousWMGESScw")
                .clientKey("Ny8D0ppZzEQv3dldmNsCrIInh4kfxTlKwPM4bmpR")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
