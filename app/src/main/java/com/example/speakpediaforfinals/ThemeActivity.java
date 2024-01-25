package com.example.speakpediaforfinals;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class ThemeActivity extends Activity {

    //ids of the imageviews you want to change color//
    private int[] ids = {R.id.shared_background_1, R.id.shared_background_2, R.id.shared_background_3, R.id.shared_background_4, R.id.shared_background_5, R.id.shared_background_6, R.id.shared_background_7, R.id.shared_background_8, R.id.shared_background_9, R.id.shared_background_10, R.id.bgbluebutton};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.theme_layout);

        // Load the saved color on activity creation
        loadSavedColor();

        ImageView back = findViewById(R.id.back_button_theme);

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
