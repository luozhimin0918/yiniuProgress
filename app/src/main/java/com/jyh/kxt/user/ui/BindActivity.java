package com.jyh.kxt.user.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.custom.DiscolorButton;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.base.widget.FunctionEditText;
import com.jyh.kxt.user.json.UserJson;
import com.jyh.kxt.user.presenter.BindPresenter;
import com.library.bean.EventBusClass;
import com.library.widget.window.ToastView;

import org.greenrobot.eventbus.EventBus;

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
    @BindView(R.id.edt_email) public FunctionEditText edtEmail;
    @BindView(R.id.edt_pwd) public FunctionEditText edtPwd;
    @BindView(R.id.ll_step1) LinearLayout llStep1;
    @BindView(R.id.ll_setPwd) LinearLayout llStep2;
    @BindView(R.id.edt_pwd1) public FunctionEditText edtPwd1;
    @BindView(R.id.edt_pwd2) public FunctionEditText edtPwd2;
    @BindView(R.id.btn_send) DiscolorButton btnSend;
    @BindView(R.id.iv_bar_break) ImageView ivBreak;
    @BindView(R.id.tv_bar_title) TextView tvTitle;

    private BindPresenter bindPresenter;

    private int type;
    private int step = 1;//步骤
    private AlertDialog errorDialog;

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
        edtPwd.setFunctionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //请求动态密码
                bindPresenter.requestCode(step);
            }
        });
    }

    @OnClick({R.id.iv_bar_break, R.id.btn_send})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_bar_break:
                onBackPressed();
                break;
            case R.id.btn_send:
                final boolean isSetPwd = LoginUtils.getUserInfo(this).isSetPwd();
                if (type == TYPE_BIND_PHONE || type == TYPE_BIND_EMAIL) {
                    if (step == 1) {
                        bindPresenter.step2(isSetPwd, edtEmail.getEdtText(), edtPwd.getEdtText(), new ObserverData() {
                            @Override
                            public void callback(Object o) {
                                if (isSetPwd) {
                                    UserJson userJson = LoginUtils.getUserInfo(BindActivity.this);
                                    if (type == BindActivity.TYPE_BIND_PHONE) {
                                        userJson.setPhone(edtEmail.getEdtText());
                                        userJson.setIs_set_phone("1");
                                    } else {
                                        userJson.setEmail(edtEmail.getEdtText());
                                        userJson.setIs_set_email("1");
                                    }
                                    LoginUtils.changeUserInfo(getContext(), userJson);
                                    EventBus.getDefault().post(new EventBusClass(EventBusClass.EVENT_LOGIN_UPDATE, userJson));
                                    ToastView.makeText3(getContext(), "绑定成功");
                                    finish();
                                } else {
                                    //设置密码
                                    step = 2;
                                    tvTitle.setText("设置密码");
                                    llStep1.setVisibility(View.GONE);
                                    llStep2.setVisibility(View.VISIBLE);
                                    tvWarning.setText("设置6-16个字符，请至少使用字母，数字和符号两种以上组合");
                                }
                            }

                            @Override
                            public void onError(Exception e) {
                                ToastView.makeText3(getContext(), e == null || e.getMessage() == null ? "绑定失败" : e.getMessage());
                            }
                        });
                    } else {
                        bindPresenter.step3(edtEmail.getEdtText(), edtPwd2.getEdtText(), new ObserverData() {
                            @Override
                            public void callback(Object o) {
                                UserJson userJson = LoginUtils.getUserInfo(BindActivity.this);
                                userJson.setIs_set_password("1");
                                LoginUtils.changeUserInfo(getContext(), userJson);
                                EventBus.getDefault().post(new EventBusClass(EventBusClass.EVENT_LOGIN_UPDATE, userJson));
                                ToastView.makeText3(getContext(), "密码设置成功");
                                finish();
                            }

                            @Override
                            public void onError(Exception e) {
                                ToastView.makeText3(getContext(), e == null || e.getMessage() == null ? "密码设置失败" : e.getMessage());
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
                                tvTitle.setText(type == TYPE_CHANGE_PHONE ? "绑定手机号" : "绑定邮箱");
                                restoreView();

                            }

                            @Override
                            public void onError(Exception e) {
                                ToastView.makeText3(getContext(), e == null || e.getMessage() == null ? "验证失败" : e.getMessage());
                            }
                        });
                    } else if (step == 2) {
                        bindPresenter.step2(isSetPwd, edtEmail.getEdtText(), edtPwd.getEdtText(), new ObserverData() {
                            @Override
                            public void callback(Object o) {
                                if (isSetPwd) {
                                    UserJson userJson = LoginUtils.getUserInfo(BindActivity.this);
                                    if (type == BindActivity.TYPE_CHANGE_PHONE) {
                                        userJson.setPhone(edtEmail.getEdtText());
                                        userJson.setIs_set_phone("1");
                                    } else {
                                        userJson.setEmail(edtEmail.getEdtText());
                                        userJson.setIs_set_email("1");
                                    }
                                    LoginUtils.changeUserInfo(getContext(), userJson);
                                    EventBus.getDefault().post(new EventBusClass(EventBusClass.EVENT_LOGIN_UPDATE, userJson));
                                    ToastView.makeText3(getContext(), "绑定成功");
                                    finish();
                                } else {
                                    step = 3;
                                    tvTitle.setText("设置密码");
                                    llStep1.setVisibility(View.GONE);
                                    llStep2.setVisibility(View.VISIBLE);
                                    tvWarning.setText("设置6-16个字符，请至少使用字母，数字和符号两种以上组合");
                                }
                            }

                            @Override
                            public void onError(Exception e) {
                                ToastView.makeText3(getContext(), e == null || e.getMessage() == null ? "绑定失败" : e.getMessage());
                            }
                        });
                    } else {
                        bindPresenter.step3(edtEmail.getEdtText(), edtPwd2.getEdtText(), new ObserverData() {
                            @Override
                            public void callback(Object o) {
                                UserJson userJson = LoginUtils.getUserInfo(BindActivity.this);
                                userJson.setIs_set_password("1");
                                LoginUtils.changeUserInfo(getContext(), userJson);
                                EventBus.getDefault().post(new EventBusClass(EventBusClass.EVENT_LOGIN_UPDATE, userJson));
                                ToastView.makeText3(getContext(), "密码设置成功");
                                finish();
                            }

                            @Override
                            public void onError(Exception e) {
                                ToastView.makeText3(getContext(), e == null || e.getMessage() == null ? "密码设置失败" : e.getMessage());
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
                showErrorView();
            }
        } else {
            if (step == 1) {
                super.onBackPressed();
            } else if (step == 2) {
                bindPresenter.saveData(step, tvWarning.getText().toString(), edtEmail.getEdtText(), edtPwd.getEdtText(), edtPwd
                        .getFunctionText(), btnSend.getText().toString(), edtPwd.getType(), edtPwd.getFunctionTextColor());
                tvTitle.setText("身份校验");
                step = 1;
                restoreView();
            } else {
                showErrorView();
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

    /**
     * 显示倒计时
     *
     * @param time
     * @param step
     */
    public void setCountDown(int time, int step) {
        if (this.step == step) {
            if (time > 0) {
                edtPwd.setFunctionText(time + "秒后重发");
                edtPwd.setFunctionTextColor(ContextCompat.getColor(this, R.color.font_color9));
            } else {
                edtPwd.setFunctionText("获取动态码");
                edtPwd.setFunctionTextColor(ContextCompat.getColor(this, R.color.font_color1));
            }
        }
    }

    /**
     * 提示设置密码
     */
    private void showErrorView() {
        if (errorDialog == null) {
            errorDialog = new AlertDialog.Builder(this)
                    .setTitle("温馨提示")
                    .setMessage("您还未设置昵称或密码，退出将导致注册失败")
                    .setNegativeButton("返回", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            edtPwd.setType(2);
                            edtPwd.reflash();
                            if (type == TYPE_BIND_PHONE || type == TYPE_BIND_EMAIL) {
                                step = 1;
                                tvWarning.setText(bindPresenter.getStep1Json().getString("warning"));
                                llStep1.setVisibility(View.VISIBLE);
                                llStep2.setVisibility(View.GONE);
                                tvTitle.setText(type == TYPE_BIND_PHONE ? "绑定手机号" : "绑定邮箱");
                            } else {
                                step = 2;
                                tvWarning.setText(bindPresenter.getStep2Json().getString("warning"));
                                llStep1.setVisibility(View.VISIBLE);
                                llStep2.setVisibility(View.GONE);
                                tvTitle.setText(type == TYPE_CHANGE_PHONE ? "绑定手机号" : "绑定邮箱");
                            }
                        }
                    }).setPositiveButton("设置密码", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create();
        }
        if (errorDialog.isShowing()) {
            errorDialog.dismiss();
        }
        errorDialog.show();
    }
}
