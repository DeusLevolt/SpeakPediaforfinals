package com.example.speakpediaforfinals;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

    private int[] ids = {R.id.shared_background_1, R.id.shared_background_2, R.id.shared_background_3, R.id.shared_background_4, R.id.shared_background_5, R.id.shared_background_6, R.id.shared_background_7, R.id.shared_background_8, R.id.shared_background_9, R.id.shared_background_10, R.id.blue1, R.id.blue2};


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.games_layout);


        TextView Jumbled = findViewById(R.id.jumbled_word);
        TextView Tts = findViewById(R.id.game_2);
        ImageView back = findViewById(R.id.back_button_game);
        ImageView top = findViewById(R.id.shared_background_5);
        ImageView bot = findViewById(R.id.shared_background_6);
        loadSavedColor();

        Tts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent tts = new Intent(GameActivity.this, Text_to_speech.class);
                startActivity(tts);
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
            }
        });
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
