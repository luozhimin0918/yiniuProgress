package com.jyh.kxt.user.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.index.ui.WebActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 项目名:Kxt
 * 类描述:关于
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/14.
 */

public class AboutActivity extends BaseActivity {

    @BindView(R.id.iv_bar_break) ImageView ivBarBreak;
    @BindView(R.id.tv_bar_title) TextView tvBarTitle;
    @BindView(R.id.iv_bar_function) TextView ivBarFunction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_about, StatusBarColor.THEME1);

        ivBarBreak.setImageResource(R.mipmap.ico_break);
        tvBarTitle.setText("关于我们");
        ivBarFunction.setVisibility(View.INVISIBLE);

    }

    @OnClick({R.id.iv_bar_break, R.id.rl_statement, R.id.rl_contact, R.id.rl_feedback, R.id.rl_visit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_bar_break:
                onBackPressed();
                break;
            case R.id.rl_statement:
                //声明条款
                Intent intent = new Intent(this, StatementActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;
            case R.id.rl_contact:
                //联系我们
                Intent contactIntent = new Intent(this, WebActivity.class);
                contactIntent.putExtra(IntentConstant.NAME, "联系我们");
                contactIntent.putExtra(IntentConstant.WEBURL, HttpConstant.CONTACT_US_URL);
                startActivity(contactIntent);
                break;
            case R.id.rl_feedback:
                //意见反馈
                Intent feedbackIntent = new Intent(this, WebActivity.class);
                feedbackIntent.putExtra(IntentConstant.NAME, "意见反馈");
                feedbackIntent.putExtra(IntentConstant.WEBURL, HttpConstant.FEEDBACK_URL);
                startActivity(feedbackIntent);
                break;
            case R.id.rl_visit:
                //访问官网
                Intent intent3 = new Intent(Intent.ACTION_VIEW);
                intent3.setData(Uri.parse(HttpConstant.OFFICIAL));
                startActivity(intent3);
                break;
        }
    }
}
