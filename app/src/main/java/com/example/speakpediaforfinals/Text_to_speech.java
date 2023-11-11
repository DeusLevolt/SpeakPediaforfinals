package com.example.speakpediaforfinals;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

public class Text_to_speech extends AppCompatActivity {

    private int[] relative_layout_bg = {R.id.ttsblue};

    private TextView displayText;
    private SpeechRecognizer speechRecognizer;
    TextToSpeech textToSpeech;
    private MediaPlayer mediaPlayer;
    private static final int REQUEST_PERMISSION_CODE = 1;
    private static final int SPEECH_REQUEST_CODE = 2;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text_to_speech_layout);


        displayText = findViewById(R.id.display_text);
        ImageView imageButtonspeak = findViewById(R.id.imageButtonspeak);
        ImageView imageButtonspeech = findViewById(R.id.imageButtonspeech);
        Button back = findViewById(R.id.back_button_tts);
        setupRetrofit();
        loadSavedColor();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back_to_game = new Intent(Text_to_speech.this, GameActivity.class);
                startActivity(back_to_game);
            }
        });

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int language = textToSpeech.setLanguage(Locale.ENGLISH);
                }
            }

        });

        imageButtonspeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = displayText.getText().toString();
                int speech = textToSpeech.speak(s, TextToSpeech.QUEUE_FLUSH, null);

                String userInput = displayText.getText().toString();
                searchWord(userInput);
            }
        });

        imageButtonspeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkPermissionAndStartSpeechToText();

                displayText.setText("");
                textView.setText("");
            }

        });
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
    }

    private void checkPermissionAndStartSpeechToText() {
        //check if the record audio permission is okay
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            //request the permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_PERMISSION_CODE);
        } else {
            //is permission is already granted, start the stt
            startSpeechToText();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //release the speechRecodnizer resources
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void startSpeechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        //start the stt act and results
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            //retrieve the stt result
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                //update the textview with the recognized text
                displayText.setText(result.get(0));
            }
        }
    }

    private void searchWord(String word) {
        Call<WordResponse[]> call = dictionaryService.getWordDefinition(word, "b78b3153-8e7e-4d5c-a5cb-bb2030f5f4d1");
        call.enqueue(new Callback<WordResponse[]>() {
            @Override
            public void onResponse(Call<WordResponse[]> call, Response<WordResponse[]> response) {
                if (response.isSuccessful()) {
                    WordResponse[] wordResponses = response.body();
                    if (wordResponses != null && wordResponses.length > 0) {
                        WordResponse firstEntry = wordResponses[0];
                        String definition = parseDefinitionFromResponse(firstEntry);
                        runOnUiThread(() -> textView.setText(definition));
                    } else {
                        runOnUiThread(() -> textView.setText("No definition found."));
                    }
                } else {
                    runOnUiThread(() -> textView.setText("Unable to fetch definition. Please try again."));
                }
            }

            @Override
            public void onFailure(Call<WordResponse[]> call, Throwable t) {
                runOnUiThread(() -> textView.setText("Unable to fetch definition. Please try again."));
            }
        });
    }
    private String parseDefinitionFromResponse(WordResponse responseBody) {
        if(responseBody.getHwi().getPhonetics() == null){
            return "No definition found.";
        }
        if(responseBody.getDefinitions() == null){
            return "No definition found.";
        }
        return "Phonetics: " + responseBody.getHwi().getPhonetics()[0].getMw().toString() + "\n\nDefinition: " + responseBody.getShortDef()[0] + "\n\n";
    }

    TextView textView;
    private DictionaryService dictionaryService;

    private void setupRetrofit() {

        textView = findViewById(R.id.ttsTextView);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(
                        new Interceptor() {
                            @Override
                            public okhttp3.Response intercept(Chain chain) throws IOException {
                                Request.Builder requestBuilder = chain.request().newBuilder();
                                requestBuilder.header("Content-Type", "application/json");
                                return chain.proceed(requestBuilder.build());
                            }
                        })
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.dictionaryapi.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        dictionaryService = retrofit.create(DictionaryService.class);
    }

    private void loadSavedColor () {
        SharedPreferences preferences = getSharedPreferences("theme_preferences", MODE_PRIVATE);
        int savedColor = preferences.getInt("selected_color", -1);
        ThemeColorManager.getInstance().setSelectedColor(savedColor);
        applyColorToRelativeLayout();// Apply the saved color on activity creation
    }

    private void applyColorToRelativeLayout() {
        for (int id : relative_layout_bg){
            RelativeLayout relativeLayout = findViewById(id);
            if (relativeLayout != null){
                int selectedColor = ThemeColorManager.getInstance().getSelectedColor();
                if (selectedColor != -1){
                    relativeLayout.setBackgroundColor(selectedColor);
                }
            }
        }
    }

}
