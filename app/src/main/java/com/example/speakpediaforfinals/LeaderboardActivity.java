// LeaderboardActivity.java
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {

    private int[] ids = {R.id.leaderboardblue,R.id.shared_background_1, R.id.shared_background_2, R.id.shared_background_3, R.id.shared_background_4, R.id.shared_background_5, R.id.shared_background_6, R.id.shared_background_7, R.id.shared_background_8, R.id.shared_background_9, R.id.shared_background_10,R.id.button1,R.id.button2,R.id.button3,R.id.translatorbluebg};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        ImageView back = findViewById(R.id.back_button_leaderboard);

        // Fetch and display leaderboard data
        displayLeaderboard();
        saveSelectedColor();
        loadSavedColor();


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backtogame = new Intent(LeaderboardActivity.this, MainGameActivity.class );
                startActivity(backtogame);
            }
        });
    }

    private void displayLeaderboard() {
        // Fetch the actual leaderboard data from SharedPreferences
        List<LeaderboardEntry> leaderboardEntries = loadLeaderboard();

        // Find the TextView in your layout
        TextView leaderboardTextView = findViewById(R.id.leaderboardlist);

        // Build the leaderboard content as a string
        StringBuilder leaderboardContent = new StringBuilder();
        int rank = 1;
        for (LeaderboardEntry entry : leaderboardEntries) {
            leaderboardContent.append(rank).append(". ")
                    .append(entry.getUsername()).append(" - Score: ")
                    .append(entry.getScore()).append("\n");
            rank++;
        }

        // Set the leaderboard content to the TextView
        leaderboardTextView.setText(leaderboardContent.toString());
    }

    private List<LeaderboardEntry> loadLeaderboard() {
        // Load the leaderboard entries from SharedPreferences
        SharedPreferences preferences = getSharedPreferences("leaderboard_preferences", MODE_PRIVATE);
        String entriesJson = preferences.getString("leaderboard_entries", "[]");

        // Print the entriesJson to log for debugging
        Log.d("LeaderboardDebug", "Entries from SharedPreferences: " + entriesJson);

        // Convert JSON string to a list of LeaderboardEntry objects
        Type listType = new TypeToken<List<LeaderboardEntry>>() {}.getType();
        return new Gson().fromJson(entriesJson, listType);
    }
    private void applyColorToImageView() {
        for (int id : ids) {
            RelativeLayout imageView = findViewById(id);
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

}
