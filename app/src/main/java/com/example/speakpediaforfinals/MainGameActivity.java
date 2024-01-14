package com.example.speakpediaforfinals;

import android.app.Dialog;
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

    private static final String[] words = {"virus", "whale", "money", "titan", "china", "music", "puppy", "books", "phone","photo","table","train","water","metal","cable",};
    private Map<String, String> wordHintMap;
    private Button[] letterButtons;
    private List<String> shownWords;
    private int score = 0;
    private static final String KEY_CURRENT_QUESTION_INDEX = "current_question_index";
    private static final String KEY_IS_GAME_OVER = "is_game_over";

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
        ImageView help = findViewById(R.id.helpbuttongame1);
        loadSavedColor();
        
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInfoDialog();
            }
        });

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
                if (isGameOver) {
                    // Save the remaining score in the leaderboard with a default username
                    saveToLeaderboard("quitedUser", score);
                }
                showQuitConfirmationDialog();
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

    private void showInfoDialog() {
        // Create a custom dialog
        final Dialog infoDialog = new Dialog(this);
        infoDialog.setContentView(R.layout.custom_info_dialog); // Create a layout file for your custom dialog

        // Customize your dialog's UI components
        TextView dialogText = infoDialog.findViewById(R.id.dialog_text);
        // Set the text you want to display in the dialog
        dialogText.setText("This game is all general questions. (except math)");

        // Show the dialog
        infoDialog.show();
    }

    private boolean startGame() {
        do {
            correctWord = getJumbledWord();
        } while (shownWords.contains(correctWord));
        baseScore = 1;
        jumbledWordTextView.setText("");
        // Add the selected word to the list of shown words
        shownWords.add(correctWord);

        // Update the hint text based on the current jumbled word
        String hint = wordHintMap.get(getOriginalWord(correctWord));
        hintTextView.setText(hint);

        // Rest of the code...
        String shuffledWord = shuffleWord(correctWord);
        // Ensure the shuffled word has a length of at least 5 before accessing characters at index 4 or higher
        if (shuffledWord.length() >= 5) {
            // Update the buttons with the jumbled letters
            for (int i = 0; i < letterButtons.length; i++) {
                char letter = shuffledWord.charAt(i);
                letterButtons[i].setText(String.valueOf(letter));
                letterButtons[i].setVisibility(View.VISIBLE);  // Ensure the button is visible
                letterButtons[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Button button = (Button) v;
                        String buttonText = button.getText().toString();
                        String currentText = jumbledWordTextView.getText().toString();
                        jumbledWordTextView.setText(currentText + buttonText);

                        // hide the button when the user clicks it
                        button.setVisibility(View.INVISIBLE);

                        // re-enable the delete button
                        ImageButton deleteButton = findViewById(R.id.imageButtondel);
                        deleteButton.setEnabled(true);
                    }
                });
            }
            return true; // Indicate that a new word has been successfully selected
        }
        return true; // Indicate that a new word has been successfully selected
    }


    private void verifyAnswer() {
        String userAnswer = jumbledWordTextView.getText().toString().trim();
        if (!userAnswer.isEmpty() && userAnswer.equalsIgnoreCase(getOriginalWord(correctWord))) {
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
            updateScore();
            jumbledWordTextView.setText("");  // Clear the text after a correct answer
            //call updatescore when the answer is correct

            // Check if all words have been shown
            if (shownWords.size() == words.length) {
                // Show the username input notice since all words have been answered
                showUsernameInputNotice();

                // Reset the game after finishing
                resetGame();
            } else {
                // Continue the game by starting a new word
                startGame();
            }
        } else {
            Toast.makeText(this, "Incorrect. Try again!", Toast.LENGTH_SHORT).show();
        }

        // Call the new method to display the original word
        displayOriginalWord(correctWord);

        if (isGameOver){
           saveToLeaderboard("quited_user", score);
        }
    }

    private void resetGame() {
        // Reset any game-related variables or UI elements here
        shownWords.clear();
        score = 0;
        baseScore = 1;
        // Reset any other game-specific states or variables as needed

        // Start a new game
        startGame();
    }





    private void showUsernameInputNotice() {
        // Use a Dialog to prompt the user for a username
        final EditText usernameInput = new EditText(this);

        new AlertDialog.Builder(this)
                .setTitle("Game Over")
                .setMessage("Let's record your data. Please enter your desired username:")
                .setView(usernameInput)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String username = usernameInput.getText().toString();
                        if (!username.isEmpty()) {
                            // Save the score with the entered username
                            saveToLeaderboard(username, score);
                        }
                        // Finish the activity (quit the game) regardless of whether a username is provided or not
                        finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // If the user clicks "Cancel," finish the activity (quit the game)
                        finish();
                    }
                })
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


    private int baseScore = 1; // Initial base score

    private void updateScore() {
        // Increment the base score for each correct word
        baseScore++;

        // Update the score with the incremented base score
        score += baseScore;

        saveScoreToPreferences();
        Log.d("ScoreDebug", "Current Score: " + score);

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
        wordHintMap.put("whale", "What is the largest mammal on Earth?");
        wordHintMap.put("china", "What country is the most populated country in the world?");
        wordHintMap.put("money", "What do we use to buy things?");
        wordHintMap.put("music", "Vocal, instrumental, or mechanical sounds having rhythm, melody, or harmony.");
        wordHintMap.put("puppy", "What do you call a young dog?");
        wordHintMap.put("books", "Written or printed work consist of pages.");
        wordHintMap.put("phone", "Refers to a communication device that allows people to speak with each other over long distances.");
        wordHintMap.put("titan","In astronomy, what is the name of the largest moon of Saturn?");
        wordHintMap.put("photo","What term refers to a single, still image that captures a moment in time?");
        wordHintMap.put("virus","What term describes malicious software that can self-replicate and spread to other systems?");
        wordHintMap.put("table", "What versatile piece of furniture has a flat top and legs, often used for various activities like dining or working?");
        wordHintMap.put("train", "What mode of transportation typically runs on tracks and carries passengers or cargo?");
        wordHintMap.put("cable", "What connects electronic devices and allows them to communicate with each other?");
        wordHintMap.put("water", "What essential liquid is colorless, tasteless, and odorless, crucial for sustaining life?");
        wordHintMap.put("metal", "What solid material is known for its conductivity, strength, and often used in construction and manufacturing?");

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
        // Get the letters of the current jumbled word
        char[] letters = correctWord.toCharArray();

        // Shuffle the letters using a Fisher-Yates shuffle
        Random random = new Random();
        for (int i = letters.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            char temp = letters[index];
            letters[index] = letters[i];
            letters[i] = temp;
        }

        // Log the shuffled word for debugging
        String shuffledWord = new String(letters);
        Log.d("ShuffleDebug", "Shuffled Word: " + shuffledWord);

        // Update the buttons with the shuffled letters
        for (int i = 0; i < letterButtons.length; i++) {
            letterButtons[i].setText(String.valueOf(letters[i]));
            letterButtons[i].setVisibility(View.VISIBLE);
        }

        // Call the new method to display the original word
        displayOriginalWord(correctWord);
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

    private void showQuitConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quit Game");
        builder.setMessage("Are you sure you want to quit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // If the user clicks "Yes," finish the activity (quit the game)
                showUsernameInputNotice();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // If the user clicks "No," dismiss the dialog
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        showQuitConfirmationDialog();
    }

}