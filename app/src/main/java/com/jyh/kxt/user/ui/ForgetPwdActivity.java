package com.jyh.kxt.user.ui;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.user.presenter.ForgetPwdPresenter;
import com.library.util.RegexValidateUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 项目名:Kxt
 * 类描述:忘记密码
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/27.
 */

public class ForgetPwdActivity extends BaseActivity {

    @BindView(R.id.iv_bar_break) ImageView ivBarBreak;
    @BindView(R.id.tv_bar_title) TextView tvBarTitle;
    @BindView(R.id.iv_bar_function) TextView ivBarFunction;
    @BindView(R.id.edt_email) EditText edtEmail;
    @BindView(R.id.iv_clear) ImageView ivClear;
    @BindView(R.id.tv_error) TextView tvError;
    @BindView(R.id.btn_send) Button btnSend;

    private ForgetPwdPresenter forgetPwdPresenter;

    public boolean isError;

    public static final String EMAIL = "email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_forget, StatusBarColor.THEME1);

        forgetPwdPresenter = new ForgetPwdPresenter(this);

        tvBarTitle.setText("忘记密码");

        String emailStr = getIntent().getStringExtra(EMAIL);
        setBtnStatus(emailStr);
        edtEmail.setText(emailStr);

        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isError) {
                    isError = false;
                    showError("");
                }
                setBtnStatus(s.toString());
            }
        });

    }

    @OnClick({R.id.iv_bar_break, R.id.iv_clear, R.id.btn_send})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_bar_break:
                onBackPressed();
                break;
            case R.id.iv_clear:
                edtEmail.setText("");
                break;
            case R.id.btn_send:
                showWaitDialog(null);
                forgetPwdPresenter.sendInfo(edtEmail.getText().toString());
                break;
        }
    }

    /**
     * 显示错误信息
     *
     * @param info
     */
    public void showError(String info) {
        try {
            tvError.setText(info);
        } catch (Exception e) {
            tvError.setText("");
        }
    }

    /**
     * 设置按钮状态
     *
     * @param emailStr
     */
    private void setBtnStatus(String emailStr) {
        boolean b = RegexValidateUtil.checkEmail(emailStr);
        btnSend.setSelected(b);
        btnSend.setClickable(b);
        if (b)
            btnSend.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        else
            btnSend.setTextColor(ContextCompat.getColor(getContext(), R.color.font_color3));
    }

}
