package com.example.speakpediaforfinals;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.translation.TranslationRequest;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class TranslatorActivity extends AppCompatActivity {
    private EditText inputEditText;
    private TextView translationTextView;
    public ImageView languageSelector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.translator_layout);

        inputEditText = findViewById(R.id.input_edit_text);
        translationTextView = findViewById(R.id.translate_textView);
        Button back = findViewById(R.id.back_button_translator);
        Button translateButton = findViewById(R.id.translate_button);
        Button clearButton = findViewById(R.id.clear_button);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back_to_main = new Intent(TranslatorActivity.this, MainActivity.class);
                startActivity(back_to_main);

            }
        });

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

        PopupWindow languagePopup = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

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

        return "Translation failed.";
    }
}

