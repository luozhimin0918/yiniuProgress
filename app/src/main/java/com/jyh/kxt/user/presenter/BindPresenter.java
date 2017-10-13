package com.jyh.kxt.user.presenter;

import android.app.Activity;
import android.support.v4.content.ContextCompat;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.node.BinaryNode;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.widget.FunctionEditText;
import com.jyh.kxt.user.ui.BindActivity;
import com.library.base.http.VarConstant;


/**
 * 项目名:KxtProfessional
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/10/24.
 */

public class BindPresenter extends BasePresenter {

    @BindObject BindActivity activity;
    private int type;
    private JSONObject step1Json, step2Json;

    public BindPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void setType(int type) {
        this.type = type;
        initData();
    }

    private void initData() {
        step1Json = new JSONObject();
        step2Json = new JSONObject();
        switch (type) {
            case BindActivity.TYPE_BIND_PHONE:
                step1Json.put("warning", "绑定后可直接使用手机号登录");
                step1Json.put("hint1", "请输入手机号");
                step1Json.put("hint2", "请输入动态密码");
                step1Json.put("functionColor", ContextCompat.getColor(mContext, R.color.font_color1));
                step1Json.put("function", "获取动态码");
                step1Json.put("type2", VarConstant.TYPE_FEDT_TEXT);
                step1Json.put("btn", "完成绑定");
                break;
            case BindActivity.TYPE_BIND_EMAIL:
                step1Json.put("warning", "绑定后可直接使用邮箱登录");
                step1Json.put("hint1", "请输入邮箱");
                step1Json.put("hint2", "请输入动态密码");
                step1Json.put("functionColor", ContextCompat.getColor(mContext, R.color.font_color1));
                step1Json.put("function", "获取动态码");
                step1Json.put("type2", VarConstant.TYPE_FEDT_TEXT);
                step1Json.put("btn", "完成绑定");
                break;
            case BindActivity.TYPE_CHANGE_PHONE:
                step1Json.put("warning", "如需换绑请先完成身份验证");
                step1Json.put("hint1", "请输入手机号或邮箱");
                step1Json.put("hint2", "请输入动态密码");
                step1Json.put("functionColor", ContextCompat.getColor(mContext, R.color.font_color1));
                step1Json.put("function", "获取动态码");
                step1Json.put("type2", VarConstant.TYPE_FEDT_TEXT);
                step1Json.put("btn", "立即验证");

                step2Json.put("warning", "绑定后可直接使用手机号登录");
                step2Json.put("hint1", "请输入手机号");
                step2Json.put("hint2", "请输入动态密码");
                step2Json.put("functionColor", ContextCompat.getColor(mContext, R.color.font_color1));
                step2Json.put("function", "获取动态码");
                step2Json.put("type2", VarConstant.TYPE_FEDT_TEXT);
                step2Json.put("btn", "完成绑定");

                break;
            case BindActivity.TYPE_CHANGE_EMAIL:
                step1Json.put("warning", "如需换绑请先完成身份验证");
                step1Json.put("hint1", "请输入手机号或邮箱");
                step1Json.put("hint2", "请输入动态密码");
                step1Json.put("functionColor", ContextCompat.getColor(mContext, R.color.font_color1));
                step1Json.put("function", "获取动态码");
                step1Json.put("type2", VarConstant.TYPE_FEDT_TEXT);
                step1Json.put("btn", "立即验证");

                step2Json.put("warning", "绑定后可直接使用邮箱登录");
                step2Json.put("hint1", "请输入邮箱");
                step2Json.put("hint2", "请输入动态密码");
                step2Json.put("functionColor", ContextCompat.getColor(mContext, R.color.font_color1));
                step2Json.put("function", "获取动态码");
                step2Json.put("type2", VarConstant.TYPE_FEDT_TEXT);
                step2Json.put("btn", "完成绑定");
                break;
            default:
                break;
        }
        activity.initView();
    }

    public void saveData(int step, String warning, String edt1Str, String edt2Str, String edt2Function, String btn, int edt2Type, int
            functionColor) {
        switch (type) {
            case BindActivity.TYPE_CHANGE_PHONE:
            case BindActivity.TYPE_CHANGE_EMAIL:
                if(step==1){
                    step1Json.put("warning",warning);
                    step1Json.put("edt1",edt1Str);
                    step1Json.put("edt2",edt2Str);
                    step1Json.put("function",edt2Function);
                    step1Json.put("functionColor",functionColor);
                    step1Json.put("btn",btn);
                    step1Json.put("type2",edt2Type);
                }else if(step==2){
                    step2Json.put("warning",warning);
                    step2Json.put("edt1",edt1Str);
                    step2Json.put("edt2",edt2Str);
                    step2Json.put("function",edt2Function);
                    step2Json.put("functionColor",functionColor);
                    step2Json.put("btn",btn);
                    step2Json.put("type2",edt2Type);
                }
                break;
            default:
                break;
        }
    }

    public JSONObject getStep1Json() {
        return step1Json;
    }

    public JSONObject getStep2Json() {
        return step2Json;
    }

    public void step1(String edtText, String edtText1, ObserverData observerData) {
        observerData.callback(null);
    }

    public void step2(String edtText, String edtText1, ObserverData observerData) {
        observerData.callback(null);
    }

    public void step3(String edtText, String edtText1, ObserverData observerData) {
        observerData.callback(null);
    }
}
