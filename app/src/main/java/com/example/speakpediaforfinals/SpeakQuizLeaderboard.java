package com.example.speakpediaforfinals;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class SpeakQuizLeaderboard extends AppCompatActivity {

    private TextView leaderboardTextView;
    private SpeakQuizLeaderboardManager leaderboardManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.speak_quiz_leaderboard_layout);

        leaderboardTextView = findViewById(R.id.leaderboardlist);
        ImageView back = findViewById(R.id.LeaderboardBack);
        leaderboardManager = SpeakQuizLeaderboardManager.getInstance();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backtospeakquiz = new Intent(SpeakQuizLeaderboard.this, GameTwoActivity.class);
                startActivity(backtospeakquiz);
            }
        });

        // Display the leaderboard data
        displayLeaderboardData();
    }

    // Method to update and display leaderboard data
    private void displayLeaderboardData() {
        // Retrieve the leaderboard data from SpeakQuizLeaderboardManager or other source
        // Adjust based on your implementation
        List<Pair<String, Integer>> leaderboardData = leaderboardManager.getLeaderboard();

        // Build the leaderboard text
        StringBuilder leaderboardText = new StringBuilder();
        int rank = 1;
        for (Pair<String, Integer> entry : leaderboardData) {
            leaderboardText.append(rank).append(". ").append(entry.first).append(": ").append(entry.second).append("\n");
            rank++;
        }

        // Set the text to the TextView
        leaderboardTextView.setText(leaderboardText.toString());
    }
}
