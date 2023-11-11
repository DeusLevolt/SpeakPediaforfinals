package com.example.speakpediaforfinals;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TranslatorActivity extends AppCompatActivity {
    private EditText inputEditText;
    private TextView translationTextView;
    public ImageView languageSelector;
    private int[] relative_layout = {R.id.translatorbluebg};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.translator_layout);

        inputEditText = findViewById(R.id.input_edit_text);
        translationTextView = findViewById(R.id.translate_textView);
        ImageView back = findViewById(R.id.back_button_translator);
        Button translateButton = findViewById(R.id.translate_button);
        Button clearButton = findViewById(R.id.clear_button);
        loadSavedColor();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back_to_main = new Intent(TranslatorActivity.this, MainActivity.class);
                startActivity(back_to_main);
                overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);

            }
        });

        translateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translateText();
            }

            private void translateText() {

            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearTranslation();
            }

            private void clearTranslation() {
            }
        });

    }

    private void loadSavedColor () {
        SharedPreferences preferences = getSharedPreferences("theme_preferences", MODE_PRIVATE);
        int savedColor = preferences.getInt("selected_color", -1);
        ThemeColorManager.getInstance().setSelectedColor(savedColor);
        // Apply the saved color on activity creation
        applyColorToRelativeLayout();
    }

    private void applyColorToRelativeLayout() {
        for (int id : relative_layout){
            RelativeLayout bg = findViewById(id);
            if (bg != null){
                int selectedColor = ThemeColorManager.getInstance().getSelectedColor();
                if (selectedColor != -1){
                    bg.setBackgroundColor(selectedColor);
                }
            }
        }
    }

}

