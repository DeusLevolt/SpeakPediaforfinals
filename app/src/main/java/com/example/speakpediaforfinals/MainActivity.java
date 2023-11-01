package com.example.speakpediaforfinals;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Gravity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import android.media.MediaPlayer;

import android.widget.PopupMenu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import androidx.drawerlayout.widget.DrawerLayout;

public class MainActivity extends AppCompatActivity {

    private Bundle savedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button game = findViewById(R.id.game_button);
        Button aboutus = findViewById(R.id.about_us);
        Button translator = findViewById(R.id.translator);
        Button Settings = findViewById(R.id.setting_button);


        Settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        // Load the selected color from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String selectedColor = sharedPreferences.getString("selectedColor", "DefaultColor");

        // Set the background color based on the selected color
        updateBackgroundColor(selectedColor);

        setContentView(R.layout.activity_main);

        // Other initialization code
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