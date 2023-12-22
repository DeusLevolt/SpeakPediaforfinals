package com.example.speakpediaforfinals;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainGameActivity extends AppCompatActivity {

    private static final String KEY_CORRECT_WORD = "correct_word";
    private int [] relative_layout_bg = {R.id.jumbledwordsblue};
    private TextView jumbledWordTextView;
    private TextView hintTextView;

    private String correctWord;

    private static final String[] words = {"smell", "crowd", "gifts", "light", "water", "music", "paint", "book", "phone", "games"};
    private Map<String, String> wordHintMap;
    private Button[] letterButtons;
    private List<String> shownWords;
    private int currentWordIndex = 0; // To keep track of the current word index
    private int score = 0;
    private static final String KEY_CURRENT_QUESTION_INDEX = "current_question_index";
    private static final String KEY_IS_GAME_OVER = "is_game_over";
    private static final int NUM_LETTER_BUTTONS = 5;


    private int currentQuestionIndex = 0;
    private boolean isGameOver = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jumbled_words_layout);

        shownWords = new ArrayList<>();
        jumbledWordTextView = findViewById(R.id.jumbledWordTextView);
        Button submitButton = findViewById(R.id.submitButton);
        ImageView backButton = findViewById(R.id.back_button_jumble);
        ImageView leaderboardImageView = findViewById(R.id.leaderboard);
        hintTextView = findViewById(R.id.hintTextView);
        initializeWordHintMap();
        letterButtons = new Button[5];
        letterButtons[0] = findViewById(R.id.button1);
        letterButtons[1] = findViewById(R.id.button2);
        letterButtons[2] = findViewById(R.id.button3);
        letterButtons[3] = findViewById(R.id.button4);
        letterButtons[4] = findViewById(R.id.button5);
        ImageButton deleteButton = findViewById(R.id.imageButtondel);
        ImageButton shuffleButton = findViewById(R.id.shuffle_button);
        loadSavedColor();
        loadSavedScore();

        leaderboardImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start LeaderboardActivity when the leaderboard ImageView is clicked
                Intent leaderboardIntent = new Intent(MainGameActivity.this, LeaderboardActivity.class);
                startActivity(leaderboardIntent);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteLastCharacter();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainGameActivity.this, GameActivity.class);
                startActivity(intent);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyAnswer();
            }
        });


        shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reshuffleGame();
            }
        });

        if (savedInstanceState != null) {
            currentQuestionIndex = savedInstanceState.getInt(KEY_CURRENT_QUESTION_INDEX, 0);
            isGameOver = savedInstanceState.getBoolean(KEY_IS_GAME_OVER, false);
        } else {
            startGame(); // Initialize a new game if no saved instance state.
        }
    }

    private boolean startGame() {
        // Initialize the current question index
        currentQuestionIndex = 0;
        correctWord = getJumbledWord();
        jumbledWordTextView.setText(""); // Clear the TextView initially

        // Check if all words have been shown once
        if (shownWords.size() == words.length) {
            // If all words have been shown once, reset the list
            shownWords.clear();
            // Optionally, you can display a message or perform other actions for completing the game
            Toast.makeText(this, "Congratulations! You've completed the game.", Toast.LENGTH_SHORT).show();
            // Restart the game by calling the method recursively
            showUsernameInputNotice();
            return startGame();
        }
        List<String> notShownWords = getNotShownWords();

        // Get a random word from the list of not-shown-yet words
        if (!notShownWords.isEmpty()) {
            Random random = new Random();
            int randomIndex = random.nextInt(notShownWords.size());
            String word = notShownWords.get(randomIndex);

            Log.d("GameDebug", "Selected Word: " + word);

            // If the word has already been shown in this session, get the next word
            while (shownWords.contains(word)) {
                word = words[currentWordIndex];
                currentWordIndex = (currentWordIndex) % words.length;
            }

            // Add the selected word to the list of shown words
            shownWords.add(word);
            // Set the current word index for the next iteration
            currentWordIndex = (currentWordIndex + 1) % words.length;

            // Set a fixed width for the buttons to prevent them from moving when hidden
            int buttonWidth = getResources().getDimensionPixelSize(R.dimen.button_fixed_width);
            for (Button button : letterButtons) {
                button.getLayoutParams().width = buttonWidth;
                button.requestLayout(); // Refresh the layout after setting the width
            }
            //set all letter buttons to visible
            for (Button button : letterButtons) {
                button.setVisibility(View.VISIBLE);
            }

            // Update the hint text based on the current jumbled word
            String hint = wordHintMap.get(getOriginalWord(correctWord));
            hintTextView.setText(hint);

            // Shuffle the letters of the jumbled word to ensure they are displayed in random order
            String shuffledWord = shuffleWord(correctWord);

            // Ensure the shuffled word has a length of at least 5 before accessing characters at index 4 or higher
            if (shuffledWord.length() >= 5) {
                // Update the buttons with the jumbled letters
                for (int i = 0; i < letterButtons.length; i++) {
                    char letter = shuffledWord.charAt(i);
                    letterButtons[i].setText(String.valueOf(letter));
                    letterButtons[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Button button = (Button) v;
                            String buttonText = button.getText().toString();
                            String currentText = jumbledWordTextView.getText().toString();
                            jumbledWordTextView.setText(currentText + buttonText);

                            //hide the button when the user click it
                            button.setVisibility(View.INVISIBLE);

                            //re-enable the delete button
                            ImageButton deleteButton = findViewById(R.id.imageButtondel);
                            deleteButton.setEnabled(true);

                        }
                    });
                }
                return true; // Indicate that a new word has been successfully selected
            }
        } else {
            // Handle the case where all words have been shown at least once
            // You might want to reshuffle, reset, or handle this scenario based on your requirements
            Toast.makeText(this, "All questions have been answered!", Toast.LENGTH_SHORT).show();
            return false; // Indicate that no new word has been selected
        }
        return true; // Indicate that a new word has been successfully selected
    }


    private List<String> getNotShownWords() {
        List<String> notShownWords = new ArrayList<>();
        for (String word : words) {
            if (!shownWords.contains(word)) {
                notShownWords.add(word);
            }
        }
        return notShownWords;
    }


    private void verifyAnswer() {
        String userAnswer = jumbledWordTextView.getText().toString().trim();
        if (!userAnswer.isEmpty() && userAnswer.equalsIgnoreCase(getOriginalWord(correctWord))) {
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
            updateScore();
            startGame();
        } else {
            Toast.makeText(this, "Incorrect. Try again!", Toast.LENGTH_SHORT).show();
        }

        // Call the new method to display the original word
        displayOriginalWord(correctWord);
    }


    private void showUsernameInputNotice() {
        // Use a Dialog to prompt the user for a username
        final EditText usernameInput = new EditText(this);

        new AlertDialog.Builder(this)
                .setTitle("Game Over")
                .setMessage("Congratulations! You've completed the game. Enter your username:")
                .setView(usernameInput)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String username = usernameInput.getText().toString();
                        saveToLeaderboard(username, score);
                        // Optionally, you can navigate to the leaderboard activity or perform other actions.
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveToLeaderboard(String username, int score) {
        // Load existing leaderboard entries
        List<LeaderboardEntry> leaderboardEntries = loadLeaderboard();

        // Create a new leaderboard entry for the current user
        LeaderboardEntry newEntry = new LeaderboardEntry(username, score);

        // Add the new entry to the list
        leaderboardEntries.add(newEntry);
        // Sort the leaderboard entries by score in descending order
        Collections.sort(leaderboardEntries, (entry1, entry2) -> Integer.compare(entry2.getScore(), entry1.getScore()));

        // Limit the leaderboard to the top 10 entries
        if (leaderboardEntries.size() > 10) {
            leaderboardEntries = leaderboardEntries.subList(0, 10);
        }

        // Save the updated list to SharedPreferences
        saveLeaderboard(leaderboardEntries);
    }


    private void saveLeaderboard(List<LeaderboardEntry> leaderboardEntries) {
        // Save the leaderboard entries to SharedPreferences
        SharedPreferences preferences = getSharedPreferences("leaderboard_preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // Convert the list to a JSON string
        String entriesJson = new Gson().toJson(leaderboardEntries);

        // Save the JSON string
        editor.putString("leaderboard_entries", entriesJson);
        editor.apply();
    }


    private void updateScore() {
        // Generate a random score between 1 and 2 for each question
        int randomScore = 1 + new Random().nextInt(2); // Random value between 1 and 2 (inclusive)

        // Update the score with the random value
        score += randomScore;

        saveScoreToPreferences();

        // Update UI or perform any other actions related to scoring.
        TextView scoreTextView = findViewById(R.id.scoreTextView);
        scoreTextView.setText(String.valueOf(score));
    }

    private List<LeaderboardEntry> loadLeaderboard() {
        // Load existing leaderboard entries from SharedPreferences
        SharedPreferences preferences = getSharedPreferences("leaderboard_preferences", MODE_PRIVATE);
        String entriesJson = preferences.getString("leaderboard_entries", "");

        // Print the entriesJson to log for debugging
        Log.d("LeaderboardDebug", "Entries from SharedPreferences: " + entriesJson);

        // If no entries are found, return an empty list
        if (entriesJson.isEmpty()) {
            return new ArrayList<>();
        }

        // Convert the JSON string to a list of LeaderboardEntry objects
        Type type = new TypeToken<List<LeaderboardEntry>>(){}.getType();
        return new Gson().fromJson(entriesJson, type);
    }
    private void saveScoreToPreferences() {
        SharedPreferences preferences = getSharedPreferences("game_preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("user_score", score);
        editor.apply();
    }
    private void loadSavedScore() {
        SharedPreferences preferences = getSharedPreferences("game_preferences", MODE_PRIVATE);
        score = preferences.getInt("user_score", 0); // Default value is 0 if the score is not found
    }


    private String getOriginalWord(String jumbledWord) {
        for (String word : words) {
            if (jumbledWord.equalsIgnoreCase(jumbleWord(word))) {
                return word;
            }
        }
        return ""; // Return an empty string if the original word is not found.
    }

    private String getJumbledWord() {
        Random random = new Random();
        String word = words[random.nextInt(words.length)];
        correctWord = word; // Store the original word for later comparison.
        return jumbleWord(word);
    }

    private String jumbleWord(String word) {
        char[] chars = word.toCharArray();
        Random random = new Random(word.hashCode());

        // Shuffle the letters using Fisher-Yates algorithm
        for (int i = chars.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            char temp = chars[index];
            chars[index] = chars[i];
            chars[i] = temp;
        }

        // Convert the char array back to a string
        return new String(chars);
    }

    private String shuffleWord(String word) {
        char[] chars = word.toCharArray();
        Random random = new Random(word.hashCode());
        for (int i = chars.length - 1; i >= 0; i--) {
            int index = random.nextInt(i + 1);
            char temp = chars[index];
            chars[index] = chars[i];
            chars[i] = temp;
        }
        return new String(chars);
    }

    private void initializeWordHintMap() {
        wordHintMap = new HashMap<>();
        // Add word-hint pairs to the map
        wordHintMap.put("smell", "A human human sensory to pick up scent.");
        wordHintMap.put("crowd", " A large number of people gathered together in a disorganized or unruly way.");
        wordHintMap.put("gifts", "Thing given willingly to someone without payment.");
        wordHintMap.put("light", "the natural agent that stimulates sight and makes things visible.");
        wordHintMap.put("water", "transparent, odorless, tasteless liquid compound.");
        wordHintMap.put("music", " vocal, instrumental, or mechanical sounds having rhythm, melody, or harmony.");
        wordHintMap.put("paint", "A liquid substance that is applied to various surfaces to create a protective or decorative coating.");
        wordHintMap.put("books", "Written or printed work consist of pages.");
        wordHintMap.put("phone", "refers to a communication device that allows people to speak with each other over long distances.");
        wordHintMap.put("games", "structured, interactive activities for enjoyment, involving entertainment, and skill development.");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CURRENT_QUESTION_INDEX, currentQuestionIndex);
        outState.putBoolean(KEY_IS_GAME_OVER, isGameOver);
    }

    private void deleteLastCharacter() {
        String currentText = jumbledWordTextView.getText().toString();
        if (!currentText.isEmpty()) {
            // Remove the last character from the current text
            String deletedLetter = currentText.substring(currentText.length() - 1);
            currentText = currentText.substring(0, currentText.length() - 1);
            jumbledWordTextView.setText(currentText);

            // Bring back the button for the deleted letter
            showButtonForLetter(deletedLetter);

            // Disable the delete button if there are no letters left
            ImageButton deleteButton = findViewById(R.id.imageButtondel);
            if (currentText.isEmpty()) {
                deleteButton.setEnabled(false);
            }
        }
    }

    private void showButtonForLetter(String letter) {
        for (Button button : letterButtons) {
            if (button.getText().toString().equals(letter)) {
                button.setVisibility(View.VISIBLE);
                break;
            }
        }
    }

    private void reshuffleGame() {
        // Reshuffle the jumbled word and update the jumbledWordTextView
        correctWord = getJumbledWord();
        jumbledWordTextView.setText("");

        // Reshuffle the letters of the jumbled word to update the buttons
        String shuffledWord = shuffleWord(correctWord);
        for (int i = 0; i < letterButtons.length; i++) {
            char letter = shuffledWord.charAt(i);
            letterButtons[i].setText(String.valueOf(letter));
            letterButtons[i].setVisibility(View.VISIBLE);
        }

        // Call the new method to display the original word
        displayOriginalWord(correctWord);

        if (!startGame()){
            // Handle the case where all words have been shown at least once
            // You might want to reshuffle, reset, or handle this scenario based on your requirements
            Toast.makeText(this, "All questions have been answered!", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadSavedColor () {
        SharedPreferences preferences = getSharedPreferences("theme_preferences", MODE_PRIVATE);
        int savedColor = preferences.getInt("selected_color", -1);
        ThemeColorManager.getInstance().setSelectedColor(savedColor);
        applyColorToRelativeLayout();// Apply the saved color on activity creation
    }

    private void applyColorToRelativeLayout() {
        for (int id : relative_layout_bg){
            RelativeLayout relativeLayout = findViewById(id);
            if (relativeLayout != null){
                int selectedColor = ThemeColorManager.getInstance().getSelectedColor();
                if (selectedColor != -1){
                    relativeLayout.setBackgroundColor(selectedColor);
                }
            }
        }
    }
    private void displayOriginalWord(String jumbledWord) {
        String originalWord = getOriginalWord(jumbledWord);
        String hint = wordHintMap.get(originalWord);
        hintTextView.setText(hint);
    }

}