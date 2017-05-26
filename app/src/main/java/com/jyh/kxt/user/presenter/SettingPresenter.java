package com.jyh.kxt.user.presenter;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.utils.GlideCacheUtil;
import com.jyh.kxt.base.widget.night.ThemeUtil;
import com.jyh.kxt.user.ui.SettingActivity;
import com.jyh.kxt.user.ui.json.VersionJson;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;
import com.library.util.SystemUtil;
import com.library.widget.window.ToastView;
import com.trycatch.mysnackbar.Prompt;
import com.trycatch.mysnackbar.TSnackbar;

/**
 * Created by Mr'Dai on 2017/3/20.
 */

public class SettingPresenter extends BasePresenter {

    @BindObject SettingActivity mSettingActivity;

    public SettingPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }


    /**
     * 清理缓存
     */
    public void clear() {
        String cacheSize = GlideCacheUtil.getInstance().getCacheSize(mContext);
        ToastView.makeText(mContext, "成功清理" + cacheSize+"缓存");
        GlideCacheUtil.getInstance().clearImageAllCache(mContext);
    }

    /**
     * 检测版本
     */
    public void version() {
        VolleyRequest volleyRequest = new VolleyRequest(mContext, mQueue);
        JSONObject jsonParam = volleyRequest.getJsonParam();

        showWaitDialog("检查中...");
        volleyRequest.doPost(HttpConstant.VERSION_VERSION, jsonParam, new HttpListener<VersionJson>() {
            @Override
            protected void onResponse(VersionJson versionJson) {
                dismissWaitDialog();
                checkComparisonVersion(versionJson);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                dismissWaitDialog();
            }
        });
    }


    private void checkComparisonVersion(VersionJson versionJson) {

        int currentVersionCode = SystemUtil.getVersionCode(mContext);
        if (versionJson.getVersionCode() <= currentVersionCode) {

            TSnackbar.make(mSettingActivity.ivBarBreak, "当前版本已是最新", TSnackbar.LENGTH_LONG, TSnackbar
                    .APPEAR_FROM_TOP_TO_DOWN)
                    .setMinHeight(SystemUtil.getStatuBarHeight(mContext),
                            mContext.getResources().getDimensionPixelOffset(R.dimen.actionbar_height))
                    .setPromptThemBackground(Prompt.WARNING).show();
            return;
        }

        String replaceContent = versionJson.getContent().replace("<br>", "\n");


        new AlertDialog.Builder(mContext, ThemeUtil.getAlertTheme(mContext))
                .setPositiveButton("是",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Uri uri = Uri.parse("http://gdown.baidu.com/data/wisegame/0852f6d39ee2e213/QQ_676.apk");
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                mContext.startActivity(intent);
                            }
                        })
                .setNegativeButton("否",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                .setTitle("检查到最新安装包" + versionJson.getVersionName())
                .setMessage(replaceContent)
                .show();
    }
}
