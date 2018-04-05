package com.shirantech.sathitv.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.gson.Gson;
import com.shirantech.sathitv.R;
import com.shirantech.sathitv.model.Photo;

/**
 * Fragment for showing fullscreen image
 */
public class FullScreenPhotoFragment extends Fragment {
    private static final String ARG_PHOTO_DETAIL = "photo_detail";

    private SimpleDraweeView mPhotoView;

    public static FullScreenPhotoFragment newInstance(Photo photo) {
        Bundle args = new Bundle();
        args.putString(ARG_PHOTO_DETAIL, new Gson().toJson(photo, Photo.class));
        FullScreenPhotoFragment fragment = new FullScreenPhotoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_full_screen_photo, container, false);
        mPhotoView = (SimpleDraweeView) view.findViewById(R.id.photo_view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (null != getArguments()) {
            final Photo photo = getPhotoFromArg();
            showPhoto(photo);
        }
    }

    private Photo getPhotoFromArg() {
        final String photoJson = getArguments().getString(ARG_PHOTO_DETAIL);
        return new Gson().fromJson(photoJson, Photo.class);
    }

    /**
     * Show the given photo
     *
     * @param photo {@link Photo} to show
     */
    public void showPhoto(final Photo photo) {
        mPhotoView.setAspectRatio(photo.getAspectRatio());
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(
                Uri.parse(photo.getImagePath()))
                .setProgressiveRenderingEnabled(true)
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(mPhotoView.getController())
                .build();
        mPhotoView.setController(controller);
    }
}
