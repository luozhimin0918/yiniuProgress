package com.jyh.kxt.user.presenter;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.base.utils.UmengLoginTool;
import com.jyh.kxt.base.widget.FunctionEditText;
import com.jyh.kxt.user.json.UserJson;
import com.jyh.kxt.user.ui.LoginActivity;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.util.NetUtils;
import com.library.util.SystemUtil;
import com.library.widget.window.ToastView;
import com.trycatch.mysnackbar.Prompt;
import com.trycatch.mysnackbar.TSnackbar;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 * 项目名:KxtProfessional
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/10/12.
 */

public class LoginPresenter extends BasePresenter {

    public static final String TYPE_ACCOUNT = "1";//账号登录
    public static final String TYPE_PHONE = "2";//短信登录

    @BindObject LoginActivity activity;
    private TSnackbar snackBar;

    private int errorNumAccount;//账号密码登录错误次数
    private int errorNumPhone;//手机短信登录错误次数

    public LoginPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    /**
     * QQ登录
     */
    public void loginQQ() {
        if (NetUtils.isNetworkAvailable(activity))
            if (UMShareAPI.get(activity).isInstall(activity, SHARE_MEDIA.QQ))
                UmengLoginTool.umenglogin(activity, SHARE_MEDIA.QQ);
            else
                ToastView.makeText3(activity, "未安装QQ");
        else
            ToastView.makeText3(activity, "暂无网络,请稍后再试");
    }

    /**
     * 微博登录
     */
    public void loginSina() {
        if (NetUtils.isNetworkAvailable(activity))
            UmengLoginTool.umenglogin(activity, SHARE_MEDIA.SINA);
        else
            ToastView.makeText3(activity, "暂无网络,请稍后再试");
    }

    /**
     * 微信登录
     */
    public void loginWX() {
        if (NetUtils.isNetworkAvailable(activity))
            if (UMShareAPI.get(activity).isInstall(activity, SHARE_MEDIA.WEIXIN))
                UmengLoginTool.umenglogin(activity, SHARE_MEDIA.WEIXIN);
            else
                ToastView.makeText3(activity, "未安装微信");
        else
            ToastView.makeText3(activity, "暂无网络,请稍后再试");
    }

    /**
     * 账号密码登录
     *
     * @param view
     * @param position 0 账号密码登录  1 手机短信登录
     */
    public void login(View view, final int position) {
        snackBar = TSnackbar.make(view, "登录中...", TSnackbar.LENGTH_INDEFINITE, TSnackbar.APPEAR_FROM_TOP_TO_DOWN);
        snackBar.setPromptThemBackground(Prompt.SUCCESS);
        snackBar.addIconProgressLoading(0, true, false);
        snackBar.show();

        LoginUtils.requestLogin(this, activity.edtName.getEdtText(), activity.edtPwd.getEdtText(), position == 0 ? VarConstant
                .LOGIN_TYPE_DEFAULT : VarConstant.LOGIN_TYPE_CODE, getClass().getName(), new ObserverData<UserJson>() {

            @Override
            public void callback(UserJson userJson) {
                errorNumAccount = 0;
                errorNumPhone = 0;
                snackBar.setPromptThemBackground(Prompt.SUCCESS).setText("登录成功").setDuration(TSnackbar.LENGTH_LONG)
                        .setMinHeight(SystemUtil.getStatuBarHeight(activity), activity.getResources()
                                .getDimensionPixelOffset(R.dimen.actionbar_height)).show();
                LoginUtils.login(activity, userJson);
            }

            @Override
            public void onError(Exception e) {
                snackBar.setPromptThemBackground(Prompt.ERROR).setText(NetUtils.isNetworkAvailable(activity) ? (e == null ? "" :
                        e.getMessage()) : "暂无网络,请稍后再试").setDuration(TSnackbar.LENGTH_LONG)
                        .setMinHeight(SystemUtil.getStatuBarHeight(activity), activity.getResources()
                                .getDimensionPixelOffset(R.dimen.actionbar_height)).show();

                if (position == 0) {
                    errorNumAccount++;
                } else {
                    errorNumPhone++;
                }
                activity.showVerifyView(errorNumAccount, errorNumPhone);
            }
        });
    }

    private JSONObject accountSave = new JSONObject();
    private JSONObject phoneSave = new JSONObject();

    /**
     * 保存状态
     *
     * @param position
     * @param edtName
     * @param edtPwd
     * @param edtCode
     */
    public void saveData(int position, FunctionEditText edtName, FunctionEditText edtPwd, FunctionEditText edtCode) {
        if (position == 0) {
            accountSave.put("name", edtName.getEdt().getText());
            accountSave.put("name_hint", edtName.getEdt().getHint());
            accountSave.put("pwd", edtPwd.getEdt().getText());
            accountSave.put("pwd_hint", edtPwd.getEdt().getHint());
            accountSave.put("code", edtCode.getEdt().getText());
            accountSave.put("code_hint", edtCode.getEdt().getHint());
            accountSave.put("pwd_status", edtPwd.isShowPwd());
            accountSave.put("pwd_function_txt", edtPwd.getFunctionText());
            accountSave.put("pwd_function_txt_color", edtPwd.getFunctionTextColor());
            accountSave.put("code_status",edtCode.getVisibility());
        } else {
            phoneSave.put("name", edtName.getEdt().getText());
            phoneSave.put("name_hint", edtName.getEdt().getHint());
            phoneSave.put("pwd", edtPwd.getEdt().getText());
            phoneSave.put("pwd_hint", edtPwd.getEdt().getHint());
            phoneSave.put("code", edtCode.getEdt().getText());
            phoneSave.put("code_hint", edtCode.getEdt().getHint());
            phoneSave.put("pwd_status", true);
            phoneSave.put("pwd_function_txt", edtPwd.getFunctionText());
            phoneSave.put("pwd_function_txt_color", edtPwd.getFunctionTextColor());
            phoneSave.put("code_status",edtCode.getVisibility());
        }
    }

    /**
     * 恢复状态
     *
     * @param position
     * @param edtName
     * @param edtPwd
     * @param edtCode
     */
    public void setData(int position, FunctionEditText edtName, FunctionEditText edtPwd, FunctionEditText edtCode) {
        if (position == 0) {
            edtName.setEdtText(accountSave.getString("name"));
            edtCode.setEdtText(accountSave.getString("code"));
            edtPwd.setEdtText(accountSave.getString("pwd"));
            edtName.setHintText(accountSave.getString("name_hint"));
            edtCode.setHintText(accountSave.getString("code_hint"));
            edtPwd.setHintText(accountSave.getString("pwd_hint"));
            edtPwd.setShowPwd(accountSave.getBoolean("pwd_status") == null ? false : accountSave.getBoolean("pwd_status"));
            edtPwd.setFunctionText(accountSave.getString("pwd_function_txt"));
            edtPwd.setFunctionTextColor(accountSave.getInteger("pwd_function_txt_color") == null ? 0 : accountSave.getInteger
                    ("pwd_function_txt_color"));
            edtPwd.setShowTxtLine(accountSave.getBoolean("showline") == null ? false : accountSave.getBoolean("showline"));
            edtCode.setVisibility(accountSave.getInteger("code_status") == View.VISIBLE ? View.VISIBLE : View.GONE);
        } else {
            edtName.setEdtText(phoneSave.getString("name"));
            edtCode.setEdtText(phoneSave.getString("code"));
            edtPwd.setEdtText(phoneSave.getString("pwd"));
            edtName.setHintText(phoneSave.getString("name_hint"));
            edtCode.setHintText(phoneSave.getString("code_hint"));
            edtPwd.setHintText(phoneSave.getString("pwd_hint"));
            edtPwd.setShowTxtLine(true);
            edtPwd.setShowPwd(true);
            edtPwd.setFunctionText(phoneSave.getString("pwd_function_txt"));
            edtPwd.setFunctionTextColor(phoneSave.getInteger("pwd_function_txt_color") == null ? 0 : phoneSave.getInteger
                    ("pwd_function_txt_color"));
            edtPwd.setShowTxtLine(phoneSave.getBoolean("showline") == null ? false : phoneSave.getBoolean("showline"));
            edtCode.setVisibility(phoneSave.getInteger("code_status") == View.VISIBLE ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 初始化第二页面输入框数据
     */
    public void setSave() {
        phoneSave.put("name_hint", "请输入手机号");
        phoneSave.put("pwd_hint", "请输入动态密码");
        phoneSave.put("code_hint", "请输入验证码");
        phoneSave.put("showline", true);
        phoneSave.put("pwd_function_txt", "获取动态码");
        phoneSave.put("pwd_function_txt_color", ContextCompat.getColor(mContext, R.color.font_color1));
        phoneSave.put("code_status",View.GONE);
    }

    private boolean canRequestPwd = true;//是否可以请求

    /**
     * 请求动态密码
     */
    public void requestPwd() {
        if (canRequestPwd) {
            Message message = new Message();
            message.arg1 = 60;
            message.what = 1;
            handler.sendMessage(message);
            LoginUtils.requestCode(this, VarConstant.CODE_GENERAL,activity.edtName.getEdtText(), getClass().getName(), new
                    ObserverData() {
                        @Override
                        public void callback(Object o) {

                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
        }
    }

    Handler handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 1) {
                //用于显示倒计时
                if (msg.arg1 > 0) {
                    canRequestPwd = false;
                    Message message = new Message();
                    message.arg1 = msg.arg1 - 1;
                    message.what = 1;
                    handler.sendMessageDelayed(message, 1000);
                    activity.setCountDown(message.arg1);
                    phoneSave.put("pwd_function_txt", message.arg1 + "秒后重发");
                    phoneSave.put("pwd_function_txt_color", ContextCompat.getColor(mContext, R.color.font_color9));
                } else {
                    canRequestPwd = true;
                    activity.setCountDown(msg.arg1);
                    phoneSave.put("pwd_function_txt", "获取动态码");
                    phoneSave.put("pwd_function_txt_color", ContextCompat.getColor(mContext, R.color.font_color1));
                }
            }
            return false;
        }
    });

    public void onDestory() {
        handler.removeCallbacksAndMessages(null);
        handler = null;
    }
}
