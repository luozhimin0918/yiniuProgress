package com.jyh.kxt.user.ui;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.custom.DiscolorButton;
import com.library.widget.window.ToastView;

import butterknife.BindView;

/**
 * 项目名:Kxt
 * 类描述:意见反馈
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/14.
 */

public class FeedbackActivity extends BaseActivity {

    @BindView(R.id.iv_bar_break) ImageView ivBarBreak;
    @BindView(R.id.tv_bar_title) TextView tvBarTitle;
    @BindView(R.id.iv_bar_function) TextView ivBarFunction;
    @BindView(R.id.btn_copy) DiscolorButton btnCopy;
    @BindView(R.id.tv_jump) TextView tvJump;

    private String copyedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feedback, StatusBarColor.THEME1);

        ivBarBreak.setImageResource(R.mipmap.ico_break);
        ivBarFunction.setVisibility(View.INVISIBLE);
        tvBarTitle.setText("意见反馈");

        btnCopy.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                copyedText = btnCopy.getText().toString();
                ToastView.makeText3(FeedbackActivity.this, "复制成功");
                return false;
            }
        });

        tvJump.setText(Html
                .fromHtml("复制微信后,<a href='http://www.baidu.com'><font color='#00FF00'>点击进入微信</font></a>"));
        tvJump.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
