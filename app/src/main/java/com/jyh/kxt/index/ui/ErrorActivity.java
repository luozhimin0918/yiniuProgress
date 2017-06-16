package com.jyh.kxt.index.ui;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;

import butterknife.BindView;

public class ErrorActivity extends BaseActivity {
    public final static String ERROR_MSG = "errorMessage";

    @BindView(R.id.iv_bar_break) ImageView ivBarBreak;
    @BindView(R.id.tv_bar_title) TextView tvBarTitle;
    @BindView(R.id.iv_bar_function) TextView ivBarFunction;
    @BindView(R.id.tv_error_message) TextView tvErrorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        tvBarTitle.setText("崩溃提醒");

        String errorMessage = getIntent().getStringExtra(ERROR_MSG);
        tvErrorMessage.setText(errorMessage);
    }
}
