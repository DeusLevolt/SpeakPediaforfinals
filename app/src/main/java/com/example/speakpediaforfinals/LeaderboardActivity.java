// LeaderboardActivity.java
package com.example.speakpediaforfinals;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        ImageView back = findViewById(R.id.back_button_leaderboard);

        // Fetch and display leaderboard data
        displayLeaderboard();
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

}
