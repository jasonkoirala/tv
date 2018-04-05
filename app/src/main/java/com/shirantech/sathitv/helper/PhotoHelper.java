package com.shirantech.sathitv.helper;

import android.graphics.BitmapFactory;
import android.net.Uri;

/**
 * Helper class for manipulating photos.
 */
public class PhotoHelper {

    /**
     * Gives the aspect ratio of image with given Uri.
     *
     * @param imageUri Uri of image
     * @return aspect ratio of image
     */
    public static float getAspectRatioOf(final Uri imageUri) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageUri.getPath(), options);
        return (float) options.outWidth / options.outHeight;
    }
}
