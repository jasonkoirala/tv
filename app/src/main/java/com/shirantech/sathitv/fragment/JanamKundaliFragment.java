package com.shirantech.sathitv.fragment;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;
import com.shirantech.sathitv.BuildConfig;
import com.shirantech.sathitv.R;
import com.shirantech.sathitv.activity.AstrologyActivity;
import com.shirantech.sathitv.activity.JanamKundaliReplyActivity;
import com.shirantech.sathitv.activity.YoutubePlayerActivity;
import com.shirantech.sathitv.helper.CrashlyticsHelper;
import com.shirantech.sathitv.helper.FileHelper;
import com.shirantech.sathitv.helper.PreferencesHelper;
import com.shirantech.sathitv.logger.Log;
import com.shirantech.sathitv.model.response.BannerResponse;
import com.shirantech.sathitv.model.response.GeneralResponse;
import com.shirantech.sathitv.model.response.HelpResponse;
import com.shirantech.sathitv.network.ApiConstants;
import com.shirantech.sathitv.utils.AppLog;
import com.shirantech.sathitv.utils.AppUtil;
import com.shirantech.sathitv.utils.ServerResponse;
import com.shirantech.sathitv.widget.CustomProgressView;
import com.shirantech.sathitv.widget.TimePicker;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * A simple {@link AstrologyActivity} subclass.
 * Use the {@link JanamKundaliFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JanamKundaliFragment extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialogFragment.OnTimeSetListener {

    private static final String TAG = JanamKundaliFragment.class.getName();
    private static final String URL_REQUEST_JANAM_KUNDALI = ApiConstants.CHANNEL_URL + "rashifal";
    private static final String URL_REQUEST_HELP = ApiConstants.APP_USER_URL + "getJanamkundaliHelpContent";
    public static final int SELECT_IMAGE = 0;
    private LinearLayout mainLayout;
    private EditText editTextName, editTextDob, editTextTime, editTextPlace, editTextQuestion;
    private Button buttonSubmit, buttonViewReply, buttonUpload, buttonHelp;
    private RadioGroup genderRadioGroup;
    private RadioButton radioButton;
    private ImageView imageView;
    private String url;
    private CustomProgressView mProgressView;
    private File imageFile = null;
    Uri imageUri;
    private final Calendar currentDateTime = Calendar.getInstance();
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
    private final SimpleDateFormat submitDateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private final SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

    /**
     * Callback for Janam kundali request
     */
    private FutureCallback<GeneralResponse> mRequestCallback = new FutureCallback<GeneralResponse>() {
        @Override
        public void onCompleted(Exception exception, GeneralResponse serverResponse) {
            if (null == exception) {
                if (TextUtils.equals(ApiConstants.RESPONSE_STATUS_SUCCESS, serverResponse.getStatus())) {
                    showSuccessMessage(serverResponse.getMessage());
                    AppLog.showLog(TAG, "chino msg ::" + serverResponse.getChinoMessage());
                } else if (TextUtils.equals(ApiConstants.RESPONSE_STATUS_FAILURE, serverResponse.getStatus())) {
                    AppLog.showLog(TAG, serverResponse.getMessage());

                    showFailureMessage(serverResponse.getMessage());

                    // TODO : track janam kundali request failure using Google Analytics(or Crashlytics)
                } else {
                    showFailureMessage(R.string.error_problem_occurred);
                    // TODO : track connection/server error using Google Analytics(or Crashlytics)
                }
            } else {
                AppLog.showLog(TAG, "exception" + exception);
                showFailureMessage(R.string.error_problem_occurred);
                CrashlyticsHelper.sendCaughtException(exception);
            }
        }
    };

    /*
    * show sucess message after form submitted
    * */
    private void showSuccessMessage(String message) {
        mProgressView.setProgressMessage(R.string.message_submitted);
        Snackbar.make(mainLayout, message, Snackbar.LENGTH_LONG)
                .setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);
                        getActivity().finish();
                    }
                })
                .show();
    }

    /*
    * show failure message and handle invalid token
    * */
    private void showFailureMessage(String message) {
        showProgress(false);
        if (ServerResponse.isTokenInvalid(message)) {
            AppUtil.showInvalidTokenSnackBar(mainLayout, message, getActivity());
        } else {
            Snackbar.make(mainLayout, message, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void showFailureMessage(int messageId) {
        showFailureMessage(getString(messageId));
    }


    private TextWatcher mInputWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // no-op
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!TextUtils.isEmpty(editTextName.getText().toString().trim()) &&
                    !TextUtils.isEmpty(editTextPlace.getText().toString().trim()) &&
                    !TextUtils.isEmpty(editTextQuestion.getText().toString().trim())) {
//                showSubmitMenu(true);
            } else {
//                showSubmitMenu(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            // no-op
        }
    };

    public JanamKundaliFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */

    public static JanamKundaliFragment newInstance() {
        JanamKundaliFragment fragment = new JanamKundaliFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_janam_kundali, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }


    /*views initialization
    *
    * */
    private void initViews(View view) {
        mainLayout = (LinearLayout) view.findViewById(R.id.layoutMain);

        editTextName = (EditText) view.findViewById(R.id.editTextName);
        editTextName.addTextChangedListener(mInputWatcher);

        editTextPlace = (EditText) view.findViewById(R.id.editTextBirthPlace);
        editTextPlace.addTextChangedListener(mInputWatcher);

        editTextDob = (EditText) view.findViewById(R.id.editTextDob);
        editTextDob.setKeyListener(null);
        editTextDob.setOnClickListener(this);
//        setBirthDate();

        editTextTime = (EditText) view.findViewById(R.id.editTextTime);
        editTextTime.setKeyListener(null);
        editTextTime.setOnClickListener(this);
//        setBirthTime();

        editTextQuestion = (EditText) view.findViewById(R.id.editTextQuestion);
        editTextQuestion.addTextChangedListener(mInputWatcher);

        genderRadioGroup = (RadioGroup) view.findViewById(R.id.radioGroupGender);

        mProgressView = (CustomProgressView) view.findViewById(R.id.progress_view);

        buttonSubmit = (Button) view.findViewById(R.id.buttonJanamKunadaliSubmit);
        buttonSubmit.setOnClickListener(this);

        buttonViewReply = (Button) view.findViewById(R.id.buttonViewReply);
        buttonViewReply.setOnClickListener(this);

        buttonUpload = (Button) view.findViewById(R.id.buttonUploadCheena);
        buttonUpload.setOnClickListener(this);


        buttonHelp = (Button) view.findViewById(R.id.buttonHelp);
        buttonHelp.setOnClickListener(this);
        imageView = (ImageView) view.findViewById(R.id.imageViewJanamKundali);

    }

    /**
     * Set birth date in the edittext
     */
    private void setBirthDate() {
        editTextDob.setText(dateFormatter.format(currentDateTime.getTime()));
    }

    /**
     * set birth time in the edittext
     */
    private void setBirthTime() {
        editTextTime.setText(timeFormatter.format(currentDateTime.getTime()));
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        currentDateTime.set(Calendar.YEAR, year);
        currentDateTime.set(Calendar.MONTH, monthOfYear);
        currentDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        setBirthDate();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute, int seconds) {
        currentDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        currentDateTime.set(Calendar.MINUTE, minute);
        currentDateTime.set(Calendar.SECOND, seconds);
        setBirthTime();
    }

    /**
     * Validate all fields and send data to server if all fields are valid
     */
    private void validateFields() {
        AppUtil.hideSoftKeyboard(getActivity());

        // reset previously set errors
        resetError();

        // Store values at the time of the submit attempt.
        final String name = editTextName.getText().toString().trim();
        final String birthPlace = editTextPlace.getText().toString().trim();
        final String reason = editTextQuestion.getText().toString().trim();

        boolean valid = true;
        View focusView = null;

        // check for location
        if (TextUtils.isEmpty(birthPlace)) {
            showError(editTextPlace, R.string.error_birth_place_required);
            focusView = editTextPlace;
            valid = false;
        }

        // check for name
        if (TextUtils.isEmpty(name)) {
            showError(editTextName, R.string.error_name_required);
            focusView = editTextPlace;
            valid = false;
        }

        // check for reason
        if (TextUtils.isEmpty(reason)) {
            showError(editTextQuestion, R.string.error_reason_required);
            focusView = editTextQuestion;
            valid = false;
        }

        if (valid) {

            try {

                sendJanamKundaliRequest();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            // There was an error; don't attempt submit and focus the first
            // form field with an error.
            focusView.requestFocus();
        }
    }

    /**
     * reset the error
     */
    private void resetError() {
        editTextName.setError(null);
        editTextPlace.setError(null);
        editTextQuestion.setError(null);
    }


    /*
    * Show error in the provided edittext

    * */
    private void showError(EditText editText, int errorMessageId) {
        editText.setError(getString(errorMessageId));
    }

    /**
     * Send the Janam kundali request to the server
     */
    private void sendJanamKundaliRequest() {
        if (AppUtil.isInternetConnected(getActivity())) {
            showProgress(true);

            final String loginToken = PreferencesHelper.getLoginToken(getActivity());
            final String name = editTextName.getText().toString().trim();
            final String birthDate = submitDateFormatter.format(currentDateTime.getTime());
            final String birthTime = timeFormatter.format(currentDateTime.getTime());
            final String birthPlace = editTextPlace.getText().toString().trim();
            final String reason = editTextQuestion.getText().toString().trim();
            radioButton = (RadioButton) getActivity().findViewById(genderRadioGroup.getCheckedRadioButtonId());
            final String gender = radioButton.getText().toString();

            final Builders.Any.B builder = Ion.with(this).load(URL_REQUEST_JANAM_KUNDALI);
            AppLog.showLog(TAG, "url janamkundali" + URL_REQUEST_JANAM_KUNDALI);
            if (BuildConfig.DEBUG) {
                builder.setLogging(TAG, Log.VERBOSE);
            }
            AppLog.showLog(TAG, " janam kundali imagefile :: " + imageFile);
            AppLog.showLog("login token " + loginToken);
            if (null == imageFile) {
                builder.setHeader(ApiConstants.HEADER_KEY_ACCEPT, ApiConstants.HEADER_ACCEPT_VALUE)
                        .setMultipartParameter("token", loginToken)
                        .setMultipartParameter("name", name)
                        .setMultipartParameter("dob", birthDate)
                        .setMultipartParameter("time", birthTime)
                        .setMultipartParameter("birthPlace", birthPlace)
                        .setMultipartParameter("gender", gender)
                        .setMultipartParameter("reason", reason)
                        .as(GeneralResponse.class)
                        .setCallback(mRequestCallback);
            } else {
                builder.setHeader(ApiConstants.HEADER_KEY_ACCEPT, ApiConstants.HEADER_ACCEPT_VALUE)
                        .setMultipartParameter("token", loginToken)
                        .setMultipartParameter("name", name)
                        .setMultipartParameter("dob", birthDate)
                        .setMultipartParameter("time", birthTime)
                        .setMultipartParameter("birthPlace", birthPlace)
                        .setMultipartParameter("gender", gender)
                        .setMultipartParameter("reason", reason)
                        .setMultipartFile("chinoimage", imageFile)
                        .as(GeneralResponse.class)
                        .setCallback(mRequestCallback);
            }


        } else {
            Snackbar.make(mainLayout, R.string.error_network_connection, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Shows the progress while request submission.
     *
     * @param showProgress <code>true</code> to show JanamKundaliReplyFragmentthe progress <code>false</code> otherwise.
     */
    public void showProgress(final boolean showProgress) {
        // hide submit menu when progress is visible
//        showSubmitMenu(!showProgress);
        mProgressView.setVisibility(showProgress ? View.VISIBLE : View.GONE);
        mProgressView.setProgressMessage(R.string.message_submitting);
    }


    public void showHelpProgress(final boolean showProgress) {
        // hide submit menu when progress is visible
//        showSubmitMenu(!showProgress);
        mProgressView.setVisibility(showProgress ? View.VISIBLE : View.GONE);
        mProgressView.setProgressMessage(R.string.message_loading);
    }

    @Override
    public void onClick(View clickedView) {
        switch (clickedView.getId()) {
            case R.id.editTextDob: {
                DatePickerDialogFragment.showDatePicker(getActivity().getSupportFragmentManager(), this, currentDateTime);
                break;
            }
            case R.id.editTextTime: {
                TimePickerDialogFragment.showTimePicker(getActivity().getSupportFragmentManager(), this, currentDateTime);
                break;
            }
            case R.id.buttonViewReply:
                AppLog.showLog(TAG, "reply clicked");
                Intent intent = new Intent(getActivity(), JanamKundaliReplyActivity.class);
                startActivity(intent);
                break;
            case R.id.buttonJanamKunadaliSubmit:
                AppLog.showLog(TAG, "submit clicked");
                validateFields();
                break;

            case R.id.buttonHelp:
                AppLog.showLog(TAG, "help clicked");
                getHelpDataFromServer();

                break;
            case R.id.buttonUploadCheena:
                Intent intentUpload = new Intent();
                intentUpload.setType("image/*");
                intentUpload.setAction(Intent.ACTION_GET_CONTENT);
//                getActivity().startActivityForResult(Intent.createChooser(intentUpload, "Select picture"), SELECT_IMAGE);
                startActivityForResult(Intent.createChooser(intentUpload, "Select picture"), SELECT_IMAGE);
            default: {
                //no-op
            }
        }
    }

    /*
    * fetch data for help from server
    * */
    private void getHelpDataFromServer() {
        if (AppUtil.isInternetConnected(getActivity())) {
            showHelpProgress(true);
            final Builders.Any.B builder = Ion.with(this).load(URL_REQUEST_HELP);
            AppLog.showLog(TAG, "url help :: " + URL_REQUEST_HELP);
            if (BuildConfig.DEBUG) {
                builder.setLogging(TAG, Log.VERBOSE);
                builder.setHeader(ApiConstants.HEADER_KEY_ACCEPT, ApiConstants.HEADER_ACCEPT_VALUE)
                        .as(HelpResponse.class)
                        .setCallback(helpCallBack);

            }
        }else {
            Snackbar.make(mainLayout, R.string.error_network_connection, Snackbar.LENGTH_LONG).show();
        }

    }

    /**
     * Callback for help url
     */
    private FutureCallback<HelpResponse> helpCallBack = new FutureCallback<HelpResponse>() {
        @Override
        public void onCompleted(Exception exception, HelpResponse serverResponse) {
            if (null == exception) {
                if (TextUtils.equals(ApiConstants.RESPONSE_STATUS_SUCCESS, serverResponse.getStatus())) {
                    url = serverResponse.getUrl();
                    showHelpProgress(false);
                    if (!TextUtils.isEmpty(url)) {
                        AppLog.showLog(TAG, "help url :: "+url);
                        getActivity().startActivity(YoutubePlayerActivity.getStartIntent(getActivity(), url));
                    } else{
                        showMessageToUser(serverResponse.getMessage());
                    }

                } else if (TextUtils.equals(ApiConstants.RESPONSE_STATUS_FAILURE, serverResponse.getStatus())) {
                    AppLog.showLog(TAG, serverResponse.getMessage());
                    String message = serverResponse.getMessage();
                    showMessageToUser(message);
                    // TODO : track failure in getting photo using Google Analytics(or Crashlytics)
                } else {
                    showMessageToUser(R.string.error_problem_occurred);
                    // TODO : track connection/server error using Google Analytics(or Crashlytics)
                }
            } else {
                Log.e(TAG, exception.getLocalizedMessage(), exception);
                showMessageToUser(R.string.error_problem_occurred);
                CrashlyticsHelper.sendCaughtException(exception);
            }
        }
    };

    /**
     * Show the message and hide the ProgressBar
     *
     * @param messageId Resource id of message to show.
     */
    private void showMessageToUser(int messageId) {
        showMessageToUser(getString(messageId));
    }

    /**
     * Show the message and hide the ProgressBar
     *
     * @param message message to show
     */
    private void showMessageToUser(String message) {
        showHelpProgress(false);
        showProgress(false);
        if (ServerResponse.isTokenInvalid(message)) {
            AppUtil.showInvalidTokenSnackBar(mainLayout, message, getActivity());
        }else{
            Snackbar.make(mainLayout, message, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AppLog.showLog(TAG, " on activity result");
        AppLog.showLog(TAG, "request code" + requestCode + "result code" + resultCode);
        if (requestCode == SELECT_IMAGE) {
            if (resultCode == getActivity().RESULT_OK) {
                if (null != data) {
                    try {
                        AppLog.showLog(TAG, " data" + data);
                        imageUri = FileHelper.getImageUriWithAuthority(getActivity(), data.getData());
                        if (null != imageUri) {
                            imageView.setImageURI(imageUri);
                            convertIntoFile(imageUri);
                        }
                        AppLog.showLog(TAG, "image uri ::" + imageUri);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_SHORT).show();
            }
        } else {
            // TODO other result code
        }
    }

    /*
    * converts image into file
    * */

    public void convertIntoFile(Uri imageUri) {
        String actualPath = getRealPathFromURI(imageUri);
        AppLog.showLog(TAG, "actualPath" + actualPath);
        AppLog.showLog(TAG, "image uri" + imageUri);
        imageFile = new File(actualPath);
        if (imageFile.exists()) {
            AppLog.showLog(TAG, "File found");
        } else {
            AppLog.showLog(TAG, "File not found");
        }
        AppLog.showLog(TAG, "image file :" + imageFile);

    }


    /*
    * gets image path
    * */
    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getActivity().getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    @Override
    public void onStop() {
        super.onStop();
        Ion.getDefault(getActivity()).cancelAll(this);
    }
}
