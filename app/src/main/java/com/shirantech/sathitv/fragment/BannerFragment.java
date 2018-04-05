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
import com.shirantech.sathitv.model.Banner;


/**
 * class  for load sliding images
 **/
public class BannerFragment extends Fragment {
    private static final String ARG_BANNER_IMAGE = "image";

    private SimpleDraweeView bannerImage;

    public static BannerFragment newInstance(Banner banner) {
        Bundle args = new Bundle();
        args.putString(ARG_BANNER_IMAGE, new Gson().toJson(banner, Banner.class));
        BannerFragment fragment = new BannerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_banner, container, false);
        bannerImage = (SimpleDraweeView) view.findViewById(R.id.imageViewBanner);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (null != getArguments()) {
            final Banner banner = getBannerImagesFromArg();
            showPhoto(banner);
        }
    }

    private Banner getBannerImagesFromArg() {
        final String bannerJson = getArguments().getString(ARG_BANNER_IMAGE);
        return new Gson().fromJson(bannerJson, Banner.class);
    }

    /**
     * Show the given photo
     *
     * @param banner {@link Banner} to show
     */
    public void showPhoto(final Banner banner) {
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(
                Uri.parse(banner.getImage()))
                .setProgressiveRenderingEnabled(true)
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(bannerImage.getController())
                .build();
        bannerImage.setController(controller);
    }
}
