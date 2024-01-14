package com.example.speakpediaforfinals;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class TermsAndConditionsActivity extends AppCompatActivity {

    private int[] ids = {R.id.shared_background_1, R.id.shared_background_2, R.id.shared_background_3,
            R.id.shared_background_4, R.id.shared_background_5, R.id.shared_background_6,
            R.id.shared_background_7, R.id.shared_background_8, R.id.shared_background_9,
            R.id.shared_background_10, R.id.button1, R.id.button2, R.id.button3, R.id.headingAbout};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_conditions);

        Button acceptButton = findViewById(R.id.acceptButton);
        Button declineButton = findViewById(R.id.declineButton);

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // User accepted the Terms and Conditions
                saveTermsAcceptance(true);
                Intent main = new Intent(TermsAndConditionsActivity.this,MainActivity.class);
                startActivity(main);

                // You might want to navigate back to the main activity or perform any other actions
                finish();
            }
        });

        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // User declined the Terms and Conditions
                saveTermsAcceptance(false);

                // You might want to handle the case where the user declines (e.g., show a message, close the app)
                finish();
            }
        });
    }

    private void saveTermsAcceptance(boolean accepted) {
        // Save the acceptance status in SharedPreferences
        SharedPreferences preferences = getSharedPreferences("app_preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("terms_accepted", accepted);

        // Set the "terms_shown_before" flag to true if terms are accepted
        editor.putBoolean("terms_shown_before", true);

        editor.apply();
    }

}
