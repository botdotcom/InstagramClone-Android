package com.example.instagramclone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {
    public static final String ACTIVITY_TAG = "LoginActivity";

    private EditText usernameEditText, passwordEditText;
    private Button signinButton, signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (ParseUser.getCurrentUser() != null) {
            gotoMainActivity();
        }

        usernameEditText = findViewById(R.id.username_edittext_view);
        passwordEditText = findViewById(R.id.password_edittext_view);
        signinButton = findViewById(R.id.signin_button);
        signupButton = findViewById(R.id.signup_button);

        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(ACTIVITY_TAG, "onClick: Sign in Button");
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                signinUser(username, password);
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(ACTIVITY_TAG, "onClick: Sign up Button");
                gotoSignupActivity();
            }
        });
    }

    private void signinUser(String username, String password) {
        Log.i(ACTIVITY_TAG, "Attempting to sign in user: " + username);

        ParseUser.logInInBackground(username, password, (user, e) -> {
            if (e != null) {
                Log.e(ACTIVITY_TAG, "Issue with login", e);
                return;
            }
            if (user == null) {
                Toast.makeText(LoginActivity.this, "Invalid username and/or password", Toast.LENGTH_SHORT).show();
                Log.e(ACTIVITY_TAG, "Missing username and/or password", e);
                return;
            }
            // navigate to main activity if user signin was successful
            Log.i(ACTIVITY_TAG, "Login successful");
            gotoMainActivity();
            Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
        });
    }

    private void gotoMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void gotoSignupActivity() {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }
}