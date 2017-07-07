package com.jyh.kxt.index.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.constant.SpConstant;

import butterknife.BindView;
import butterknife.OnClick;

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

        ivBarBreak.setVisibility(View.INVISIBLE);
        tvBarTitle.setText("找不到页面");

        String errorMessage = getIntent().getStringExtra(ERROR_MSG);
        tvErrorMessage.setText(errorMessage);
    }

    private int errorImageClickCount = 0;

    @OnClick({R.id.error_image, R.id.error_restart})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.error_image:
                errorImageClickCount++;
                if (errorImageClickCount >= 5) {
                    tvErrorMessage.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.error_restart:
                Intent intent = new Intent(ErrorActivity.this, MainActivity.class);
                intent.putExtra(SpConstant.MAIN_ACTIVITY_FROM, 1);
                startActivity(intent);

                ErrorActivity.this.finish();
                break;
        }
    }
}
