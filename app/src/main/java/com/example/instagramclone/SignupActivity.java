package com.example.instagramclone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignupActivity extends AppCompatActivity {
    public static final String ACTIVITY_TAG = "SignupActivity";

    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        usernameEditText = findViewById(R.id.new_username_edittext_view);
        passwordEditText = findViewById(R.id.new_password_edittext_view);
        confirmPasswordEditText = findViewById(R.id.confirm_password_edittext_view);
        signupButton = findViewById(R.id.signup_instagram_button);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(ACTIVITY_TAG, "onClick: Sign up Button");
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();

                if (!password.equals(confirmPassword)) {
                    Log.d(ACTIVITY_TAG, "onClick: Passwords do not match");
                    Toast.makeText(SignupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }
                signupUser(username, password);
            }
        });
    }

    private void signupUser(String username, String password) {
        Log.i(ACTIVITY_TAG, "Attempting to sign up user: " + username);

        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.signUpInBackground(e -> {
            if (e != null) {
                Log.e(ACTIVITY_TAG, "Issue with signup", e);
                return;
            }
            Log.i(ACTIVITY_TAG, "Signup successful");
            gotoMainActivity();
            Toast.makeText(SignupActivity.this, "Signup successful", Toast.LENGTH_SHORT).show();
        });
    }

    private void gotoMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}