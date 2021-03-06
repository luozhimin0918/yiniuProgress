package com.jyh.kxt.user.ui;

import android.content.DialogInterface;
import android.nfc.tech.TagTechnology;
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
import com.jyh.kxt.base.utils.validator.EditTextValidator;
import com.jyh.kxt.base.utils.validator.ValidationModel;
import com.jyh.kxt.base.utils.validator.validation.EmailOrPhoneValidation;
import com.jyh.kxt.base.utils.validator.validation.EmailValidation;
import com.jyh.kxt.base.utils.validator.validation.PhoneValidation;
import com.jyh.kxt.base.utils.validator.validation.PwdDynamicValidation;
import com.jyh.kxt.base.utils.validator.validation.PwdValidation;
import com.jyh.kxt.base.widget.FunctionEditText;
import com.jyh.kxt.user.json.UserJson;
import com.jyh.kxt.user.presenter.BindPresenter;
import com.library.bean.EventBusClass;
import com.library.util.RegexValidateUtil;
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
    private EditTextValidator editTextValidator;

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
                editTextValidator = new EditTextValidator(this)
                        .setButton(btnSend)
                        .add(new ValidationModel(edtEmail, new PhoneValidation()))
                        .add(new ValidationModel(edtPwd, new PwdDynamicValidation()))
                        .execute();
                break;
            case TYPE_BIND_EMAIL:
                tvTitle.setText("邮箱绑定");
                editTextValidator = new EditTextValidator(this)
                        .setButton(btnSend)
                        .add(new ValidationModel(edtEmail, new EmailValidation()))
                        .add(new ValidationModel(edtPwd, new PwdDynamicValidation()))
                        .execute();
                break;
            case TYPE_CHANGE_PHONE:
            case TYPE_CHANGE_EMAIL:
                tvTitle.setText("身份效验");
                editTextValidator = new EditTextValidator(this)
                        .setButton(btnSend)
                        .add(new ValidationModel(edtEmail, new EmailOrPhoneValidation()))
                        .add(new ValidationModel(edtPwd, new PwdDynamicValidation()))
                        .execute();
                break;
            default:
                tvTitle.setText("手机号绑定");
                editTextValidator = new EditTextValidator(this)
                        .setButton(btnSend)
                        .add(new ValidationModel(edtEmail, new PhoneValidation()))
                        .add(new ValidationModel(edtPwd, new PwdDynamicValidation()))
                        .execute();
                break;
        }
        bindPresenter = new BindPresenter(this);
        bindPresenter.setType(type);
        edtPwd.setFunctionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //请求动态密码
                if (RegexValidateUtil.isEmpty(edtEmail.getEdtText())) {
                    ToastView.makeText3(getContext(), "手机号或邮箱不能为空");
                    return;
                }
                if (RegexValidateUtil.checkCellphone(edtEmail.getEdtText()) || RegexValidateUtil.checkEmail(edtEmail.getEdtText())) {
                    bindPresenter.requestCode(step);
                } else
                    ToastView.makeText3(getContext(), "手机号或邮箱不合法");

            }
        });
    }

    private String userTemp = "";

    @OnClick({R.id.iv_bar_break, R.id.btn_send})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_bar_break:
                onBackPressed();
                break;
            case R.id.btn_send:
                if (editTextValidator.validate()) {
                    final boolean isSetPwd = LoginUtils.getUserInfo(this).isSetPwd();
                    showWaitDialog(null);
                    if (type == TYPE_BIND_PHONE || type == TYPE_BIND_EMAIL) {
                        if (step == 1) {
                            bindPresenter.step2(isSetPwd, edtEmail.getEdtText(), edtPwd.getEdtText(), new ObserverData() {
                                @Override
                                public void callback(Object o) {
                                    dismissWaitDialog();
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
                                        userTemp = edtEmail.getEdtText();
                                        tvTitle.setText("设置密码");
                                        llStep1.setVisibility(View.GONE);
                                        llStep2.setVisibility(View.VISIBLE);
                                        tvWarning.setText("设置6-16个字符，请至少使用字母，数字和符号两种以上组合");
                                        editTextValidator = new EditTextValidator(getContext())
                                                .setButton(btnSend)
                                                .add(new ValidationModel(edtPwd1, new PwdValidation()))
                                                .add(new ValidationModel(edtPwd2, new PwdValidation()))
                                                .execute();
                                    }
                                }

                                @Override
                                public void onError(Exception e) {
                                    dismissWaitDialog();
                                    ToastView.makeText3(getContext(), e == null || e.getMessage() == null ? "绑定失败" : e.getMessage());
                                }
                            });
                        } else {
                            if (edtPwd1.getEdtText().equals(edtPwd2.getEdtText())) {
                                bindPresenter.step3(edtEmail.getEdtText(), edtPwd2.getEdtText(), new ObserverData() {
                                    @Override
                                    public void callback(Object o) {
                                        dismissWaitDialog();
                                        UserJson userJson = LoginUtils.getUserInfo(BindActivity.this);
                                        userJson.setIs_set_password("1");
                                        if (type == BindActivity.TYPE_BIND_PHONE) {
                                            userJson.setPhone(userTemp);
                                            userJson.setIs_set_phone("1");
                                        } else {
                                            userJson.setEmail(userTemp);
                                            userJson.setIs_set_email("1");
                                        }
                                        LoginUtils.changeUserInfo(getContext(), userJson);
                                        EventBus.getDefault().post(new EventBusClass(EventBusClass.EVENT_LOGIN_UPDATE, userJson));
                                        ToastView.makeText(getContext(), "设置成功");
                                        finish();
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        dismissWaitDialog();
                                        ToastView.makeText(getContext(), e == null || e.getMessage() == null ? "设置失败" : e.getMessage());
                                    }
                                });
                            } else {
                                edtPwd2.setErrorInfo("密码不一致");
                                dismissWaitDialog();
                            }
                        }
                    } else {
                        if (step == 1) {
                            bindPresenter.saveData(step, tvWarning.getText().toString(), edtEmail.getEdtText(), edtPwd.getEdtText(), edtPwd
                                    .getFunctionText(), btnSend.getText().toString(), edtPwd.getType(), edtPwd.getFunctionTextColor());

                            UserJson userJson = LoginUtils.getUserInfo(getContext());
                            String phone = userJson.getPhone();
                            String email = userJson.getEmail();
                            if ((phone != null && phone.trim().equals(edtEmail.getEdtText().trim())) || (email != null && email.trim()
                                    .equals(edtEmail.getEdtText().trim()))) {
                                bindPresenter.step1(edtEmail.getEdtText(), edtPwd.getEdtText(), new ObserverData() {
                                    @Override
                                    public void callback(Object o) {
                                        bindPresenter.saveData(step, tvWarning.getText().toString(), edtEmail.getEdtText(), edtPwd
                                                        .getEdtText(),
                                                edtPwd.getFunctionText(), btnSend.getText().toString(), edtPwd.getType(), edtPwd
                                                        .getFunctionTextColor());
                                        step = 2;
                                        dismissWaitDialog();
                                        tvTitle.setText(type == TYPE_CHANGE_PHONE ? "绑定手机号" : "绑定邮箱");
                                        restoreView();
                                        if (type == TYPE_CHANGE_PHONE) {
                                            editTextValidator = new EditTextValidator(getContext())
                                                    .setButton(btnSend)
                                                    .add(new ValidationModel(edtEmail, new PhoneValidation()))
                                                    .add(new ValidationModel(edtPwd, new PwdDynamicValidation()))
                                                    .execute();
                                        } else {
                                            editTextValidator = new EditTextValidator(getContext())
                                                    .setButton(btnSend)
                                                    .add(new ValidationModel(edtEmail, new EmailValidation()))
                                                    .add(new ValidationModel(edtPwd, new PwdDynamicValidation()))
                                                    .execute();
                                        }

                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        dismissWaitDialog();
                                        ToastView.makeText3(getContext(), e == null || e.getMessage() == null ? "验证失败" : e.getMessage());
                                    }
                                });
                            } else {
                                dismissWaitDialog();
                                ToastView.makeText3(getContext(), "手机号或邮箱不一致,请重新输入");
                            }

                        } else if (step == 2) {
                            bindPresenter.step2(isSetPwd, edtEmail.getEdtText(), edtPwd.getEdtText(), new ObserverData() {
                                @Override
                                public void callback(Object o) {
                                    dismissWaitDialog();
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
                                        ToastView.makeText(getContext(), "绑定成功");
                                        finish();
                                    } else {
                                        step = 3;
                                        userTemp = edtEmail.getEdtText();
                                        tvTitle.setText("设置密码");
                                        llStep1.setVisibility(View.GONE);
                                        llStep2.setVisibility(View.VISIBLE);
                                        tvWarning.setText("设置6-16个字符，请至少使用字母，数字和符号两种以上组合");
                                        editTextValidator = new EditTextValidator(getContext())
                                                .setButton(btnSend)
                                                .add(new ValidationModel(edtPwd1, new PwdValidation()))
                                                .add(new ValidationModel(edtPwd2, new PwdValidation()))
                                                .execute();
                                    }
                                }

                                @Override
                                public void onError(Exception e) {
                                    dismissWaitDialog();
                                    ToastView.makeText(getContext(), e == null || e.getMessage() == null ? "绑定失败" : e.getMessage());
                                }
                            });
                        } else {
                            if (edtPwd1.getEdtText().equals(edtPwd2.getEdtText())) {
                                bindPresenter.step3(edtEmail.getEdtText(), edtPwd2.getEdtText(), new ObserverData() {
                                    @Override
                                    public void callback(Object o) {
                                        dismissWaitDialog();
                                        UserJson userJson = LoginUtils.getUserInfo(BindActivity.this);
                                        userJson.setIs_set_password("1");

                                        if (type == BindActivity.TYPE_CHANGE_PHONE) {
                                            userJson.setPhone(userTemp);
                                            userJson.setIs_set_phone("1");
                                        } else {
                                            userJson.setEmail(userTemp);
                                            userJson.setIs_set_email("1");
                                        }

                                        LoginUtils.changeUserInfo(getContext(), userJson);
                                        EventBus.getDefault().post(new EventBusClass(EventBusClass.EVENT_LOGIN_UPDATE, userJson));
                                        ToastView.makeText(getContext(), "设置成功");
                                        finish();
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        dismissWaitDialog();
                                        ToastView.makeText(getContext(), e == null || e.getMessage() == null ? "设置失败" : e.getMessage());
                                    }
                                });
                            } else {
                                edtPwd2.setErrorInfo("密码不一致");
                                dismissWaitDialog();
                            }
                        }
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
                editTextValidator = new EditTextValidator(this)
                        .setButton(btnSend)
                        .add(new ValidationModel(edtEmail, new EmailOrPhoneValidation()))
                        .add(new ValidationModel(edtPwd, new PwdDynamicValidation()))
                        .execute();
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

                            if (type == TYPE_CHANGE_PHONE || type == TYPE_BIND_PHONE) {
                                editTextValidator = new EditTextValidator(getContext())
                                        .setButton(btnSend)
                                        .add(new ValidationModel(edtEmail, new PhoneValidation()))
                                        .add(new ValidationModel(edtPwd, new PwdDynamicValidation()))
                                        .execute();
                            } else {
                                editTextValidator = new EditTextValidator(getContext())
                                        .setButton(btnSend)
                                        .add(new ValidationModel(edtEmail, new EmailValidation()))
                                        .add(new ValidationModel(edtPwd, new PwdDynamicValidation()))
                                        .execute();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (bindPresenter != null)
                bindPresenter.onDestroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
