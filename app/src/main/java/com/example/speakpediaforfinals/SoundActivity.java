package com.example.speakpediaforfinals;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class SoundActivity extends Activity {

    private MediaPlayer mediaPlayer;


    private int[] ids = {R.id.soundbluebutton2, R.id.soundbluebutton,R.id.shared_background_1, R.id.shared_background_2, R.id.shared_background_3, R.id.shared_background_4, R.id.shared_background_5, R.id.shared_background_6, R.id.shared_background_7, R.id.shared_background_8, R.id.shared_background_9, R.id.shared_background_10,R.id.button1,R.id.button2,R.id.button3,R.id.headingAbout};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sounds);

        ImageView backbutton = findViewById(R.id.back_button_sound);
        TextView musicTextView = findViewById(R.id.music);
        loadSavedColor();
        saveSelectedColor();
        mediaPlayer = MediaPlayer.create(this, R.raw.splashscreenbg);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        Switch soundToggle = findViewById(R.id.sound_toggle);
        BackgroundMusicService backgroundMusicService = new BackgroundMusicService();

        soundToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is ON
                    // Start or resume music playback
                    startOrResumeMusic();
                } else {
                    // The toggle is OFF
                    // Stop music playback
                    stopMusic();
                }
            }
        });


        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backtosetting = new Intent(SoundActivity.this, SettingsActivity.class);
                startActivity(backtosetting);
            }
        });

        musicTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMusicMenu(view);
            }
        });

    }
    // Method to start or resume music playback
    private void startOrResumeMusic() {
        // Check if mediaPlayer is already playing
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            // If not playing, start or resume music
            mediaPlayer.start();
        } else {
            // If mediaPlayer is null or already playing, play music from a specific resource
            playMusic(R.raw.splashscreenbg); // replace with your actual music resource
        }
    }

    private void playMusic(int splashscreenbg) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        mediaPlayer = MediaPlayer.create(this, R.raw.splashscreenbg);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    // Method to stop the music playback
    private void stopMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause(); // Pause instead of stop to allow resuming later
        }
    }
    private void showMusicMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.music_menu, popupMenu.getMenu());

        // Set a listener for menu item clicks
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                // Handle menu item clicks here
                int itemId = menuItem.getItemId();

                if (itemId == R.id.music_option_1) {
                    // Handle option 1
                    changeBackgroundMusic(R.raw.music1);
                    return true;
                } else if (itemId == R.id.music_option_2) {
                    // Handle option 2
                    changeBackgroundMusic(R.raw.music2);
                    return true;
                } else if (itemId == R.id.music_option_3) {
                    // Handle option 3
                    changeBackgroundMusic(R.raw.music3);
                    return true;
                } else if (itemId == R.id.music_option_4) {
                    // Handle option 4
                    changeBackgroundMusic(R.raw.music4);
                    return true;
                } else if (itemId == R.id.music_option_5) {
                    // Handle option 5
                    changeBackgroundMusic(R.raw.splashscreenbg);
                    return true;
                } else {
                    return false;
                }

            }
        });

        // Show the popup menu
        popupMenu.show();
    }
    private void applyColorToImageView() {
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
    private void loadSavedColor () {
        SharedPreferences preferences = getSharedPreferences("theme_preferences", MODE_PRIVATE);
        int savedColor = preferences.getInt("selected_color", -1);
        ThemeColorManager.getInstance().setSelectedColor(savedColor);
        applyColorToImageView();// Apply the saved color on activity creation
    }
    private void saveSelectedColor () {
        int selectedColor = ThemeColorManager.getInstance().getSelectedColor();
        SharedPreferences preferences = getSharedPreferences("theme_preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("selected_color", selectedColor);
        editor.apply();
    }
    private void changeBackgroundMusic(int resourceId) {
        // Release the existing MediaPlayer
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        // Create and start a new MediaPlayer with the specified resource
        mediaPlayer = MediaPlayer.create(this, resourceId);

        // Check if MediaPlayer creation was successful
        if (mediaPlayer != null) {
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }
    }


}
