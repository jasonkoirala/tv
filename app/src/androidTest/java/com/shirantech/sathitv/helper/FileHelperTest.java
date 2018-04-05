package com.shirantech.sathitv.helper;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import junit.framework.Assert;

import static org.junit.Assert.assertNotEquals;

/**
 * Unit tests for the {@link FileHelper}.
 */
@SmallTest
@RunWith(AndroidJUnit4.class)
public class FileHelperTest {

    @Test
    public void testGetImageDirectory() throws Exception {
        File imageDirectory = FileHelper.getImageDirectory();
        Assert.assertNotNull(imageDirectory);
        Assert.assertTrue(imageDirectory.isDirectory());
        Assert.assertTrue(imageDirectory.exists());
    }

    @Test
    public void testGetUniqueImageName() throws Exception {
        String imageName1 = FileHelper.getUniqueImageName();
        String imageName2 = FileHelper.getUniqueImageName();
        assertNotEquals(imageName1, imageName2);
    }
}