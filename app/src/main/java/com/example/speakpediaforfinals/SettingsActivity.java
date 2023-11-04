package com.example.speakpediaforfinals;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class SettingsActivity extends Activity {
    private final String[] colors = {"Red", "Green", "Blue", "Yellow", "Purple"};

    private TextView Theme;
    private TextView AboutUs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

         Theme = findViewById(R.id.theme);
         AboutUs = findViewById(R.id.about);
        ImageView back = findViewById(R.id.back_button_settings);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back_to_main = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(back_to_main);
            }
        });

         Theme.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent theme = new Intent(SettingsActivity.this, ThemeActivity.class);
                 startActivity(theme);
             }
         });


    }
}
