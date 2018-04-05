package com.shirantech.sathitv.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;
import com.shirantech.sathitv.BuildConfig;
import com.shirantech.sathitv.R;
import com.shirantech.sathitv.adapter.CommentListAdapter;
import com.shirantech.sathitv.helper.CrashlyticsHelper;
import com.shirantech.sathitv.helper.PreferencesHelper;
import com.shirantech.sathitv.logger.Log;
import com.shirantech.sathitv.model.Comment;
import com.shirantech.sathitv.model.Photo;
import com.shirantech.sathitv.model.postparams.PhotoCommentParams;
import com.shirantech.sathitv.model.response.GeneralResponse;
import com.shirantech.sathitv.network.ApiConstants;
import com.shirantech.sathitv.utils.AppUtil;

import de.greenrobot.event.EventBus;

/**
 * {@link android.app.Activity} for showing comments in a photo.
 */
public class CommentActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "CommentActivity";
    private static final String URL_COMMENT_PHOTO = ApiConstants.APP_USER_URL + "comment";
    private static final String ARG_CURRENT_PHOTO = "current_photo";

    private RelativeLayout mCommentListContainer;
    private RecyclerView mCommentListView;
    private EditText mCommentEditText;
    private ImageButton mPostCommentButton;

    private CommentListAdapter mCommentListAdapter;

    private Photo currentPhoto;

    private final TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // no-op
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().trim().length() > 0) {
                mPostCommentButton.setVisibility(View.VISIBLE);
            } else {
                mPostCommentButton.setVisibility(View.GONE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            // no-op
        }
    };

    /**
     * Callback for comment posting.
     */
    private class CommentPostCallBack implements FutureCallback<GeneralResponse> {
        private final Comment postedComment;

        public CommentPostCallBack(Comment postedComment) {
            this.postedComment = postedComment;
        }

        @Override
        public void onCompleted(Exception exception, GeneralResponse serverResponse) {
            if (null == exception) {
                if (TextUtils.equals(ApiConstants.RESPONSE_STATUS_SUCCESS, serverResponse.getStatus())) {
                    Snackbar.make(mCommentListContainer, serverResponse.getMessage(),
                            Snackbar.LENGTH_LONG).show();
                    updateCommentStatus(postedComment, true, serverResponse.getMessage());
                } else if (TextUtils.equals(ApiConstants.RESPONSE_STATUS_FAILURE, serverResponse.getStatus())) {
                    Snackbar.make(mCommentListContainer, serverResponse.getMessage(),
                            Snackbar.LENGTH_LONG).show();
                    Log.d(TAG, serverResponse.getMessage());

                    // TODO : track comment posting failure using Google Analytics(or Crashlytics)
                    updateCommentStatus(postedComment, false, serverResponse.getMessage());
                } else {
                    showFailureMessage(R.string.error_problem_occurred);
                    // TODO : track connection/server error using Google Analytics(or Crashlytics)
                }
            } else {
                Log.e(TAG, exception.getLocalizedMessage(), exception);
                showFailureMessage(R.string.error_problem_occurred);
                CrashlyticsHelper.sendCaughtException(exception);
            }
        }

        private void showFailureMessage(int errorMessageId) {
            Snackbar.make(mCommentListContainer, errorMessageId, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Get the {@link Intent} to start this activity
     *
     * @param context context to initialize the Intent.
     * @param photo   {@link Photo} data to pass
     * @return the Intent to start {@link CommentActivity}
     */
    public static Intent getStartIntent(Context context, Photo photo) {
        Intent startIntent = new Intent(context, CommentActivity.class);
        Gson gson = new Gson();
        startIntent.putExtra(ARG_CURRENT_PHOTO, gson.toJson(photo, Photo.class));
        return startIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        currentPhoto = getCurrentPhoto();
        setUpToolbar();
        initViews();
    }

    /**
     * Get the {@link Photo} passed from intent.
     *
     * @return the current {@link Photo}
     */
    public Photo getCurrentPhoto() {
        final String photoJson = getIntent().getStringExtra(ARG_CURRENT_PHOTO);
        return new Gson().fromJson(photoJson, Photo.class);
    }

    private void setUpToolbar() {
        final Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            //noinspection ConstantConditions
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    private void initViews() {
        mCommentListContainer = (RelativeLayout) findViewById(R.id.comment_list_container);
        mCommentListView = (RecyclerView) findViewById(R.id.comment_listView);
        setupCommentListView();
        mCommentEditText = (EditText) findViewById(R.id.comment_editText);
        mCommentEditText.addTextChangedListener(mTextWatcher);
        mPostCommentButton = (ImageButton) findViewById(R.id.post_comment_button);
        mPostCommentButton.setOnClickListener(this);
    }

    /**
     * Set up and show comments in the list
     */
    private void setupCommentListView() {
        mCommentListView.setHasFixedSize(true);
        mCommentListView.setLayoutManager(new LinearLayoutManager(CommentActivity.this));
        mCommentListAdapter = new CommentListAdapter(currentPhoto.getPhotoCommentList());
        mCommentListView.setAdapter(mCommentListAdapter);
        if (currentPhoto.getPhotoCommentList().isEmpty()) {
            showMessageView(true);
        }
    }

    /**
     * Show/Hide the message view
     *
     * @param show <code>true</code> to show the message <code>false</code> otherwise.
     */
    private void showMessageView(final boolean show) {
        final int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        final TextView mMessageView = (TextView) findViewById(R.id.message_view);
        mMessageView.setVisibility(show ? View.VISIBLE : View.GONE);
        mMessageView.animate()
                .setDuration(shortAnimTime)
                .alpha(show ? 1 : 0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mMessageView.setVisibility(show ? View.VISIBLE : View.GONE);
                    }
                });
    }

    @Override
    public void onClick(View clickedView) {
        if (R.id.post_comment_button == clickedView.getId()) {
            postComment();
        }
    }

    /**
     * Post comment to server
     */
    private void postComment() {
        if (AppUtil.isInternetConnected(this)) {
            final String loginToken = PreferencesHelper.getLoginToken(CommentActivity.this);
            final String photoId = currentPhoto.getPhotoId();
            final String photoComment = mCommentEditText.getText().toString();

            // comment to add to the list
            final Comment postedComment = new Comment();
            postedComment.setUserName(PreferencesHelper.getUsername(CommentActivity.this));
            postedComment.setComment(photoComment);
            postedComment.setPostingComment(true);

            final Builders.Any.B builder = Ion.with(CommentActivity.this)
                    .load(URL_COMMENT_PHOTO);
            if (BuildConfig.DEBUG) {
                builder.setLogging(TAG, Log.VERBOSE);
            }
            builder.setHeader(ApiConstants.HEADER_KEY_ACCEPT, ApiConstants.HEADER_ACCEPT_VALUE)
                    .setJsonPojoBody(new PhotoCommentParams(photoId, photoComment, loginToken))
                    .as(GeneralResponse.class)
                    .setCallback(new CommentPostCallBack(postedComment));

            clearInputField();
            addCommentToList(postedComment);

        } else {
            Snackbar.make(mCommentListContainer, R.string.error_network_connection,
                    Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Clear the comment input field and hide the soft keyboard.
     */
    private void clearInputField() {
        mCommentEditText.setText("");
        AppUtil.hideSoftKeyboard(CommentActivity.this);
    }

    /**
     * Add the posted comment to the comment list to show the user with the update comment list.
     *
     * @param postedComment the posted comment
     */
    private void addCommentToList(final Comment postedComment) {
        currentPhoto.getPhotoCommentList().add(postedComment);
        mCommentListAdapter.notifyDataSetChanged();
    }

    /**
     * Update the status of the comment added by user in the list.
     * <p/>
     * hide the status if successfully posted and remove the comment from the list if failed
     * informing the user of the failure.
     *
     * @param postedComment the comment posted by the user
     * @param commentPosted <code>true</code> if comment successfully posted <code>false</code> otherwise
     * @param errorMessage  error message if comment could not be posted
     */
    private void updateCommentStatus(final Comment postedComment, final boolean commentPosted,
                                     final String errorMessage) {
        if (commentPosted) {
            currentPhoto.getPhotoCommentList()
                    .get(currentPhoto.getPhotoCommentList().indexOf(postedComment))
                    .setPostingComment(false);
            // post event using event bus to notify about the added comment to the {@link PostActivity}
            EventBus.getDefault().postSticky(currentPhoto);
        } else {
            currentPhoto.getPhotoCommentList()
                    .remove(currentPhoto.getPhotoCommentList().indexOf(postedComment));
            Snackbar.make(mCommentListContainer, errorMessage, Snackbar.LENGTH_LONG).show();
        }
        mCommentListAdapter.notifyDataSetChanged();

        showMessageView(currentPhoto.getPhotoCommentList().isEmpty());
    }

    @Override
    protected void onStop() {
        Ion.getDefault(this)
                .cancelAll(this);
        super.onStop();
    }
}
