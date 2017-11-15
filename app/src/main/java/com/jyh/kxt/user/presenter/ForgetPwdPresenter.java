package com.jyh.kxt.user.presenter;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.ContextCompat;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.user.ui.ForgetPwdActivity;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.util.EncryptionUtils;
import com.library.util.SystemUtil;
import com.trycatch.mysnackbar.Prompt;
import com.trycatch.mysnackbar.TSnackbar;

import java.util.HashMap;
import java.util.Map;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/27.
 */

public class ForgetPwdPresenter extends BasePresenter {

    @BindObject ForgetPwdActivity forgetPwdActivity;
    private VolleyRequest request;
    private TSnackbar snackBar;

    private int step=1;//步骤
    private JSONObject stepOneJson,stepTwoJson;

    public ForgetPwdPresenter(IBaseView iBaseView) {
        super(iBaseView);
        init();
    }

    private void init() {
        stepOneJson=new JSONObject();
        stepTwoJson=new JSONObject();
        stepOneJson.put("warning","请先验证身份");
        stepTwoJson.put("warning","设置6-16个字符，请至少使用字母、数字和符号两种以上组合");
        stepOneJson.put("edt1hint","请输入注册的手机号或邮箱");
        stepTwoJson.put("edt1hint","请输入新密码");
        stepOneJson.put("edt2hint","请输入动态密码");
        stepTwoJson.put("edt2hint","确认密码");
        stepOneJson.put("edt1Type",VarConstant.TYPE_FEDT_DEFALUT);
        stepTwoJson.put("edt1Type",VarConstant.TYPE_FEDT_IMAGE);
        stepOneJson.put("edt2Type",VarConstant.TYPE_FEDT_TEXT);
        stepTwoJson.put("edt2Type",VarConstant.TYPE_FEDT_IMAGE);
    }

    /**
     * 保存输入数据
     * @param step 步骤
     * @param edt1String 输入框1文本
     * @param edt2String 输入框2文本
     * @param functionString 输入框2功能文本
     * @param edt1Open 输入框1密码是否隐蔽
     * @param edt2Open 输入框2密码是否隐蔽
     */
    public void saveData(int step,String edt1String,String edt2String,String functionString,int functionColor,boolean edt1Open,boolean edt2Open){
        this.step=step;
        if(step==1){
            stepOneJson.put("edt1Text",edt1String);
            stepOneJson.put("edt2Text",edt2String);
            stepOneJson.put("function",functionString);
            stepOneJson.put("functionColor",functionColor);
            stepOneJson.put("edt1open",edt1Open);
            stepOneJson.put("edt2open",edt2Open);
        }else{
            stepTwoJson.put("edt1Text",edt1String);
            stepTwoJson.put("edt2Text",edt2String);
            stepTwoJson.put("edt1open",edt1Open);
            stepTwoJson.put("edt2open",edt2Open);
        }
    }

    public void sendInfo(String email) {
        if (request == null) {
            request = new VolleyRequest(mContext, mQueue);
            request.setTag(getClass().getName());
        }
        Map map = new HashMap();
        JSONObject jsonObject = request.getJsonParam();
        jsonObject.put(VarConstant.HTTP_EMAIL, email);
        try {
            map.put(VarConstant.HTTP_CONTENT2, EncryptionUtils.createJWT(VarConstant.KEY, jsonObject.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        snackBar = TSnackbar.make(forgetPwdActivity.ivBarBreak, "邮件发送中", TSnackbar.LENGTH_INDEFINITE, TSnackbar.APPEAR_FROM_TOP_TO_DOWN);
        snackBar.setPromptThemBackground(Prompt.SUCCESS);
        snackBar.addIconProgressLoading(0, true, false);
        snackBar.show();
        request.doPost(HttpConstant.USER_FORGET, map, new HttpListener<Object>() {
            @Override
            protected void onResponse(Object o) {
                snackBar.setPromptThemBackground(Prompt.SUCCESS).setText("邮件发送成功,请前往查看密码").setDuration(TSnackbar.LENGTH_LONG)
                        .setMinHeight(SystemUtil.getStatuBarHeight(mContext), mContext.getResources()
                                .getDimensionPixelOffset(R.dimen.actionbar_height)).show();
                forgetPwdActivity.finish();
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                String msg = "";
                if (error != null && error.getMessage() != null) {
                    msg = error.getMessage();
                }
                snackBar.setPromptThemBackground(Prompt.ERROR).setText(msg == null ? "邮件发送失败" : msg).setDuration(TSnackbar.LENGTH_LONG)
                        .setMinHeight(SystemUtil.getStatuBarHeight(mContext), mContext.getResources()
                                .getDimensionPixelOffset(R.dimen.actionbar_height)).show();
            }
        });
        forgetPwdActivity.dismissWaitDialog();
    }

    public JSONObject getStepOneJson() {
        return stepOneJson;
    }

    public JSONObject getStepTwoJson() {
        return stepTwoJson;
    }

    public void requestPwd() {
        if (canRequestPwd) {
            Message message = new Message();
            message.arg1 = 60;
            message.what = 1;
            handler.sendMessage(message);
            LoginUtils.requestCode(this, VarConstant.CODE_VERIFY,forgetPwdActivity.edtEmail.getEdtText(), getClass().getName(), new
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

    private boolean canRequestPwd = true;//是否可以请求
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
                    forgetPwdActivity.setCountDown(message.arg1);
                    stepOneJson.put("pwd_function_txt", message.arg1 + "秒后重发");
                    stepOneJson.put("pwd_function_txt_color", ContextCompat.getColor(mContext, R.color.font_color9));
                } else {
                    canRequestPwd = true;
                    forgetPwdActivity.setCountDown(msg.arg1);
                    stepOneJson.put("pwd_function_txt", "获取动态码");
                    stepOneJson.put("pwd_function_txt_color", ContextCompat.getColor(mContext, R.color.font_color1));
                }
            }
            return false;
        }
    });

    public void onDestroy(){

        handler.removeCallbacksAndMessages(null);
        handler=null;

    }
}
