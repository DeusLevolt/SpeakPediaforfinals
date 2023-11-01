package com.example.speakpediaforfinals;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;

public class SettingsActivity extends Activity {
    private final String[] colors = {"Red", "Green", "Blue", "Yellow", "Purple"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

        Spinner colorSpinner = findViewById(R.id.colorSpinner);

        // Create an ArrayAdapter for the Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, colors);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colorSpinner.setAdapter(adapter);

        // Set an item selected listener for the Spinner
        colorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle the color selection here
                String selectedColor = colors[position];
                // You can perform actions based on the selected color
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });
    }
    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
        // Handle the color selection here
        String selectedColor = colors[position];

        // Store the selected color in SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("selectedColor", selectedColor);
        editor.apply();

        // You can also update the background color here if needed
        updateBackgroundColor(selectedColor);
    }

    // Function to update the background color
    private void updateBackgroundColor(String colorName) {
        int color;
        switch (colorName) {
            case "Red":
                color = Color.RED;
                break;
            case "Green":
                color = Color.GREEN;
                break;
            case "Blue":
                color = Color.BLUE;
                break;
            case "Yellow":
                color = Color.YELLOW;
                break;
            case "Purple":
                color = Color.MAGENTA;
                break;
            default:
                color = Color.GRAY; // Default color
                break;
        }
        // Set the background color of the main layout
        RelativeLayout mainLayout = findViewById(R.id.main_act_layout);
        mainLayout.setBackgroundColor(color);
    }
}
