package com.shirantech.sathitv.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.shirantech.sathitv.R;
import com.shirantech.sathitv.fragment.JanamKundaliReplyFragment;
import com.shirantech.sathitv.utils.AppLog;

public class JanamKundaliReplyActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "message";
    String message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_janam_kundali_reply);
        setupToolbar();
        message = getIntent().getStringExtra(EXTRA_MESSAGE);
        AppLog.showLog( "message ::"+message);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerJanamKundaliReply, JanamKundaliReplyFragment.newInstance())
                .commit();
    }

    /*
    * toolbar initialization
    * */
    private void setupToolbar() {
        final Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            //noinspection ConstantConditions
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.title_janam_kundali_reply));
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }
}
