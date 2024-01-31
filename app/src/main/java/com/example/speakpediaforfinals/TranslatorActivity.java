package com.example.speakpediaforfinals;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TranslatorActivity extends AppCompatActivity {
    private EditText inputEditText;
    private TextView translationTextView;

    private OpenAiTranslationService translationService;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Handler uiHandler = new Handler(Looper.getMainLooper());
    private String selectedLanguage;
    private String selectedDialect;

    private Spinner languageSpinner;
    private Spinner dialectSpinner;
    private int[] ids = {R.id.shared_background_1, R.id.shared_background_2, R.id.shared_background_3, R.id.shared_background_4, R.id.shared_background_5, R.id.shared_background_6, R.id.shared_background_7, R.id.shared_background_8, R.id.shared_background_9, R.id.shared_background_10,R.id.button1,R.id.button2,R.id.button3,R.id.translatorbluebg};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.translator_layout);

        languageSpinner = findViewById(R.id.language1_spinner);
        dialectSpinner = findViewById(R.id.language2_spinner);
        ImageView helpButton = findViewById(R.id.help_button_translate);

        initializeViews();
        setupRetrofit();
        setupListeners();
        setupLanguageSpinner();
        setupDialectSpinner();
        loadSavedColor();
        saveSelectedColor();

        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new instance of PopupWindow
                PopupWindow popupWindow = new PopupWindow(TranslatorActivity.this);

                // Set the layout parameters for the PopupWindow
                popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
                popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                popupWindow.setFocusable(true);

                // Inflate the layout for the PopupWindow
                View popupView = LayoutInflater.from(TranslatorActivity.this).inflate(R.layout.popup_layout, null);

                // Set the image resource for the ImageView in the popup layout
                ImageView imageView = popupView.findViewById(R.id.popupImageView);
                imageView.setImageResource(R.drawable.help_translator);

                // Set the content view of the PopupWindow
                popupWindow.setContentView(popupView);

                // Show the PopupWindow at the center of the screen
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
            }
        });


        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedLanguage = (String) parentView.getItemAtPosition(position);
                updateLanguageTextView(selectedLanguage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here for now
            }
        });

        dialectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemVIew, int position, long id) {
                String selectedLanguage2 = (String) parentView.getItemAtPosition(position);
                updateLanguage2TextView(selectedLanguage2);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });
    }
    private void updateLanguage2TextView(String selectedLanguage2) {
        TextView language2TextView = findViewById(R.id.dialect);
        language2TextView.setText(selectedLanguage2);
    }

    private void updateLanguageTextView(String selectedLanguage) {
        TextView languageTextView = findViewById(R.id.language);
        languageTextView.setText(selectedLanguage);
    }

    private void setupLanguageSpinner() {
        // Define your language choices
        String[] languages = {"English", "Tagalog","Cebuano", "Hiligaynon", "Ilokano", "Kapampangan", "Chavacano"};

        // Create an ArrayAdapter using a simple spinner layout and your choices
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, languages);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        languageSpinner.setAdapter(adapter);
    }

    private void setupDialectSpinner() {
        // Define your dialect choices
        String[] dialects = {"Cebuano", "Hiligaynon", "Ilokano", "Kapampangan", "Chavacano","English", "Tagalog"};

        // Create an ArrayAdapter using a simple spinner layout and your choices
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dialects);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        dialectSpinner.setAdapter(adapter);
    }

    private void initializeViews() {
        inputEditText = findViewById(R.id.input_edit_text);
        translationTextView = findViewById(R.id.translate_textView);
        // Initialize other views if necessary

        inputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do nothing before text changes
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do nothing on text changing
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Translate text after it has changed
                translateText();
            }
        });
    }

    private void setupRetrofit() {
        // Create a logging interceptor
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Set custom timeout values (adjust as needed)
        int timeoutSeconds = 30;
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .connectTimeout(timeoutSeconds, TimeUnit.SECONDS)
                .readTimeout(timeoutSeconds, TimeUnit.SECONDS)
                .writeTimeout(timeoutSeconds, TimeUnit.SECONDS)
                .addInterceptor(logging);

        // Build Retrofit with the OkHttpClient
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openai.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        translationService = retrofit.create(OpenAiTranslationService.class);
    }



    private void setupListeners() {
        findViewById(R.id.back_button_translator).setOnClickListener(view -> {
            startActivity(new Intent(TranslatorActivity.this, MainActivity.class));
            overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
        });

        findViewById(R.id.translate_button).setOnClickListener(v -> translateText());

        findViewById(R.id.clear_button).setOnClickListener(v -> clearTranslation());
    }

    private void translateText() {
        String inputText = inputEditText.getText().toString().trim();
        if (!inputText.isEmpty()) {
            selectedLanguage = (String) languageSpinner.getSelectedItem();
            selectedDialect = (String) dialectSpinner.getSelectedItem();
            translateInBackground(inputText);
        }
    }


    private void translateInBackground(String inputText) {
        CompletableFuture.supplyAsync(() -> {
            String apiKey = "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"; // Replace with your actual API key
            String prompt = "Translate the following " + selectedLanguage + " text to " + selectedDialect + ": '" + inputText + "'";
            String jsonInput = "{\"prompt\": \"" + prompt + "\", \"max_tokens\": 50, \"temperature\": 0.7}";
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");


            int retryCount = 0;
            while (retryCount < 3) {
                try {
                    Call<ResponseBody> call = translationService.translateText("Bearer " + apiKey, RequestBody.create(jsonInput, JSON));
                    retrofit2.Response<ResponseBody> response = call.execute();

                    if (response.isSuccessful() && response.body() != null) {
                        String result = response.body().string();
                        JSONObject jsonResponse = new JSONObject(result);
                        return jsonResponse.getString("choices");
                    } else if (response.code() == 429) {
                        retryCount++;
                        Thread.sleep(1000);
                    } else {
                        Log.e("TranslationTask", "Unexpected Response Code: " + response.code());
                        Log.e("Translator","API req: "+call.request().toString());
                        return "Error: Unexpected response code " + response.code();
                    }
                } catch (Exception e) {
                    Log.e("TranslatorActivity", "Error in translation task", e);
                    return "Error: " + e.getMessage();
                }
            }
            return "Error: Failed after retries";
        }, executorService).thenAcceptAsync(result -> {
            // Update UI on the main thread
            uiHandler.post(() -> updateUI(result));
        }, executorService).exceptionally(e -> {
            // Handle exceptions here
            Log.e("TranslatorActivity", "Error in translation task", e);
            uiHandler.post(() -> updateUI("Error: " + e.getMessage()));
            return null;
        });
    }

    private void updateUI(String result) {
        if (result != null && !result.startsWith("Error")) {
            try {
                // Parse the JSON response
                JSONArray choicesArray = new JSONArray(result);
                if (choicesArray.length() > 0) {
                    JSONObject choiceObject = choicesArray.getJSONObject(0);
                    String translatedText = choiceObject.optString("text", "Error: Text not found in response");
                    translationTextView.setText(translatedText.trim());
                } else {
                    translationTextView.setText("Error: No choices in response");
                }
            } catch (JSONException e) {
                Log.e("TranslatorActivity", "Error parsing JSON response", e);
                translationTextView.setText("Error: " + e.getMessage());
            }
        } else {
            translationTextView.setText(result != null ? result : "Error in translation");
        }
    }


    private void clearTranslation() {
        inputEditText.setText("");
        translationTextView.setText("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown(); // Shut down the executor service
    }

    private void applyColorToImageView() {
        for (int id : ids) {
            RelativeLayout imageView = findViewById(id);
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
