package com.shirantech.sathitv.helper;

/**
 * Helper class for font
 */
public class FontHelper {

    private static final String FONT_DIRECTORY = "fonts/";
    public static final String FONT_TYPE_REGULAR = "Regular";
    public static final String FONT_TYPE_BOLD = "Bold";
    public static final String FONT_TYPE_ITALIC = "Italic";

    /**
     * Get the path of the font
     *
     * @param fontFileName file name of the font
     * @param fontType     type of the font. Either {@link #FONT_TYPE_REGULAR} or {@link #FONT_TYPE_BOLD}
     * @return the path of the font to use
     */
    public static String getFontPath(String fontFileName, String fontType) {
        return FONT_DIRECTORY + String.format(fontFileName, fontType);
    }
}
