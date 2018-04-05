package com.shirantech.sathitv.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.google.gson.Gson;
import com.shirantech.sathitv.R;
import com.shirantech.sathitv.adapter.PhotoPagerAdapter;
import com.shirantech.sathitv.model.PhotoAlbum;

/**
 * {@link android.app.Activity} for showing fullscreen photos.
 */
public class FullScreenPhotoActivity extends BaseActivity {
    private static final String ARG_POST_DETAILS = "post_detail";
    private static final String ARG_CURRENT_IMAGE_POSITION = "current_image_position";

    /**
     * Get the {@link Intent} to start this activity
     *
     * @param context context to initialize the Intent.
     * @param photoAlbum    {@link PhotoAlbum} data to pass
     * @return the Intent to start {@link FullScreenPhotoActivity}
     */
    public static Intent getStartIntent(Context context, PhotoAlbum photoAlbum, int currentImagePosition) {
        Intent startIntent = new Intent(context, FullScreenPhotoActivity.class);
        Gson gson = new Gson();
        startIntent.putExtra(ARG_POST_DETAILS, gson.toJson(photoAlbum, PhotoAlbum.class));
        startIntent.putExtra(ARG_CURRENT_IMAGE_POSITION, currentImagePosition);
        return startIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_photo);

        final PhotoAlbum mPhotoAlbumData = getPostData(getIntent());
        final ViewPager mPhotoPager = (ViewPager) findViewById(R.id.photo_pager);
        mPhotoPager.setAdapter(new PhotoPagerAdapter(getSupportFragmentManager(), mPhotoAlbumData.getPhotoList()));
        mPhotoPager.setCurrentItem(getCurrentImagePosition(getIntent()));
    }


    private int getCurrentImagePosition(Intent intent) {
        return intent.getIntExtra(ARG_CURRENT_IMAGE_POSITION, 0);
    }

    /**
     * Get the {@link PhotoAlbum} data.
     *
     * @param intent {@link Intent} to get post data from
     * @return {@link PhotoAlbum} data to show images.
     */
    private PhotoAlbum getPostData(Intent intent) {
        final String postJson = intent.getStringExtra(ARG_POST_DETAILS);
        return new Gson().fromJson(postJson, PhotoAlbum.class);
    }


}
