package com.jyh.kxt.user.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 项目名:Kxt
 * 类描述:声明条款
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/14.
 */

public class StatementActivity extends BaseActivity {
    @BindView(R.id.iv_bar_break) ImageView ivBarBreak;
    @BindView(R.id.tv_bar_title) TextView tvBarTitle;
    @BindView(R.id.iv_bar_function) TextView ivBarFunction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_statement,StatusBarColor.THEME1);

        ivBarBreak.setImageResource(R.mipmap.ico_break);
        ivBarFunction.setVisibility(View.INVISIBLE);
        tvBarTitle.setText("声明条款");
    }

    @OnClick(R.id.iv_bar_break)
    public void onClick() {
        finish();
    }
}
