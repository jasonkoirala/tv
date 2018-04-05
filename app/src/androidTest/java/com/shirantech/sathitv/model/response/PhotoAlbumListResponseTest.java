package com.shirantech.sathitv.model.response;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.MediumTest;

import com.google.gson.Gson;
import com.shirantech.sathitv.model.Photo;
import com.shirantech.sathitv.model.PhotoAlbum;

import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Assert;
import org.hamcrest.Matchers;

/**
 * Unit test for {@link PhotoAlbumListResponse}
 */
@RunWith(AndroidJUnit4.class)
@MediumTest
public class PhotoAlbumListResponseTest {

    private PhotoAlbumListResponse photoAlbumListResponse;

    @Before
    public void setUp() throws Exception {
        photoAlbumListResponse = new Gson().fromJson(
                SampleTestData.SAMPLE_POST_LIST_SUCCESS, PhotoAlbumListResponse.class);
    }

    @Test
    public void testHasPosts() throws Exception {
        Assert.assertNotNull(photoAlbumListResponse);
        Assert.assertFalse(photoAlbumListResponse.getPhotoAlbumList().isEmpty());
        Assert.assertNotNull(photoAlbumListResponse.getOwnPhotoAlbumList());
    }

    @Test
    public void testImage_HasRating() throws Exception {
        // take any image from one post and check
        Photo photo = photoAlbumListResponse.getPhotoAlbumList().get(0).getPhotoList().get(0);
        Assert.assertNotNull(photo);
        Assert.assertNotNull(photo.getPhotoRating());
        Assert.assertThat(photo.getPhotoRating(), Is.is(Matchers.greaterThanOrEqualTo(0F)));
    }

    @Test
    public void testPost_HasDescription() throws Exception {
        // take any image from one post and check
        PhotoAlbum photoAlbum = photoAlbumListResponse.getPhotoAlbumList().get(1);
        Assert.assertNotNull(photoAlbum);
        Assert.assertNotNull(photoAlbum.getDescription());
    }

}