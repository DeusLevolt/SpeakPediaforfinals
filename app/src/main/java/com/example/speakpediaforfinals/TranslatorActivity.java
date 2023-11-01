package com.example.speakpediaforfinals;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class TranslatorActivity extends AppCompatActivity {
    private EditText inputEditText;
    private TextView translationTextView;
    private ImageView languageSelector;
    private PopupWindow languagePopup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.translator_layout);

        inputEditText = findViewById(R.id.input_edit_text);
        translationTextView = findViewById(R.id.translate_textView);
        Button translateButton = findViewById(R.id.translate_button);
        Button clearButton = findViewById(R.id.clear_button);

        translateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translateText();
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearTranslation();
            }
        });

        languageSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLanguageSelectorPopup(view);
            }
        });
    }

    public void showLanguageSelectorPopup(View v){
        View popupView = getLayoutInflater().inflate(R.layout.translator_layout,null);

        languagePopup = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,true);

        languagePopup.showAsDropDown(v, 0, 0);
    }

    private void translateText() {
        // Implement your translation logic here.
        // You can use an API or any translation service to perform the translation.

        // For example, you can set the translation result to the TextView:
        String inputText = inputEditText.getText().toString();
        String translatedText = performTranslation(inputText); // You need to implement this method.
        translationTextView.setText(translatedText);
    }

    private void clearTranslation() {
        inputEditText.setText("");
        translationTextView.setText("");
    }

    private String performTranslation(String inputText) {
        // Implement your translation logic here, such as using an API.
        // Return the translated text.
        // You can use services like Google Translate API or others for translation.
        // This example assumes a simple direct translation, but you may need to integrate an actual translation service.
        return "Translated: " + inputText;
    }


}

