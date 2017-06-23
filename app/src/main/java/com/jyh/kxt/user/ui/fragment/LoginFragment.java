package com.jyh.kxt.user.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.util.validation.PwdValidation;
import com.jyh.kxt.base.util.validation.UserNameValidation;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.base.utils.MarketConnectUtil;
import com.jyh.kxt.base.utils.UmengLoginTool;
import com.jyh.kxt.base.widget.LineEditText;
import com.jyh.kxt.user.json.UserJson;
import com.jyh.kxt.user.ui.ForgetPwdActivity;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.util.EncryptionUtils;
import com.library.util.NetUtils;
import com.library.util.SystemUtil;
import com.library.util.avalidations.EditTextValidator;
import com.library.util.avalidations.ValidationModel;
import com.library.widget.window.ToastView;
import com.trycatch.mysnackbar.Prompt;
import com.trycatch.mysnackbar.TSnackbar;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 项目名:Kxt
 * 类描述:登录
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/25.
 */

public class LoginFragment extends BaseFragment {

    @BindView(R.id.edt_email) LineEditText edtEmail;
    @BindView(R.id.tv_error_email) TextView tvErrorEmail;
    @BindView(R.id.edt_pwd) LineEditText edtPwd;
    @BindView(R.id.tv_error_pwd) TextView tvErrorPwd;

    @BindView(R.id.db_login) Button btnLogin;

    private VolleyRequest request;

    private EditTextValidator editTextValidator;
    private TSnackbar snackBar;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_login);

        editTextValidator = new EditTextValidator(getContext())
                .setButton(btnLogin)
                .add(new ValidationModel(edtEmail, tvErrorEmail, new UserNameValidation()))
                .add(new ValidationModel(edtPwd, tvErrorPwd, new PwdValidation()))
                .execute();
    }

    @OnClick({R.id.tv_forgetPwd, R.id.db_login, R.id.iv_qq, R.id.iv_sina, R.id.iv_wx})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_forgetPwd:
                //忘记密码
                Intent intent = new Intent(getContext(), ForgetPwdActivity.class);
                startActivity(intent);
                break;
            case R.id.db_login:
                //登录
                if (editTextValidator.validate())
                    login();
                break;
            case R.id.iv_qq:
                //qq
                if (NetUtils.isNetworkAvailable(getContext()))
                    if (UMShareAPI.get(getContext()).isInstall(getActivity(), SHARE_MEDIA.QQ))
                        UmengLoginTool.umenglogin((BaseActivity) getActivity(), SHARE_MEDIA.QQ);
                    else
                        ToastView.makeText3(getContext(), "未安装QQ");
                else
                    ToastView.makeText3(getContext(), "暂无网络,请稍后再试");
                break;
            case R.id.iv_sina:
                //新浪
                if (NetUtils.isNetworkAvailable(getContext()))
                    UmengLoginTool.umenglogin((BaseActivity) getActivity(), SHARE_MEDIA.SINA);
                else
                    ToastView.makeText3(getContext(), "暂无网络,请稍后再试");
                break;
            case R.id.iv_wx:
                //微信
                if (NetUtils.isNetworkAvailable(getContext()))
                    if (UMShareAPI.get(getContext()).isInstall(getActivity(), SHARE_MEDIA.WEIXIN))
                        UmengLoginTool.umenglogin((BaseActivity) getActivity(), SHARE_MEDIA.WEIXIN);
                    else
                        ToastView.makeText3(getContext(), "未安装微信");
                else
                    ToastView.makeText3(getContext(), "暂无网络,请稍后再试");
                break;
        }
    }

    private void login() {
        snackBar = TSnackbar.make(btnLogin, "登录中...", TSnackbar.LENGTH_INDEFINITE, TSnackbar.APPEAR_FROM_TOP_TO_DOWN);
        snackBar.setPromptThemBackground(Prompt.SUCCESS);
        snackBar.addIconProgressLoading(0, true, false);
        snackBar.show();

        if (request == null) {
            request = new VolleyRequest(getContext(), getQueue());
            request.setTag(getClass().getName());
        }
        request.doPost(HttpConstant.USER_LOGIN2, getMap(), new HttpListener<UserJson>() {
            @Override
            protected void onResponse(UserJson user) {
                snackBar.setPromptThemBackground(Prompt.SUCCESS).setText("登录成功").setDuration(TSnackbar.LENGTH_LONG)
                        .setMinHeight(SystemUtil.getStatuBarHeight(getContext()), getResources()
                                .getDimensionPixelOffset(R.dimen.actionbar_height)).show();
                LoginUtils.login(getContext(), user);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                snackBar.setPromptThemBackground(Prompt.ERROR).setText(NetUtils.isNetworkAvailable(getContext()) ? (error == null ? "" :
                        error.getMessage()) : "暂无网络,请稍后再试").setDuration(TSnackbar.LENGTH_LONG)
                        .setMinHeight(SystemUtil.getStatuBarHeight(getContext()), getResources()
                                .getDimensionPixelOffset(R.dimen.actionbar_height)).show();
            }
        });
    }

    private Map getMap() {
        Map map = new HashMap();
        JSONObject jsonParam = request.getJsonParam();
        jsonParam.put(VarConstant.HTTP_USERNAME, edtEmail.getText().toString());
        jsonParam.put(VarConstant.HTTP_PWD, edtPwd.getText().toString());
        try {
            map.put(VarConstant.HTTP_CONTENT2, EncryptionUtils.createJWT(VarConstant.KEY, jsonParam.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getQueue().cancelAll(getClass().getName());
    }
}
