package com.shirantech.sathitv.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.shirantech.sathitv.R;
import com.shirantech.sathitv.fragment.HealthConsultantReplyFragment;
import com.shirantech.sathitv.fragment.JanamKundaliReplyFragment;
import com.shirantech.sathitv.utils.AppLog;

public class HeathConsultantReplyActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "message";
    String message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_health_consultant_reply);
        setupToolbar();
        message = getIntent().getStringExtra(EXTRA_MESSAGE);
        AppLog.showLog( "message ::"+message);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerHealthConsultant, HealthConsultantReplyFragment.newInstance())
                .commit();
    }


    private void setupToolbar() {
        final Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            //noinspection ConstantConditions
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.title_health_consultant_reply));
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }
}
