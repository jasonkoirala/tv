package com.shirantech.sathitv.activity;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;
import com.shirantech.sathitv.BuildConfig;
import com.shirantech.sathitv.R;
import com.shirantech.sathitv.adapter.ChitChatRecyclerViewAdapter;
import com.shirantech.sathitv.helper.CrashlyticsHelper;
import com.shirantech.sathitv.helper.PreferencesHelper;
import com.shirantech.sathitv.logger.Log;
import com.shirantech.sathitv.model.ChitChat;
import com.shirantech.sathitv.model.postparams.ChitChatReceiveMessageParams;
import com.shirantech.sathitv.model.postparams.ChitChatSendMessageParams;
import com.shirantech.sathitv.model.response.ChitChatReceiveMessageResponse;
import com.shirantech.sathitv.model.response.GcmResponse;
import com.shirantech.sathitv.model.response.GeneralResponse;
import com.shirantech.sathitv.network.ApiConstants;
import com.shirantech.sathitv.utils.AppLog;
import com.shirantech.sathitv.utils.AppUtil;
import com.shirantech.sathitv.utils.ServerResponse;
import com.shirantech.sathitv.widget.CustomFontTextView;
import com.shirantech.sathitv.widget.CustomProgressView;

import java.util.ArrayList;
import java.util.List;

/*
* this class shows chat list
* */
public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = ChatActivity.class.getName();
    private static final String URL_CHITCHAT_SEND_MESSAGE = ApiConstants.CHIT_CHAT_URL + "sendMessage";
    private static final String URL_CHITCHAT_RECEIVE_MESSAGE = ApiConstants.CHIT_CHAT_URL + "receiveMessage";
    private static final String URL_CHITCHAT_DELIVER_MESSAGE = ApiConstants.CHIT_CHAT_URL + "deliverMessage";
    public static final String EXTRA_ID = "id";
    public static final String EXTRA_NAME = "name";
    public static final String EXTRA_MESSAGE = "message";
    public static final String EXTRA_IMAGE = "image";
    private LinearLayout linearLayout;
    //    private RelativeLayout relativeLayout;
    private CustomFontTextView textViewNoData;
    private CustomProgressView mProgressView;
    private RecyclerView recyclerView;
    private EditText editTextMessage;
    private List<ChitChat> chitChatList = new ArrayList<>();
    private Button buttonSend;
    private int id;
    private String name, groupId, message, image;
    private ChitChatRecyclerViewAdapter adapter;
    private ChitChat chatSendMessage;

    private IntentFilter intentFilter = new IntentFilter();
    private BroadcastReceiver chatReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            AppLog.showLog(TAG, "on receive ");
            String chatMessage = intent.getExtras().getString("message");
            GcmResponse gcmResponse = new Gson().fromJson(chatMessage, GcmResponse.class);
            if (gcmResponse.getMessageType().equals("Chitchat")) {
                String latestChatMessage = gcmResponse.getGcmMessage();
                int notificationId = gcmResponse.getNotificationId();
                AppLog.showLog(TAG, "notificationId : " + notificationId);
                cancelNotification(notificationId);
                ChitChat chitChat = new ChitChat();
                chitChat.setMessage(latestChatMessage);
                chitChat.setSender(gcmResponse.getName());
                chitChat.setInitiate(false);
                chitChat.setReceiver(gcmResponse.getReceiverName());
                addMessageToList(chitChat);
                AppLog.showLog(TAG, "latest : " + latestChatMessage);
            }
            AppLog.showLog(TAG, "data received: " + intent.getExtras().getString("message"));
            abortBroadcast();
        }
    };

    /*
    * add message to chatlist
    * */
    private void addMessageToList(ChitChat chitChat) {
        if (null != adapter) {
            adapter.addLatestMessage(chitChat);
            scrollListToBottom();
        } else {
            chitChatList.add(chitChat);
            adapter = new ChitChatRecyclerViewAdapter(chitChatList, this);
            recyclerView.setAdapter(adapter);
        }
    }

    /*
    * Notification removed when chat is active
    * */
    private void cancelNotification(int notificationId) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_chat);
        id = getIntent().getIntExtra(EXTRA_ID, 0);
        name = getIntent().getStringExtra(EXTRA_NAME);
        message = getIntent().getStringExtra(EXTRA_MESSAGE);
        image = getIntent().getStringExtra(EXTRA_IMAGE);
        PreferencesHelper.setImageUrl(this, image);
        AppLog.showLog(TAG, "id :: " + id + "name :: " + name + "message ::: " + message + "image :: " + image);
        setUpToolbar();
        initViews();
        receiveChatMessage();
        setFiltersInIntent();

    }

    private void setFiltersInIntent() {
        intentFilter.addAction("com.google.android.c2dm.intent.RECEIVE");
        intentFilter.addCategory("com.shirantech.sathitv");
        intentFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(chatReceiver, intentFilter);
        AppLog.showLog(TAG, "on start");
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(chatReceiver, intentFilter);
       /* PreferencesHelper.getChatMessage(this);

        AppLog.showLog(TAG,
                PreferencesHelper.getChatMessage(this) != null ?
                PreferencesHelper.getChatMessage(this).size()+"size chat :: ": " size 0" );
//        generateListView();*/
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(chatReceiver);
/*
       PreferencesHelper.setChatMessage(this, chitChatList);
*/
    }

    @Override
    protected void onStop() {
        super.onStop();
        registerReceiver(chatReceiver, intentFilter);
        unregisterReceiver(chatReceiver);
    }

    /*
    * initialize views
    * */
    private void initViews() {
        editTextMessage = (EditText) findViewById(R.id.editTextChatMessage);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayoutChat);
//        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayoutChatParent);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewChitChat);
        textViewNoData = (CustomFontTextView) findViewById(R.id.textViewNoData);
        mProgressView = (CustomProgressView) findViewById(R.id.progress_view);
        buttonSend = (Button) findViewById(R.id.buttonChatSend);
        buttonSend.setOnClickListener(this);

    }

    /*
    * toolbar initialization
    * */
    private void setUpToolbar() {
        final Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            //noinspection ConstantConditions
            getSupportActionBar().setTitle(name);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        sendChatRequest();
    }


    /*
    * callback for receive message
    * */
    private FutureCallback<ChitChatReceiveMessageResponse> receiveChatMessageCallback = new FutureCallback<ChitChatReceiveMessageResponse>() {
        @Override
        public void onCompleted(Exception exception, ChitChatReceiveMessageResponse serverResponse) {
            if (null == exception) {
                AppLog.showLog(TAG, "status :" + serverResponse.getStatus());
                if (TextUtils.equals(ApiConstants.RESPONSE_STATUS_SUCCESS, serverResponse.getStatus())) {
                    showProgress(false);
                    chitChatList = serverResponse.getChatMessageList();
                    groupId = serverResponse.getMessageGroupId();
                    AppLog.showLog(TAG, "groupId " + groupId);
                    AppLog.showLog(TAG, "chitChatList size" + chitChatList.size());
                    textViewNoData.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    populateChatMessage();

                } else if (TextUtils.equals(ApiConstants.RESPONSE_STATUS_FAILURE, serverResponse.getStatus())) {
                    AppLog.showLog(TAG, "errorCode " + serverResponse.getCode());
                    showFailureMessage(serverResponse.getMessage());
//                     TODO : track janam kundali request failure using Google Analytics(or Crashlytics)
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
    * check chatlist empty or not
    * */
    private void populateChatMessage() {
        AppLog.showLog(TAG, "populate data");
        if ((null != chitChatList && !chitChatList.isEmpty())) {
            showProgress(false);
            setupReplyList();
        } else {
            showMessageToUser(R.string.message_no_chat_list);
        }
    }

    /*
    * set chat messages to the list
    * */
    private void setupReplyList() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChitChatRecyclerViewAdapter(chitChatList, this);
        recyclerView.setAdapter(adapter);
        scrollListToBottom();

    }


    private void scrollListToBottom() {
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                recyclerView.scrollToPosition(chitChatList.size() - 1);
            }
        });
    }

    private void showMessageToUser(int messageId) {
        showMessageToUser(getString(messageId));
    }

    private void showMessageToUser(String message) {
//        progressView.setProgressMessage(message);
//        progressView.hideProgressBar();
        AppUtil.showInvalidTokenSnackBar(linearLayout, message, this);
    }


    /**
     * Shows the progress while request submission.
     *
     * @param showProgress <code>true</code> to show the progress <code>false</code> otherwise.
     */
    public void showProgress(final boolean showProgress) {
        // hide submit menu when progress is visible
//        showSubmitMenu(!showProgress);
        mProgressView.setVisibility(showProgress ? View.VISIBLE : View.GONE);
        mProgressView.setProgressMessage(R.string.chat_message_loading);
    }


    public void showSendingProgress(final boolean showProgress) {
        // hide submit menu when progress is visible
//        showSubmitMenu(!showProgress);
        mProgressView.setVisibility(showProgress ? View.VISIBLE : View.GONE);
        mProgressView.setProgressMessage(R.string.chat_message_sending);
    }


    /*
    * fetch receive message from server
    * */
    private void receiveChatMessage() {
        if (AppUtil.isInternetConnected(ChatActivity.this)) {
            showProgress(true);
            AppLog.showLog(TAG, "receiveChatMessage");
            final String loginToken = PreferencesHelper.getLoginToken(this);
            final Builders.Any.B builder = Ion.with(this).load(URL_CHITCHAT_RECEIVE_MESSAGE);
            if (BuildConfig.DEBUG) {
                builder.setLogging(TAG, Log.VERBOSE);
            }
            AppLog.showLog(TAG, "receiveChatMessage : " + id + "\n" + loginToken);
            builder.setHeader(ApiConstants.HEADER_KEY_ACCEPT, ApiConstants.HEADER_ACCEPT_VALUE)
                    .setJsonPojoBody(new ChitChatReceiveMessageParams(loginToken, id))
                    .as(ChitChatReceiveMessageResponse.class)
                    .setCallback(receiveChatMessageCallback);
        } else {
            Snackbar.make(linearLayout, R.string.error_network_connection, Snackbar.LENGTH_LONG).show();
        }

    }


    /*
    * callback for sending chat message
    * */
    private FutureCallback<GeneralResponse> mRequestCallback = new FutureCallback<GeneralResponse>() {
        @Override
        public void onCompleted(Exception exception, GeneralResponse serverResponse) {
            if (null == exception) {
                if (TextUtils.equals(ApiConstants.RESPONSE_STATUS_SUCCESS, serverResponse.getStatus())) {
//                    showSendingProgress(false);
                    textViewNoData.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    addMessageToList(chatSendMessage);

                } else if (TextUtils.equals(ApiConstants.RESPONSE_STATUS_FAILURE, serverResponse.getStatus())) {
                    AppLog.showLog(TAG, "error message ::" + serverResponse.getMessage());
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

    private void showFailureMessage(String message) {
//        showSendingProgress(false);
        showProgress(false);
        if (ServerResponse.isTokenInvalid(message)) {
            AppUtil.showInvalidTokenSnackBar(recyclerView, message, ChatActivity.this);
        } else {
            textViewNoData.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    private void showFailureMessage(int messageId) {
        showFailureMessage(getString(messageId));
    }


    /*
    *
    * fetch send message from server
    * */
    private void sendChatRequest() {
        if (AppUtil.isInternetConnected(ChatActivity.this)) {
//            showSendingProgress(true);
            final String loginToken = PreferencesHelper.getLoginToken(this);
            final String message = editTextMessage.getText().toString().trim();
            if (!TextUtils.isEmpty(message)) {
                chatSendMessage = new ChitChat();
                chatSendMessage.setMessage(message);
                chatSendMessage.setReceiver(PreferencesHelper.getUsername(this));
                chatSendMessage.setSender(name);
                chatSendMessage.setInitiate(true);

                final Builders.Any.B builder = Ion.with(this).load(URL_CHITCHAT_SEND_MESSAGE);
                if (BuildConfig.DEBUG) {
                    builder.setLogging(TAG, Log.VERBOSE);
                }
                builder.setHeader(ApiConstants.HEADER_KEY_ACCEPT, ApiConstants.HEADER_ACCEPT_VALUE)
                        .setJsonPojoBody(new ChitChatSendMessageParams(loginToken, id, message))
                        .as(GeneralResponse.class)
                        .setCallback(mRequestCallback);
                editTextMessage.setText(null);
            } else {
                editTextMessage.setError("Please enter message");
            }
        } else {
            Snackbar.make(recyclerView, R.string.error_network_connection, Snackbar.LENGTH_LONG).show();
        }

    }
}
