package com.jyh.kxt.user.presenter;

import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.user.ui.ChangePwdActivity;

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
            if (oldPwd.equals(rePwd))
                LoginUtils.changePwd(this, null, null, oldPwd, getClass().getName(), new ObserverData() {
                    @Override
                    public void callback(Object o) {

                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
            else
                activity.showError("密码不一致");
        } else {

            if (newPwd.equals(rePwd))
                LoginUtils.changePwd(this, null, oldPwd, newPwd, getClass().getName(), new ObserverData() {
                    @Override
                    public void callback(Object o) {

                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
            else {
                activity.showError("密码不一致");
            }
        }
    }
}
