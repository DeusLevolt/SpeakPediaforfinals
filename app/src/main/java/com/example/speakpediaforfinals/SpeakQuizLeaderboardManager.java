package com.example.speakpediaforfinals;

import android.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SpeakQuizLeaderboardManager {

    private static SpeakQuizLeaderboardManager instance;
    private List<Pair<String, Integer>> leaderboard;

    SpeakQuizLeaderboardManager() {
        leaderboard = new ArrayList<>();
    }

    public static synchronized SpeakQuizLeaderboardManager getInstance() {
        if (instance == null) {
            instance = new SpeakQuizLeaderboardManager();
        }
        return instance;
    }

    // Add a new user score to the leaderboard
    public void addUserScore(String username, int score) {
        leaderboard.add(new Pair<>(username, score));
        sortLeaderboard();
    }

    // Get the current leaderboard data
    public List<Pair<String, Integer>> getLeaderboard() {
        return leaderboard;
    }

    // Sort the leaderboard in descending order based on scores
    private void sortLeaderboard() {
        Collections.sort(leaderboard, new Comparator<Pair<String, Integer>>() {
            @Override
            public int compare(Pair<String, Integer> p1, Pair<String, Integer> p2) {
                return Integer.compare(p2.second, p1.second);
            }
        });
    }
}


