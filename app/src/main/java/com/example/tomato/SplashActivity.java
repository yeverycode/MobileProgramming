package com.example.tomato;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Hide the action bar for the splash screen
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Start the main activity after the delay
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            // Finish this activity so the user can't go back to it
            finish();
        }, SPLASH_DELAY);
    }
}
