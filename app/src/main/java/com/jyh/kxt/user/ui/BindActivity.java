package com.jyh.kxt.user.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.custom.DiscolorButton;
import com.jyh.kxt.base.widget.FunctionEditText;
import com.jyh.kxt.user.presenter.BindPresenter;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 项目名:KxtProfessional
 * 类描述:绑定换绑界面
 * 创建人:苟蒙蒙
 * 创建日期:2017/10/24.
 */

public class BindActivity extends BaseActivity {

    public static String TYPE = "type";
    public static final int TYPE_BIND_PHONE = 1;//绑定手机号
    public static final int TYPE_CHANGE_PHONE = 2;//换绑手机号
    public static final int TYPE_BIND_EMAIL = 3;//绑定邮箱
    public static final int TYPE_CHANGE_EMAIL = 4;//换绑邮箱

    @BindView(R.id.tv_warning) TextView tvWarning;
    @BindView(R.id.edt_email) FunctionEditText edtEmail;
    @BindView(R.id.edt_pwd) FunctionEditText edtPwd;
    @BindView(R.id.ll_step1) LinearLayout llStep1;
    @BindView(R.id.edt_pwd2) FunctionEditText edtPwd2;
    @BindView(R.id.btn_send) DiscolorButton btnSend;
    @BindView(R.id.iv_bar_break) ImageView ivBreak;
    @BindView(R.id.tv_bar_title) TextView tvTitle;

    private BindPresenter bindPresenter;

    private int type;
    private int step = 1;//步骤

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_bind, StatusBarColor.THEME1);
        init();
    }

    private void init() {
        type = getIntent().getIntExtra(TYPE, TYPE_BIND_PHONE);
        switch (type) {
            case TYPE_BIND_PHONE:
                tvTitle.setText("手机号绑定");
                break;
            case TYPE_BIND_EMAIL:
                tvTitle.setText("邮箱绑定");
                break;
            case TYPE_CHANGE_PHONE:
            case TYPE_CHANGE_EMAIL:
                tvTitle.setText("身份效验");
                break;
            default:
                tvTitle.setText("手机号绑定");
                break;
        }
        bindPresenter = new BindPresenter(this);
        bindPresenter.setType(type);
    }

    @OnClick({R.id.iv_bar_break, R.id.btn_send})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_bar_break:
                onBackPressed();
                break;
            case R.id.btn_send:
                if (type == TYPE_BIND_PHONE || type == TYPE_BIND_EMAIL) {
                    if (step == 1) {
                        bindPresenter.step1(edtEmail.getEdtText(), edtPwd.getEdtText(), new ObserverData() {
                            @Override
                            public void callback(Object o) {
                                step = 2;
                                llStep1.setVisibility(View.GONE);
                                edtPwd2.setVisibility(View.VISIBLE);
                                tvWarning.setText("我们已经发送验证码到你的手机:/n" + edtEmail.getEdtText());
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
                    } else {
                        bindPresenter.step2(edtEmail.getEdtText(), edtPwd2.getEdtText(), new ObserverData() {
                            @Override
                            public void callback(Object o) {

                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
                    }
                } else {
                    if (step == 1) {
                        bindPresenter.saveData(step, tvWarning.getText().toString(), edtEmail.getEdtText(), edtPwd.getEdtText(), edtPwd
                                .getFunctionText(), btnSend.getText().toString(), edtPwd.getType(), edtPwd.getFunctionTextColor());
                        bindPresenter.step1(edtEmail.getEdtText(), edtPwd2.getEdtText(), new ObserverData() {
                            @Override
                            public void callback(Object o) {
                                bindPresenter.saveData(step, tvWarning.getText().toString(), edtEmail.getEdtText(), edtPwd.getEdtText(),
                                        edtPwd.getFunctionText(), btnSend.getText().toString(), edtPwd.getType(), edtPwd
                                                .getFunctionTextColor());
                                step = 2;
                                restoreView();

                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
                    } else if (step == 2) {
                        bindPresenter.step2(edtEmail.getEdtText(), edtPwd2.getEdtText(), new ObserverData() {
                            @Override
                            public void callback(Object o) {
                                step = 3;
                                llStep1.setVisibility(View.GONE);
                                edtPwd2.setVisibility(View.VISIBLE);
                                tvWarning.setText("我们已经发送验证码到你的手机:/n" + edtEmail.getEdtText());
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
                    } else {
                        bindPresenter.step3(edtEmail.getEdtText(), edtPwd2.getEdtText(), new ObserverData() {
                            @Override
                            public void callback(Object o) {

                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
                    }
                }
                break;
        }
    }

    private void restoreView() {
        if (type == TYPE_CHANGE_PHONE || type == TYPE_CHANGE_EMAIL) {
            JSONObject data = null;
            if (step == 1) {
                data = bindPresenter.getStep1Json();

            } else if (step == 2) {
                data = bindPresenter.getStep2Json();
            }
            tvWarning.setText(data.getString("warning"));
            edtEmail.setHintText(data.getString("hint1"));
            edtPwd.setHintText(data.getString("hint2"));
            btnSend.setText(data.getString("btn"));
            edtPwd.setType(data.getInteger("type2"));
            edtPwd.setFunctionText(data.getString("function"));
            edtPwd.setFunctionTextColor(data.getInteger("functionColor"));
            edtEmail.setEdtText(data.getString("edt1"));
            edtPwd.setEdtText(data.getString("edt2"));
        }
    }

    @Override
    public void onBackPressed() {

        if (type == TYPE_BIND_PHONE || type == TYPE_BIND_EMAIL) {
            if (step == 1) {
                super.onBackPressed();
            } else {
                step = 1;
                tvWarning.setText(bindPresenter.getStep1Json().getString("warning"));
                llStep1.setVisibility(View.VISIBLE);
                edtPwd2.setVisibility(View.GONE);
            }
        } else {
            if (step == 1) {
                super.onBackPressed();
            } else if (step == 2) {
                bindPresenter.saveData(step, tvWarning.getText().toString(), edtEmail.getEdtText(), edtPwd.getEdtText(), edtPwd
                        .getFunctionText(), btnSend.getText().toString(), edtPwd.getType(), edtPwd.getFunctionTextColor());
                step = 1;
                restoreView();
            } else {
                step = 2;
                tvWarning.setText(bindPresenter.getStep1Json().getString("warning"));
                llStep1.setVisibility(View.VISIBLE);
                edtPwd2.setVisibility(View.GONE);
            }
        }

    }

    public void initView() {
        JSONObject data = null;
        if (step == 1) {
            data = bindPresenter.getStep1Json();
        } else {
            data = bindPresenter.getStep2Json();
        }
        tvWarning.setText(data.getString("warning"));
        edtEmail.setHintText(data.getString("hint1"));
        edtPwd.setHintText(data.getString("hint2"));
        btnSend.setText(data.getString("btn"));
        edtPwd.setType(data.getInteger("type2"));
        edtPwd.setFunctionText(data.getString("function"));
        edtPwd.setFunctionTextColor(data.getInteger("functionColor"));
    }
}
