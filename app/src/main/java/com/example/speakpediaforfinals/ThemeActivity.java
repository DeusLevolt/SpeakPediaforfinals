package com.example.speakpediaforfinals;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;


public class ThemeActivity extends Activity {

    private TextView background;
    private TextView fonts;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.theme_layout);

        background = findViewById(R.id.background);
        fonts = findViewById(R.id.fonts);
        ImageView back = findViewById(R.id.back_button_theme);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back_to_settings = new Intent(ThemeActivity.this, SettingsActivity.class);
                startActivity(back_to_settings);
            }
        });

    }
}
