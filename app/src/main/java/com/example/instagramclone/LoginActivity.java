package com.example.instagramclone;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {
    public static final String ACTIVITY_TAG = "LoginActivity";

    private EditText usernameEditText, passwordEditText;
    private Button signinButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.username_edittext_view);
        passwordEditText = findViewById(R.id.password_edittext_view);
        signinButton = findViewById(R.id.signin_button);

        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(ACTIVITY_TAG, "onClick: Login Button");
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                signinUser(username, password);
            }
        });
    }

    private void signinUser(String username, String password) {
        Log.i(ACTIVITY_TAG, "Attempting to sign in user: " + username);

        // navigate to main activity if user signin was successful
    }
}