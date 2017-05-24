package com.jyh.kxt.user.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.widget.night.ThemeUtil;
import com.jyh.kxt.index.ui.WebActivity;
import com.jyh.kxt.user.ui.json.VersionJson;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;
import com.library.util.SystemUtil;
import com.trycatch.mysnackbar.Prompt;
import com.trycatch.mysnackbar.TSnackbar;

import butterknife.BindView;
import butterknife.OnClick;

import static com.taobao.accs.ACCSManager.mContext;

/**
 * 项目名:Kxt
 * 类描述:关于
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/14.
 */

public class AboutActivity extends BaseActivity {

    @BindView(R.id.iv_bar_break) ImageView ivBarBreak;
    @BindView(R.id.tv_bar_title) TextView tvBarTitle;
    @BindView(R.id.iv_bar_function) TextView ivBarFunction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_about, StatusBarColor.THEME1);

        ivBarBreak.setImageResource(R.mipmap.ico_break);
        tvBarTitle.setText("关于我们");
        ivBarFunction.setVisibility(View.INVISIBLE);

    }

    @OnClick({R.id.iv_bar_break, R.id.rl_statement, R.id.rl_contact, R.id.rl_feedback, R.id.rl_visit, R.id
            .rl_version_update})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_bar_break:
                onBackPressed();
                break;
            case R.id.rl_statement:
                //声明条款
                Intent intent = new Intent(this, StatementActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;
            case R.id.rl_contact:
                //联系我们
                Intent contactIntent = new Intent(this, WebActivity.class);
                contactIntent.putExtra(IntentConstant.NAME, "联系我们");
                contactIntent.putExtra(IntentConstant.WEBURL, HttpConstant.CONTACT_US_URL);
                startActivity(contactIntent);
                break;
            case R.id.rl_feedback:
                //意见反馈
                Intent feedbackIntent = new Intent(this, WebActivity.class);
                feedbackIntent.putExtra(IntentConstant.NAME, "意见反馈");
                feedbackIntent.putExtra(IntentConstant.WEBURL, HttpConstant.FEEDBACK_URL);
                startActivity(feedbackIntent);
                break;
            case R.id.rl_visit:
                //访问官网
                Intent intent3 = new Intent(Intent.ACTION_VIEW);
                intent3.setData(Uri.parse(HttpConstant.OFFICIAL));
                startActivity(intent3);
                break;
            case R.id.rl_version_update:
                requestVersionUpgrade();
                break;
        }
    }

    /**
     * 请求版本升级
     */
    private void requestVersionUpgrade() {
        VolleyRequest volleyRequest = new VolleyRequest(this, mQueue);
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

        int currentVersionCode = SystemUtil.getVersionCode(this);
        if (versionJson.getVersionCode() <= currentVersionCode) {

            TSnackbar.make(tvBarTitle, "当前版本已是最新", TSnackbar.LENGTH_LONG, TSnackbar.APPEAR_FROM_TOP_TO_DOWN)
                    .setMinHeight(SystemUtil.getStatuBarHeight(mContext),
                            mContext.getResources().getDimensionPixelOffset(R.dimen.actionbar_height))
                    .setPromptThemBackground(Prompt.WARNING).show();
            return;
        }

        String replaceContent = versionJson.getContent().replace("<br>", "\n");


        new AlertDialog.Builder(AboutActivity.this, ThemeUtil.getAlertTheme(AboutActivity.this))
                .setPositiveButton("是",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Uri uri = Uri.parse("http://gdown.baidu.com/data/wisegame/0852f6d39ee2e213/QQ_676.apk");
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
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
