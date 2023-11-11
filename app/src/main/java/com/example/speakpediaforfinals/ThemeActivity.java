package com.example.speakpediaforfinals;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

public class ThemeActivity extends Activity {

    private int[] ids = {R.id.shared_background_1, R.id.shared_background_2, R.id.shared_background_3, R.id.shared_background_4, R.id.shared_background_5, R.id.shared_background_6, R.id.shared_background_7, R.id.shared_background_8, R.id.shared_background_9, R.id.shared_background_10, R.id.bgbluebutton, R.id.fontsbluebutton};
    private int[] headerFonts = {R.id.heading1, R.id.heading2, R.id.heading3, R.id.heading4, R.id.heading5, R.id.heading6, R.id.heading7, R.id.heading8, R.id.heading9, R.id.heading10, R.id.heading11, R.id.heading12};
    private static final String FONT_PATH_A = "res/font/baloo.ttf";
    private String savedFontPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.theme_layout);

        // Load the saved color on activity creation
        loadSavedColor();
        savedFontPath = loadSavedFont();

        ImageView back = findViewById(R.id.back_button_theme);
        TextView fonts = findViewById(R.id.fonts);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back_to_settings = new Intent(ThemeActivity.this, SettingsActivity.class);
                startActivity(back_to_settings);
                overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
                saveSelectedColor();  // Save the selected color before navigating back
            }
        });

        findViewById(R.id.background).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showColorSelectionMenu(view);
            }
        });

        fonts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFontsSelectionMenu(view);
            }
        });
    }

    private void showFontsSelectionMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.font_menu, popupMenu.getMenu());

        final Context context = view.getContext();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();

                // Handle the selected font here
                String selectedFontPath = null;

                if (itemId == R.id.font_a) {
                    selectedFontPath = String.valueOf(R.font.baloo);
                } else if (itemId == R.id.font_b) {
                    selectedFontPath = String.valueOf(R.font.freeserif);
                } else if (itemId == R.id.font_c) {
                    selectedFontPath = String.valueOf(R.font.ztchablis);
                }
                // Add more conditions for other fonts

                if (selectedFontPath != null) {
                    Log.d("FontSelection", "Selected Font Path: " + selectedFontPath);

                    int fontResourceId = getFontResourceId(selectedFontPath);
                    Log.d("FontSelection", "Corresponding Resource ID: " + fontResourceId);

                    saveSelectedFont(selectedFontPath);
                    Typeface selectedTypeface = getTypeface(selectedFontPath);
                    if (selectedTypeface != null){
                        applyFontToTextViews(headerFonts, selectedTypeface);
                        Log.d("FontSelection", "Font applied successfully!");
                    } else {
                        Log.e("FontSelection", "Failed to apply font. Typeface is null.");
                    }
                } else {
                    Log.e("FontSelection", "Selected font path is null.");
                }

                return true;
            }
        });

        popupMenu.show();
    }

    private void saveSelectedFont(String fontIdentifier) {
        SharedPreferences preferences = getSharedPreferences("font_preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("selected_font", fontIdentifier);
        editor.apply();
    }

    private String loadSavedFont() {
        SharedPreferences preferences = getSharedPreferences("font_preferences", MODE_PRIVATE);
        return preferences.getString("selected_font", FONT_PATH_A);
    }

    private Typeface getTypeface(String fontPath) {
        return ResourcesCompat.getFont(this, getFontResourceId(fontPath));
    }

    private int getFontResourceId(String fontPath) {
        // Convert the fontPath to the corresponding resource ID
        switch (fontPath) {
            case "res/font/baloo.ttf":
                return R.font.baloo;
            case "res/font/freeserif.ttf":
                return R.font.freeserif;
            case "res/font/ztchablis.ttf":
                return R.font.ztchablis;
            // Add more cases for other fonts as needed
            default:
                return R.font.baloo; // Default to baloo if the fontPath is not recognized
        }
    }


    private void applyFontToTextViews(int[] textViewsIds, Typeface typeface) {
        for (int textViewId : textViewsIds) {
            TextView textView = findViewById(textViewId);
            if (textView != null) {
                // Retrieve the current style attributes
                int[] attrs = {android.R.attr.fontFamily, android.R.attr.textColor, android.R.attr.textSize};
                TypedArray ta = obtainStyledAttributes(R.styleable.FontsForAllHeading, attrs);
                try {
                    // Use the correct styleable indices
                    textView.setTextColor(ta.getColor(R.styleable.FontsForAllHeading_android_textColor, ContextCompat.getColor(this, R.color.black)));
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ta.getDimensionPixelSize(R.styleable.FontsForAllHeading_android_textSize, getResources().getDimensionPixelSize(R.dimen.font_size)));
                } finally {
                    ta.recycle(); // Don't forget to recycle the TypedArray
                }
            }
        }
    }

    private TypedArray obtainStyledAttributes(int[] fontsForAllHeading, int[] attrs) {
        return getTheme().obtainStyledAttributes(fontsForAllHeading);
    }


    private void showColorSelectionMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.color_menu, popupMenu.getMenu());

        final Context context = view.getContext();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.color_orange) {
                    ThemeColorManager.getInstance().setSelectedColor(ContextCompat.getColor(context, R.color.orange));
                } else if (itemId == R.id.color_green) {
                    ThemeColorManager.getInstance().setSelectedColor(ContextCompat.getColor(context, R.color.lightGreen));
                } else if (itemId == R.id.color_yellow) {
                    ThemeColorManager.getInstance().setSelectedColor(ContextCompat.getColor(context, R.color.Yellow));
                } else if (itemId == R.id.color_blue) {
                    ThemeColorManager.getInstance().setSelectedColor(ContextCompat.getColor(context, R.color.very_light_blue));
                }

                applyColorToImageViews();
                saveSelectedColor();  // Save the selected color after changing
                return true;
            }
        });

        popupMenu.show();
    }

    private void applyColorToImageViews() {
        for (int id : ids) {
            ImageView imageView = findViewById(id);
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

    private void saveSelectedColor() {
        int selectedColor = ThemeColorManager.getInstance().getSelectedColor();
        SharedPreferences preferences = getSharedPreferences("theme_preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("selected_color", selectedColor);
        editor.apply();
    }

    private void loadSavedColor() {
        SharedPreferences preferences = getSharedPreferences("theme_preferences", MODE_PRIVATE);
        int savedColor = preferences.getInt("selected_color", -1);
        ThemeColorManager.getInstance().setSelectedColor(savedColor);
        applyColorToImageViews();  // Apply the saved color on activity creation
    }
}
