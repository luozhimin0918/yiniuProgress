package com.jyh.kxt.trading.presenter;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.trading.ui.ViewpointFragment;
import com.jyh.kxt.user.ui.LoginOrRegisterActivity;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/7/26.
 */

public class ViewpointPresenter extends BasePresenter {

    @BindObject ViewpointFragment fragment;
    private AlertDialog loginPop;

    public ViewpointPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void showLoginDialog() {
        if (loginPop == null) {
            loginPop = new AlertDialog.Builder(mContext)
                    .setTitle("提醒")
                    .setMessage("请先登录")
                    .setNegativeButton("登录", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mContext.startActivity(new Intent(mContext, LoginOrRegisterActivity.class));
                        }
                    }).setPositiveButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create();
        }
        if (loginPop.isShowing()) {
            loginPop.dismiss();
        }
        loginPop.show();
    }
}
