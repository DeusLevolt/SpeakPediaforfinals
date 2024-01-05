package com.example.speakpediaforfinals;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class SoundActivity extends Activity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sounds);

        ImageView backbutton = findViewById(R.id.back_button_sound);
        TextView music = findViewById(R.id.music);

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backtosetting = new Intent(SoundActivity.this, SettingsActivity.class);
                startActivity(backtosetting);
            }
        });

    }
}
