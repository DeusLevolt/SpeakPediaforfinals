package com.example.speakpediaforfinals;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private int[] ids = {R.id.shared_background_1, R.id.shared_background_2, R.id.shared_background_3,
            R.id.shared_background_4, R.id.shared_background_5, R.id.shared_background_6,
            R.id.shared_background_7, R.id.shared_background_8, R.id.shared_background_9,
            R.id.shared_background_10, R.id.button1, R.id.button2, R.id.button3, R.id.headingAbout};
    private Intent backgroundMusicIntent; // Declare the intent at the class level
    private BackgroundMusicService musicService;
    private boolean isBound = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Start the background music service
        loadSavedColor();
        saveSelectedColor();

        // Check if Terms and Conditions have been accepted
        boolean termsAccepted = checkTermsAndConditions();

        if (!termsAccepted) {
            // If Terms and Conditions have not been accepted, show the Terms and Conditions screen
            showTermsAndConditions();
        } else {
            // Terms and Conditions have been accepted, proceed with the normal flow
            setContentView(R.layout.activity_main);

            RelativeLayout mainLayout = findViewById(R.id.main_act_layout);

            TextView game = findViewById(R.id.heading3);
            TextView translator = findViewById(R.id.heading2);
            TextView Settings = findViewById(R.id.heading4);
            TextView About = findViewById(R.id.aboutUs);
            loadSavedColor();
            saveSelectedColor();

            About.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent about = new Intent(MainActivity.this, AboutUsActivity.class);
                    startActivity(about);
                }
            });

            translator.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent translator = new Intent(MainActivity.this, TranslatorActivity.class);
                    startActivity(translator);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });

            Settings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent settings = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(settings);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });

            game.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent game = new Intent(MainActivity.this, GameActivity.class);
                    startActivity(game);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });
        }
    }

    private boolean checkTermsAndConditions() {
        SharedPreferences preferences = getSharedPreferences("app_preferences", MODE_PRIVATE);
        boolean termsAccepted = preferences.getBoolean("terms_accepted", false);
        Log.d("TermsAndConditions", "Terms accepted: " + termsAccepted);
        return termsAccepted;
    }


    private void showTermsAndConditions() {
        // Launch the Terms and Conditions Activity
        Intent termsIntent = new Intent(MainActivity.this, TermsAndConditionsActivity.class);
        startActivity(termsIntent);
        // You might want to finish the current activity to prevent the user from going back to it without accepting terms
        finish();
    }

    public void applyColorToImageView() {
        for (int id : ids) {
            ImageView imageView = findViewById(id);
            if (imageView != null) {
                int selectedColor = ThemeColorManager.getInstance().getSelectedColor();
                if (selectedColor != -1) {
                    // Get the existing background drawable
                    Drawable backgroundDrawable = imageView.getBackground();

                    // Create a new shape drawable with the selected color
                    GradientDrawable shapeDrawable = new GradientDrawable();
                    shapeDrawable.setShape(GradientDrawable.RECTANGLE);
                    shapeDrawable.setCornerRadius(getResources().getDimensionPixelSize(R.dimen.corner_radius)); // Set your corner radius
                    shapeDrawable.setColor(selectedColor);

                    // Create a LayerDrawable with the existing background and the new shape
                    LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{backgroundDrawable, shapeDrawable});

                    // Set the LayerDrawable as the background of the ImageView
                    imageView.setBackground(layerDrawable);
                }
            }
        }
    }

    public void loadSavedColor() {
        SharedPreferences preferences = getSharedPreferences("theme_preferences", MODE_PRIVATE);
        int savedColor = preferences.getInt("selected_color", -1);
        ThemeColorManager.getInstance().setSelectedColor(savedColor);
        applyColorToImageView();// Apply the saved color on activity creation
    }

    public void saveSelectedColor() {
        int selectedColor = ThemeColorManager.getInstance().getSelectedColor();
        SharedPreferences preferences = getSharedPreferences("theme_preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("selected_color", selectedColor);
        editor.apply();
    }
    @Override
    protected void onDestroy() {
        // Stop the background music service when the activity is destroyed
        if (backgroundMusicIntent != null) {
            stopService(backgroundMusicIntent);
        }
        super.onDestroy();
    }
}
