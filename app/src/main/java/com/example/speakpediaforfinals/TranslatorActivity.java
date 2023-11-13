package com.example.speakpediaforfinals;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TranslatorActivity extends AppCompatActivity {
    private EditText inputEditText;
    private TextView translationTextView;
    public ImageView languageSelector;
    private int[] relative_layout = {R.id.translatorbluebg};

    private OpenAiTranslationService translationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.translator_layout);



        inputEditText = findViewById(R.id.input_edit_text);
        translationTextView = findViewById(R.id.translate_textView);
        ImageView back = findViewById(R.id.back_button_translator);
        Button translateButton = findViewById(R.id.translate_button);
        Button clearButton = findViewById(R.id.clear_button);
        ImageView lang_select = findViewById(R.id.language_selector);
        ImageView dia_select = findViewById(R.id.dialect_selector);
        loadSavedColor();
        setupRetrofit();

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
                String inputText = inputEditText.getText().toString().trim();
                if (!inputText.isEmpty()) {
                    new TranslationTask().execute();
                }
            }

        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearTranslation();
            }

            private void clearTranslation() {
                inputEditText.setText("");
                translationTextView.setText("");
            }
        });

    }
    private void setupRetrofit(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openai.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        translationService = retrofit.create(OpenAiTranslationService.class);
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

    private class TranslationTask extends AsyncTask<Void, Void, String> {
        private static final int MAX_RETRIES = 3;
        private static final int RETRY_DELAY_MS = 1000;

        @Override
        protected String doInBackground(Void... params) {
            String apiKey = "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";
            String model = "text-davinci-003";
            String inputText = inputEditText.getText().toString().trim();

            // Update the prompt to translate from Tagalog to English
            String prompt = "Translate the following Tagalog text to English: '" + inputText + "'";
            String jsonInput = "{\"prompt\": \"" + prompt + "\", \"max_tokens\": 100, \"temperature\": 0.7}";

            int retryCount = 0;

            while (retryCount < MAX_RETRIES) {
                try {
                    Call<ResponseBody> call = translationService.translateText("Bearer " + apiKey, RequestBody.create(jsonInput.getBytes()));
                    retrofit2.Response<ResponseBody> response = call.execute();

                    if (response.isSuccessful()) {
                        String result = response.body().string();
                        JSONObject jsonResponse = new JSONObject(result);
                        return jsonResponse.getString("choices");
                    } else if (response.code() == 429) {
                        // Rate limit exceeded, wait for some time and retry
                        retryCount++;
                        try {
                            Thread.sleep(RETRY_DELAY_MS); // Adjust the sleep duration as needed
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        // Handle other response codes
                        Log.i("TranslationTask", "Unexpected Response Code: " + response.code());
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i("TranslationTask", "Response result: " + result);
            runOnUiThread(() -> {
                if (result != null) {
                    translationTextView.setText(result);
                } else {
                    translationTextView.setText("Error in translation");
                }
            });
        }
    }

}


