package com.shirantech.sathitv.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.shirantech.sathitv.R;
import com.shirantech.sathitv.activity.ShowAllPhotosActivity;
import com.shirantech.sathitv.model.ModelProfile;
import com.shirantech.sathitv.utils.AppLog;
import com.shirantech.sathitv.utils.AppUtil;
import com.shirantech.sathitv.utils.ServerResponse;
import com.shirantech.sathitv.widget.CustomFontTextView;
import com.shirantech.sathitv.widget.CustomProgressView;

public class ShowAllPhotosFragment extends Fragment {
    private static final String TAG = ShowAllPhotosFragment.class.getName();
    private static final String IMAGE = "image";
    private String image;
    private SimpleDraweeView imageView;

    private CustomProgressView progressView;

    public ShowAllPhotosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    // TODO: Rename and change types and number of parameters
    public static ShowAllPhotosFragment newInstance(String image) {
        ShowAllPhotosFragment fragment = new ShowAllPhotosFragment();
        Bundle args = new Bundle();
        args.putString(IMAGE, image);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            image = getArguments().getString(IMAGE);

            AppLog.showLog(TAG, "image " + image);
        }

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//       // TODO: progress bar
//        showProgress(true);
        showPhoto(image);
    }

    public void showPhoto(String image) {

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_show_all_photos, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        imageView = (SimpleDraweeView) view.findViewById(R.id.imageViewModelAllPhotos);
        progressView = (CustomProgressView) view.findViewById(R.id.progressViewModelAllPhotos);

    }
    public void showProgress(final boolean showProgress) {
        progressView.setVisibility(showProgress ? View.VISIBLE : View.GONE);
//        mProgressView.setProgressMessage(R.string.message_getting_reply);
        progressView.setProgressMessage(R.string.message_getting_model_list);
    }

    private void showMessageToUser(int messageId) {
        showMessageToUser(getString(messageId));
    }
    private void showMessageToUser(String message) {
        progressView.setProgressMessage(message);
        progressView.hideProgressBar();
        if (ServerResponse.isTokenInvalid(message)) {
            AppUtil.showInvalidTokenSnackBar(imageView, message, getActivity());
        } else {
            Snackbar.make(imageView, message, Snackbar.LENGTH_SHORT).show();
        }
    }
}
