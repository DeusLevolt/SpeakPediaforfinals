package com.example.speakpediaforfinals;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class GameTwoActivity extends AppCompatActivity {

    private SpeechRecognizer speechRecognizer;
    private TextView hintTextView;
    private EditText gameTwoTextView;
    private ImageButton speakButton;
    private ArrayList<String> questions;
    private int currentQuestionIndex;
    private ArrayList<String> correctAnswers;
    private CountDownTimer countDownTimer;
    private TextView timer;
    private int[] ids = {R.id.quizblue};


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_two_layout);

        ImageView back = findViewById(R.id.back_button_quiz);
        timer = findViewById(R.id.timerTextView);
        hintTextView = findViewById(R.id.hintTextView);
        speakButton = findViewById(R.id.game_speak_2);
        gameTwoTextView = findViewById(R.id.game_two_textview);
        ImageView speakQuizInfo = findViewById(R.id.speakQuiz_info);
        startTimer();
        loadSavedColor();
        saveSelectedColor();

        speakQuizInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInfoDialog();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back_to_game_act = new Intent(GameTwoActivity.this, GameActivity.class);
                startActivity(back_to_game_act);
            }
        });
        // Initialize questions and correctAnswers as pairs
        ArrayList<Pair<String, String>> questionAnswerPairs = new ArrayList<>();

// Add your 10 questions here with their correct answers
        questionAnswerPairs.add(new Pair<>("What is the capital of France?", "paris"));
        questionAnswerPairs.add(new Pair<>("In which year did World War II end?", "1945"));
        questionAnswerPairs.add(new Pair<>("Who wrote 'Romeo and Juliet'? note: only the surname", "shakespeare"));
        questionAnswerPairs.add(new Pair<>("What is the largest planet in our solar system?", "jupiter"));
        questionAnswerPairs.add(new Pair<>("What is the square root of 144?", "12"));
        questionAnswerPairs.add(new Pair<>("Which element has the chemical symbol 'O'?", "oxygen"));
        questionAnswerPairs.add(new Pair<>("Which planet is known as the Red Planet?", "mars"));
        questionAnswerPairs.add(new Pair<>("What is the currency of Japan?", "yen"));
        questionAnswerPairs.add(new Pair<>("In what year did the Titanic sink?", "1912"));
        questionAnswerPairs.add(new Pair<>("What is the world's largest ocean?","pacific ocean"));
        questionAnswerPairs.add(new Pair<>("Who developed the theory of relativity?","albert einstein"));
// Add more questions...

// Shuffle the pairs
        Collections.shuffle(questionAnswerPairs);

// Separate the shuffled pairs into questions and correctAnswers lists
        questions = new ArrayList<>();
        correctAnswers = new ArrayList<>();

        for (Pair<String, String> pair : questionAnswerPairs) {
            questions.add(pair.first);
            correctAnswers.add(pair.second);
        }
        // Initialize currentQuestionIndex
        currentQuestionIndex = 0;
        // Display the first question
        displayNextQuestion();


        // Initialize SpeechRecognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                // Speech recognition is ready
                Log.d("SpeechRecognition", "Ready for speech");
            }

            @Override
            public void onBeginningOfSpeech() {
                // User started speaking
                Log.d("SpeechRecognition", "Beginning of speech");
            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {
                // User finished speaking
                Log.d("SpeechRecognition", "End of speech");
            }

            @Override
            public void onResults(Bundle results) {
                // Get the speech recognition results
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                if (matches != null && !matches.isEmpty()) {
                    // Display the recognized speech in the gameTwoTextView
                    String userAnswer = matches.get(0);
                    gameTwoTextView.setText(userAnswer);

                    checkAnswer(userAnswer);
                }
            }

            private void checkAnswer(String userAnswer) {
                // Assuming currentQuestionIndex corresponds to the index of the current question
                String correctAnswer = correctAnswers.get(currentQuestionIndex);

                // Compare the user's answer with the correct answer
                if (userAnswer.equalsIgnoreCase(correctAnswer)) {
                    // Correct answer handling, update score or perform other actions
                    showToast("Correct!");
                } else {
                    // Incorrect answer handling, if needed
                    showToast("Incorrect. The correct answer is: " + correctAnswer);
                }

                // Move to the next question
                currentQuestionIndex++;
                displayNextQuestion();
            }


            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }

            // Implement other SpeechRecognizer methods as needed

            @Override
            public void onError(int errorCode) {
                // Handle speech recognition error
            }
        });

        speakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start speech recognition
                startSpeechRecognition();
            }
        });

        // Display the first question
        displayNextQuestion();
    }

    private void showInfoDialog() {
        // Create a custom dialog
        final Dialog infoDialog = new Dialog(this);
        infoDialog.setContentView(R.layout.custom_info_dialog); // Create a layout file for your custom dialog

        // Customize your dialog's UI components
        TextView dialogText = infoDialog.findViewById(R.id.dialog_text);
        // Set the text you want to display in the dialog
        dialogText.setText("To play, click the speaker icon and wait for a sound. Then say your answer before the time runs out.");

        // Show the dialog
        infoDialog.show();

    }

    private void startTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel(); // Cancel the previous timer if it exists
        }

        countDownTimer = new CountDownTimer(20000, 1000) { // 20 seconds, tick every 1 second
            public void onTick(long millisUntilFinished) {
                timer.setText(millisUntilFinished / 1000 + "s");
            }

            public void onFinish() {
                // Timer finished, handle the logic for the end of the time (e.g., move to the next question)
                showToast("Time's up!");

                // Move to the next question
                currentQuestionIndex++;
                if (currentQuestionIndex < questions.size()) {
                    displayNextQuestion();

                    startTimer();
                } else {
                    hintTextView.setText("all questions answered!");
                }
            }
        }.start();
    }



    private void displayNextQuestion() {
        if (currentQuestionIndex < questions.size()) {
            // Display the next question in the hintTextView
            hintTextView.setText(questions.get(currentQuestionIndex));

            // Add a delay before clearing the EditText
            gameTwoTextView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Clear the EditText for the new question
                    gameTwoTextView.getText().clear();
                }
            }, 1500); // Adjust the delay time as needed (1000 milliseconds = 1 second)

            // Start the timer for the new question
            startTimer();
        } else {
            // All questions have been displayed
            hintTextView.setText("All questions answered");
        }
    }


    private void startSpeechRecognition() {
        // Start the speech recognition intent
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        speechRecognizer.startListening(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release resources when the activity is destroyed
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void checkAnswer(String userAnswer) {
        // Assuming currentQuestionIndex corresponds to the index of the current question
        String correctAnswer = correctAnswers.get(currentQuestionIndex);

        // Compare the user's answer with the correct answer
        if (userAnswer.equalsIgnoreCase(correctAnswer)) {
            // Correct answer handling, update score or perform other actions
            showToast("Correct!");
        } else {
            // Incorrect answer handling, if needed
            showToast("Incorrect. The correct answer is: " + correctAnswer);
        }

        // Move to the next question
        displayNextQuestion();
    }

    private void showToast(String message) {
        Toast.makeText(GameTwoActivity.this, message, Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onStop() {
        super.onStop();
        // Cancel the timer when the activity is no longer visible
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
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

}
