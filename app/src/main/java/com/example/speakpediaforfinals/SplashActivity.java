package com.example.speakpediaforfinals;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;

import com.example.speakpediaforfinals.R;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ProgressBar progressBar = findViewById(R.id.progressBar);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.loading_bar_anim);
        progressBar.startAnimation(animation);

        // You can also add a delay and then launch your main activity or another screen.
        // For example:
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start your main activity or other screen here
            }
        }, 5000); // Delay for 3 seconds (adjust as needed)
    }
}
