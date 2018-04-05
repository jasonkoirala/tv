package com.shirantech.sathitv.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.shirantech.sathitv.R;
import com.shirantech.sathitv.utils.AppLog;
import com.shirantech.sathitv.widget.CustomProgressView;

public class NewsWebViewActivity extends AppCompatActivity {

    private static final String TAG = NewsWebViewActivity.class.getName();
    public static final String EXTRA_URL = "news_url";
    private WebView webView;
    private String url;
    WebViewClient webViewClient;
    private CustomProgressView progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_news_web_view);
        setUpToolbar();
        webView = (WebView) findViewById(R.id.webviewNews);
        progressView = (CustomProgressView) findViewById(R.id.progressViewNewsWebView);
        url = getIntent().getStringExtra(EXTRA_URL);
        AppLog.showLog(TAG, "url :: " + url);
        showProgress(true);
        webViewClient = new WevViewClient();
        webView.setWebViewClient(webViewClient);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);
    }

    public void showProgress(final boolean showProgress) {
        progressView.setVisibility(showProgress ? View.VISIBLE : View.GONE);
        progressView.setProgressMessage("Getting news... ");
    }

    private class WevViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);

            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // Make a note that the page has finished loading.
            AppLog.showLog(TAG, "finished loading");
            showProgress(false);
        }

    }


    @Override
    protected void onResume() {
        webView.onResume();
        super.onResume();
    }


    @Override
    protected void onPause() {
        webView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        webView.destroy();
        super.onDestroy();
    }

    private void setUpToolbar() {
        final Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            //noinspection ConstantConditions
            getSupportActionBar().setTitle(getString(R.string.dashboard_news));
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
