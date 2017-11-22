package com.jyh.kxt.user.presenter;

import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.user.json.UserJson;
import com.jyh.kxt.user.ui.ChangePwdActivity;
import com.library.bean.EventBusClass;
import com.library.widget.window.ToastView;

import org.greenrobot.eventbus.EventBus;

/**
 * 项目名:KxtProfessional
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/10/30.
 */

public class ChangePwdPresenter extends BasePresenter {
    @BindObject ChangePwdActivity activity;
    private int type;

    public ChangePwdPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void setType(int type) {
        this.type = type;
    }

    public void pwd(String oldPwd, String newPwd, String rePwd) {
        if (type == ChangePwdActivity.TYPE_SET) {
            if (newPwd.equals(rePwd)) {
                activity.showWaitDialog(null);
                LoginUtils.changePwd(this, null, null, oldPwd, getClass().getName(), new ObserverData() {
                    @Override
                    public void callback(Object o) {
                        activity.dismissWaitDialog();
                        ToastView.makeText(mContext, "设置成功");
                        UserJson userJson = LoginUtils.getUserInfo(mContext);
                        userJson.setIs_set_password("1");
                        LoginUtils.changeUserInfo(mContext, userJson);
                        EventBus.getDefault().post(new EventBusClass(EventBusClass.EVENT_LOGIN_UPDATE, userJson));
                        activity.finish();
                    }

                    @Override
                    public void onError(Exception e) {
                        activity.dismissWaitDialog();
                        ToastView.makeText(mContext, e == null || e.getMessage() == null ? "设置失败" : e.getMessage());
                    }
                });
            } else
                activity.showError("密码不一致");
        } else {

            if (newPwd.equals(rePwd)) {
                activity.showWaitDialog(null);
                LoginUtils.changePwd(this, null, oldPwd, newPwd, getClass().getName(), new ObserverData() {
                    @Override
                    public void callback(Object o) {
                        activity.dismissWaitDialog();
                        ToastView.makeText(mContext, "修改成功");
                        activity.finish();
                    }

                    @Override
                    public void onError(Exception e) {
                        activity.dismissWaitDialog();
                        ToastView.makeText(mContext, e == null || e.getMessage() == null ? "修改失败" : e.getMessage());
                    }
                });
            } else {
                activity.showError("密码不一致");
            }
        }
    }
}
