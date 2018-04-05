package com.shirantech.sathitv.helper;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Helper class for managing files.
 */
public class FileHelper {
    private static final String IMAGE_PREFIX = "img_";
    private static final String IMAGE_POSTFIX = ".jpg";

    /**
     * Get the image directory to store photo taken in sathi tv app
     *
     * @return the image directory to save photo to
     */
    public static File getImageDirectory() {
        File filePath = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), File.separator + "SathiTv");

        if (!filePath.exists()) {
            if (!(filePath.mkdirs() || filePath.isDirectory())) {
                return null;
            }
        }
        return filePath;
    }

    /**
     * Get unique filename for photo.
     *
     * @return the unique filename for the photo
     */
    public static String getUniqueImageName() {
        try {
            return File.createTempFile(IMAGE_PREFIX, IMAGE_POSTFIX).getName();
        } catch (IOException e) {
            return IMAGE_PREFIX + System.currentTimeMillis() + IMAGE_POSTFIX;
        }
    }

    /**
     * Get the proper uri(to get the image) from the given uri.<p>
     * Used to get the proper uri of the image selected from gallery
     *
     * @param context  {@link Context} to access system resources required.
     * @param imageUri {@link Uri} of the image(from gallery)
     * @return the proper {@link Uri} to get image from.
     */
    @Nullable
    public static Uri getImageUriWithAuthority(final Context context, final Uri imageUri) {
        final String imagePath = attemptGetPathFromUri(context, imageUri);
        if (null != imagePath) {
            return Uri.parse(imagePath);
        } else { // it happens while selecting photo from external apps like Photos, Picasso etc
            InputStream is = null;
            if (imageUri.getAuthority() != null) {
                try {
                    is = context.getContentResolver().openInputStream(imageUri);
                    Bitmap image = BitmapFactory.decodeStream(is);
                    return writeTempImageAndGetUri(context, image);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
    }

    /**
     * Attempt to get the path of the image with the provided {@link Uri}.
     *
     * @param context  {@link Context} to access system resources required.
     * @param imageUri {@link Uri} of the image(from gallery)
     * @return the path of the image if found <code>null</code> otherwise
     */
    private static String attemptGetPathFromUri(Context context, Uri imageUri) {
        Cursor cursor = null;
        try {
            String[] projection = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(imageUri, projection, null, null, null);
            if (null != cursor) {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                if (columnIndex == -1) {
                    return null;
                }
                return cursor.getString(columnIndex);
            } else {
                return null;
            }
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
    }

    /**
     * Write the given bitmap to temporary file and return its Uri.
     *
     * @param context {@link Context} to access system resources required.
     * @param image   {@link Bitmap} whose {@link Uri} is to be obtained.
     * @return the generated {@link Uri} of the provided {@link Bitmap}.
     */
    private static Uri writeTempImageAndGetUri(Context context, Bitmap image) {
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), image,
                getUniqueImageName(), null);
        return Uri.parse(path);
    }
}
