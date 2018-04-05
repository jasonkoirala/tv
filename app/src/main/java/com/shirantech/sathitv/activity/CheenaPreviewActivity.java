package com.shirantech.sathitv.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.shirantech.sathitv.R;
import com.shirantech.sathitv.utils.AppLog;

/*
* shows cheena image that has been uploaded by the user
*
* show image
* */
public class CheenaPreviewActivity extends AppCompatActivity {

    private static final String TAG = CheenaPreviewActivity.class.getName();
    public static final String EXTRA_IMAGE = "image";
    private SimpleDraweeView imageView;
    private String image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_cheena_preview);
        setUpToolbar();
        imageView = (SimpleDraweeView) findViewById(R.id.imageViewCheena);
        getExtras();

    }

    /*
    * getting extras value
    * */
    private void getExtras() {
        image = getIntent().getStringExtra(EXTRA_IMAGE);
        AppLog.showLog(TAG, "image :: " + image);
        showImage(image);
    }


    /*
    *
    * show image in imageview
    * */
    private void showImage(String image) {
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(
                Uri.parse(image))
                .setProgressiveRenderingEnabled(true)
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(imageView.getController())
                .build();
        imageView.setController(controller);
    }

    /*
    *
    * toolbar initialization
    * */
    private void setUpToolbar() {
        final Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            //noinspection ConstantConditions
            getSupportActionBar().setTitle(getString(R.string.title_activity_cheena_preview));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }


}
