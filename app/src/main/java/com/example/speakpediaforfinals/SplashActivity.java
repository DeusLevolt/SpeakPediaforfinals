package com.example.speakpediaforfinals;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {


    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ProgressBar progressBar = findViewById(R.id.progressBar);


        // Initialize MediaPlayer
        mediaPlayer = MediaPlayer.create(this, R.raw.splashscreenbg); // Replace with your audio file
        mediaPlayer.setLooping(true); // Set looping to true for background music
        // Start the MediaPlayer
        mediaPlayer.start();


        // Delay for 5 seconds before showing the terms
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Check if the terms have been shown before
                SharedPreferences preferences = getSharedPreferences("app_preferences", MODE_PRIVATE);
                boolean termsShownBefore = preferences.getBoolean("terms_shown_before", false);

                if (!termsShownBefore) {
                    // If terms have not been shown before, show the TermsAndConditionsActivity
                    showTermsAndConditions();
                } else {
                    // If terms have been shown before, proceed with your normal flow
                    // hide the progress bar
                    progressBar.setVisibility(View.GONE);

                    // Start your main activity or other screen here
                    Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(mainIntent);

                    // Stop the MediaPlayer
                    stopMediaPlayer();

                    // Finish the current activity
                    finish();
                }
            }
        }, 7000); // Delay for 5 seconds (adjust as needed)
    }

    private void stopMediaPlayer() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void showTermsAndConditions() {
        // Show the TermsAndConditionsActivity
        Intent termsIntent = new Intent(SplashActivity.this, TermsAndConditionsActivity.class);
        startActivity(termsIntent);

        // Finish the current activity to prevent going back
        finish();
    }
}
