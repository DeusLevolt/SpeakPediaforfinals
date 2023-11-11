package com.example.speakpediaforfinals;

public class ThemeColorManager {
    private static ThemeColorManager instance;
    private int selectedColor;

    private ThemeColorManager() {
        // Private constructor to enforce singleton pattern
    }

    public static synchronized ThemeColorManager getInstance() {
        if (instance == null) {
            instance = new ThemeColorManager();
        }
        return instance;
    }

    public int getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(int selectedColor) {
        this.selectedColor = selectedColor;
    }
}