package com.shirantech.sathitv.adapter;

import android.net.Uri;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import com.shirantech.sathitv.activity.PhotoUploadActivity;
import com.shirantech.sathitv.model.PhotoToUpload;

import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

/**
 * Test to verify {@link UploadPhotoListAdapter}'s behavior is correct.
 * <p>
 * Note that in order to scroll the list you shouldn't use {@link ViewActions#scrollTo()} as
 * {@link Espresso#onData(org.hamcrest.Matcher)} handles scrolling.</p>
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class UploadPhotoListAdapterTest {

    private static final String URI_ONE = "file:///tmp/android.jpg";
    private static final String URI_TWO = "file:///tmp/android.png";
    private static final String URI_Three = "file:///tmp/android.jpeg";

    private UploadPhotoListAdapter mUploadPhotoListAdapter;
    private List<PhotoToUpload> photoToUploadList;

    @Rule
    public ActivityTestRule<PhotoUploadActivity> mActivityRule = new ActivityTestRule<>(
            PhotoUploadActivity.class);

    @Before
    public void setUp() throws Exception {
        photoToUploadList = getTestDataList();
        mUploadPhotoListAdapter = new UploadPhotoListAdapter(photoToUploadList, null);
    }

    private List<PhotoToUpload> getTestDataList() {
        List<PhotoToUpload> photoToUploadList = new ArrayList<>();
        photoToUploadList.add(new PhotoToUpload(Uri.parse(URI_ONE), 1));
        photoToUploadList.add(new PhotoToUpload(Uri.parse(URI_TWO), 1));
        photoToUploadList.add(new PhotoToUpload(Uri.parse(URI_Three), 1));
        return photoToUploadList;
    }

    @Test
    public void headerView_Present() {
        Assert.assertThat(UploadPhotoListAdapter.TYPE_HEADER, Is.is(mUploadPhotoListAdapter.getItemViewType(0)));
    }

    @Test
    public void getPhoto() {
        Assert.assertEquals(photoToUploadList.get(0).getPhotoUri(), mUploadPhotoListAdapter.getPhotoAt(0).getPhotoUri());
    }

    @Test
    public void testGetItemCount() {
        Assert.assertThat(photoToUploadList.size() + 1, Is.is(mUploadPhotoListAdapter.getItemCount()));
    }
}