package com.shirantech.sathitv.model;

/**
 * The available language for the user to select with the corresponding font names.
 */
public enum Language {

    ENGLISH("Roboto-%s.ttf"),
    NEPALI("NotoSansDevanagari-%s.ttf");

    private final String fontName;

    Language(String fontName) {
        this.fontName = fontName;
    }

    public String getFontName() {
        return fontName;
    }
}
