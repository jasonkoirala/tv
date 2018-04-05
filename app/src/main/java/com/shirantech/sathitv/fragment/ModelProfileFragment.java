package com.shirantech.sathitv.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.gson.Gson;
import com.shirantech.sathitv.R;
import com.shirantech.sathitv.activity.ChatActivity;
import com.shirantech.sathitv.activity.ShowAllPhotosActivity;
import com.shirantech.sathitv.model.ModelProfile;
import com.shirantech.sathitv.utils.AppLog;


public class ModelProfileFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = ModelProfileFragment.class.getName();
    private static final String ARG_MODEL_PROFILE_DETAIL = "model_profile_detail";
    private TextView textViewDescription, textViewStatus;
    private Button buttonViewAllPhotos, buttonChat;
    private RatingBar ratingBar;
    private String name, image;
    private SimpleDraweeView imageView;
    private int userId;

    public static ModelProfileFragment newInstance(ModelProfile modelProfile) {
        Bundle args = new Bundle();
        args.putString(ARG_MODEL_PROFILE_DETAIL, new Gson().toJson(modelProfile, ModelProfile.class));
        ModelProfileFragment fragment = new ModelProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    private ModelProfile getModelProfileFromArg() {
        final String modelProfileJson = getArguments().getString(ARG_MODEL_PROFILE_DETAIL);
        return new Gson().fromJson(modelProfileJson, ModelProfile.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_model_profile, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        textViewDescription = (TextView) view.findViewById(R.id.textViewDescriptionModelProfile);
        textViewStatus = (TextView) view.findViewById(R.id.textViewProfileStatus);
        ratingBar = (RatingBar) view.findViewById(R.id.ratingBarProfile);
        imageView = (SimpleDraweeView) view.findViewById(R.id.imageViewModelProfile);
        buttonChat = (Button) view.findViewById(R.id.buttonProfileChat);
        buttonViewAllPhotos = (Button) view.findViewById(R.id.buttonModelProfileViewAllPhotos);
        buttonViewAllPhotos.setOnClickListener(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            final ModelProfile modelProfileJson = getModelProfileFromArg();
            setModelProfile(modelProfileJson);
            AppLog.showLog(TAG, "modelProfilejson ::::  " + modelProfileJson.getUserId());
        }
    }

    private void setModelProfile(ModelProfile modelProfileJson) {
        textViewDescription.setText(modelProfileJson.getDescription());
        String loginStatus = modelProfileJson.getLoginStatus();
        textViewStatus.setText(loginStatus);
        if (loginStatus.equalsIgnoreCase("offline")) {
            textViewStatus.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_offline_status_textview));
        } else {
            textViewStatus.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_online_status_textview));
        }
        ratingBar.setRating(modelProfileJson.getRating());
        userId = modelProfileJson.getUserId();
        name = modelProfileJson.getName();
        image  = modelProfileJson.getImage();
        showPhoto(modelProfileJson);
        AppLog.showLog(TAG, "image  ::::  " + modelProfileJson.getImage());

        buttonChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra(ChatActivity.EXTRA_ID, userId);
                intent.putExtra(ChatActivity.EXTRA_NAME, name);
                intent.putExtra(ChatActivity.EXTRA_IMAGE, image);
                v.getContext().startActivity(intent);
            }
        });
    }

    public void showPhoto(final ModelProfile photo) {
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(
                Uri.parse(photo.getImage()))
                .setProgressiveRenderingEnabled(true)
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(imageView.getController())
                .build();
        imageView.setController(controller);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), ShowAllPhotosActivity.class);
        intent.putExtra(ShowAllPhotosActivity.EXTRA_PHOTOS, userId);
        startActivity(intent);

    }
}
