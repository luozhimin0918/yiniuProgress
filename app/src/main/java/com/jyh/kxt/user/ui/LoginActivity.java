package com.jyh.kxt.user.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.custom.DiscolorButton;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.base.utils.UmengShareUI;
import com.jyh.kxt.base.utils.validator.EditTextValidator;
import com.jyh.kxt.base.utils.validator.ValidationModel;
import com.jyh.kxt.base.utils.validator.validation.PhoneValidation;
import com.jyh.kxt.base.utils.validator.validation.PwdValidation;
import com.jyh.kxt.base.utils.validator.validation.UserNameValidation;
import com.jyh.kxt.base.widget.FunctionEditText;
import com.jyh.kxt.user.presenter.LoginPresenter;
import com.library.base.http.VarConstant;
import com.library.bean.EventBusClass;
import com.library.util.SystemUtil;
import com.library.widget.tablayout.NavigationTabLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 项目名:Kxt
 * 类描述:登录界面
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/25.
 */

public class LoginActivity extends BaseActivity implements NavigationTabLayout.OnTabSelectListener {

    @BindView(R.id.sv_rootView) public ScrollView rootView;
    @BindView(R.id.iv_close) ImageView ivClose;
    @BindView(R.id.stl_navigation_bar) NavigationTabLayout stlNavigationBar;
    @BindView(R.id.fl_bg) FrameLayout flBg;
    @BindView(R.id.edt_name) public FunctionEditText edtName;
    @BindView(R.id.edt_pwd) public FunctionEditText edtPwd;
    @BindView(R.id.edt_code) FunctionEditText edtCode;
    @BindView(R.id.db_login) DiscolorButton dbLogin;

    private LoginPresenter presenter;
    private EditTextValidator editTextValidator;

    private int position;
    private boolean isShowCode = false;//验证码控件是否显示

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_anim1, R.anim.activity_out1);

        setContentView(R.layout.activity_user_login, StatusBarColor.NO_COLOR);
        presenter = new LoginPresenter(this);
        try {
            EventBus.getDefault().register(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        editTextValidator = new EditTextValidator(getContext())
                .setButton(dbLogin)
                .add(new ValidationModel(edtName, new UserNameValidation()))
                .add(new ValidationModel(edtPwd, new PwdValidation()))
                .execute();

        String[] tabs = new String[]{"密码登录", "短信登录"};

        DisplayMetrics screenDisplay = SystemUtil.getScreenDisplay(this);
        stlNavigationBar.setTabWidth(SystemUtil.px2dp(this, screenDisplay.widthPixels / 2));

        stlNavigationBar.setData(tabs);
        stlNavigationBar.setCurrentTab(0);

        stlNavigationBar.setOnTabSelectListener(this);

        edtPwd.setFunctionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position == 0) {
                    //忘记密码
                    Intent intent = new Intent(getContext(), ForgetPwdActivity.class);
                    startActivity(intent);
                } else {
                    //请求动态密码
                    LoginUtils.requestCode(presenter, VarConstant.CODE_GENERAL, true, edtName.getEdtText(), presenter
                            .getClass().getName(), new ObserverData() {
                        @Override
                        public void callback(Object o) {

                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusClass eventBus) {
        if (eventBus.fromCode == EventBusClass.EVENT_LOGIN) {
            setResult(Activity.RESULT_OK);
            onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null)
            presenter.onDestory();
        try {
            EventBus.getDefault().unregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UmengShareUI.onActivityResult(this, requestCode, resultCode, data);
    }

    private boolean isFirstChanged = true;

    @Override
    public void onTabSelect(int position, int clickId) {
        this.position = position;
        if (isFirstChanged) {
            presenter.saveData(0, edtName, edtPwd, edtCode);
            presenter.setSave();
            isFirstChanged = false;
        } else {
            presenter.saveData(position == 0 ? 1 : 0, edtName, edtPwd, edtCode);
        }

        if (position == 0) {
            edtPwd.setType(VarConstant.TYPE_FEDT_IMAGE_TEXT);
        } else {
            edtPwd.setType(VarConstant.TYPE_FEDT_TEXT);
        }
        changeValidation();
        edtPwd.reflash();
        presenter.setData(position, edtName, edtPwd, edtCode);
        edtName.requestFocus();//切换之后焦点重置
    }

    @OnClick({R.id.db_login, R.id.iv_qq, R.id.iv_sina, R.id.iv_wx, R.id.iv_close, R.id.tv_register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.db_login:
                if (editTextValidator.validate() && ((isShowCode && edtCode.isEqualsIgnoreCase(edtCode.getEdtText())) || !isShowCode)) {
                    presenter.login(dbLogin, position);
                }
                break;
            case R.id.iv_qq:
                //qq
                presenter.loginQQ();
                break;
            case R.id.iv_sina:
                //微博
                presenter.loginSina();
                break;
            case R.id.iv_wx:
                //微信
                presenter.loginWX();
                break;
            case R.id.iv_close:
                onBackPressed();
                break;
            case R.id.tv_register:
                //注册
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * 显示验证码控件
     *
     * @param errorNumAccount
     * @param errorNumPhone
     */
    public void showVerifyView(int errorNumAccount, int errorNumPhone) {
        //错误3次后显示验证码
        if (position == 0) {
            if (errorNumAccount >= 3) {
                isShowCode = true;
                edtCode.setVisibility(View.VISIBLE);
            } else {
                isShowCode = false;
                edtCode.setVisibility(View.GONE);
            }
        } else {
            if (errorNumPhone >= 3) {
                isShowCode = true;
                edtCode.setVisibility(View.VISIBLE);
            } else {
                isShowCode = false;
                edtCode.setVisibility(View.GONE);
            }
        }
        changeValidation();
    }

    /**
     * 设置倒计时
     *
     * @param time
     */
    public void setCountDown(int time) {
        if (position == 1) {
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
     * 改变验证方式
     */
    private void changeValidation() {
        if (position == 0) {
            editTextValidator = new EditTextValidator(getContext())
                    .setButton(dbLogin)
                    .add(new ValidationModel(edtName, new UserNameValidation()))
                    .add(new ValidationModel(edtPwd, new PwdValidation()))
                    .execute();
        } else {
            editTextValidator = new EditTextValidator(getContext())
                    .setButton(dbLogin)
                    .add(new ValidationModel(edtName, new PhoneValidation()))
                    .add(new ValidationModel(edtPwd, new PwdValidation()))
                    .execute();
        }
    }
}
