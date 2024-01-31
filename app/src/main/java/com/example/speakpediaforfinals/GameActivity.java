package com.example.speakpediaforfinals;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

    private int[] ids = {R.id.blue_quiz ,R.id.shared_background_1, R.id.shared_background_2, R.id.shared_background_3, R.id.shared_background_4, R.id.shared_background_5, R.id.shared_background_6, R.id.shared_background_7, R.id.shared_background_8, R.id.shared_background_9, R.id.shared_background_10, R.id.blue1, R.id.blue2};
    private MediaPlayer click;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.games_layout);


        TextView Jumbled = findViewById(R.id.jumbled_word);
        TextView Tts = findViewById(R.id.game_2);
        ImageView back = findViewById(R.id.back_button_game);
        TextView quiz_game = findViewById(R.id.quiz_game);
        ImageView help = findViewById(R.id.gamehelpbutton);
        click = MediaPlayer.create(this, R.raw.beep);
        loadSavedColor();

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHelpDialog();
            }
        });

        quiz_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent game_two = new Intent(GameActivity.this, GameTwoActivity.class);
                startActivity(game_two);

            }
        });
        Tts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent tts = new Intent(GameActivity.this, Text_to_speech.class);
                startActivity(tts);
                playButtonClickSound();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back_to_main = new Intent(GameActivity.this, MainActivity.class);
                startActivity(back_to_main);
            }
        });

        Jumbled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent jumbled_words = new Intent(GameActivity.this, MainGameActivity.class);
                startActivity(jumbled_words);
                playButtonClickSound();
            }
        });
    }
    private void playButtonClickSound() {
        if (click != null) {
            click.start();
        }
    }
    @Override
    protected void onDestroy() {
        // Release MediaPlayer resources when the activity is destroyed
        if (click != null) {
            click.release();
        }
        super.onDestroy();
    }

    private void showHelpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Help"); // Set the title of the dialog
        builder.setMessage("Jumbled Word game is general questions (except math)while the speak quiz is dialects and Phillipine's region."); // Set the message to be displayed

        // Set a button for the user to acknowledge the message
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // You can perform any action here upon clicking the OK button
                dialog.dismiss(); // Dismiss the dialog
            }
        });

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
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
