package com.jyh.kxt.user.ui;

import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.base.utils.GlideCacheUtil;
import com.jyh.kxt.index.json.PatchJson;
import com.jyh.kxt.user.presenter.SettingPresenter;
import com.library.util.FileUtils;
import com.library.util.SPUtils;
import com.library.util.SystemUtil;
import com.tencent.tinker.lib.tinker.Tinker;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * 设置
 */
public class SettingActivity extends BaseActivity {

    @BindView(R.id.iv_bar_break) public ImageView ivBarBreak;
    @BindView(R.id.tv_bar_title) TextView tvBarTitle;
    @BindView(R.id.iv_bar_function) TextView ivBarFunction;
    @BindView(R.id.iv_push) CheckBox ivPush;
    @BindView(R.id.rl_push) RelativeLayout rlPush;
    @BindView(R.id.iv_sound) CheckBox ivSound;
    @BindView(R.id.rl_sound) RelativeLayout rlSound;
    @BindView(R.id.rl_clear) RelativeLayout rlClear;
    @BindView(R.id.rl_version) RelativeLayout rlVersion;
    @BindView(R.id.tv_clear) TextView tvClear;
    @BindView(R.id.v_version_point) View vPoint;
    @BindView(R.id.tv_introduce_patch) TextView tvIntroducePatch;
    @BindView(R.id.tv_version_name) TextView tvVersionName;

    private SettingPresenter settingPresenter;
    private String cacheSize;

    private int clickBarTitleCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting, StatusBarColor.THEME1);


        Boolean isOpenPush = SPUtils.getBoolean(this, SpConstant.SETTING_PUSH);
        Boolean isOpenSound = SPUtils.getBoolean(this, SpConstant.SETTING_SOUND);

        ivPush.setChecked(isOpenPush);
        ivSound.setChecked(isOpenSound);

        settingPresenter = new SettingPresenter(this);
        tvBarTitle.setText("设置");

        String versionName = SystemUtil.getVersionName(this);
        tvVersionName.setText("当前版本V" + versionName);

        cacheSize = GlideCacheUtil.getInstance().getCacheSize(this);
        tvClear.setText(cacheSize);
        settingPresenter.version(true);

    }

    @OnClick({R.id.iv_bar_break, R.id.rl_push, R.id.iv_push, R.id.rl_sound, R.id.iv_sound, R.id.rl_clear, R.id
            .rl_version, R.id.tv_bar_title})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_bar_break:
                onBackPressed();
                break;
            case R.id.rl_push:
            case R.id.iv_push:
                changeBtnStatus(view, 0);
                break;
            case R.id.rl_sound:
            case R.id.iv_sound:
                changeBtnStatus(view, 1);
                break;
            case R.id.rl_clear:
                settingPresenter.clear(cacheSize);
                tvClear.setText("0.00KB");
                break;
            case R.id.rl_version:
                settingPresenter.version(false);
                break;
            case R.id.tv_bar_title:
                clickBarTitleCount++;
                if (clickBarTitleCount >= 5) {
                    showPatchDialog();
                }
                break;
        }
    }

    /**
     * 改变按钮状态
     *
     * @param view
     * @param type
     */
    public void changeBtnStatus(View view, int type) {
        switch (type) {
            case 0:
                boolean checkedPush = view instanceof CheckBox ? ivPush.isChecked() : !ivPush.isChecked();
                ivPush.setChecked(checkedPush);
                SPUtils.save(this, SpConstant.SETTING_PUSH, checkedPush);
                break;
            case 1:
                boolean checkedSound = view instanceof CheckBox ? ivSound.isChecked() : !ivSound.isChecked();
                ivSound.setChecked(checkedSound);
                SPUtils.save(this, SpConstant.SETTING_SOUND, checkedSound);
                break;
        }
    }

    public void changeVersionPointStatus(boolean isShow) {
        vPoint.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getQueue().cancelAll(SettingPresenter.class.getName());
    }

    /**
     * 显示补丁相关信息
     */
    private void showPatchDialog() {
        CharSequence[] charSequences = {"查看补丁信息", "清空本地补丁", "清空已应用补丁", "SP文件"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("关闭")
                .setIcon(R.drawable.ic_launcher)
                .setItems(charSequences, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tvIntroducePatch.setVisibility(View.VISIBLE);
                        switch (which) {
                            case 0:
                                StringBuilder sb = new StringBuilder();
                                Tinker tinker = Tinker.with(getApplicationContext());

                                PackageManager packageManager = SettingActivity.this.getPackageManager();
                                if (packageManager != null) {
                                    try {
                                        ApplicationInfo applicationInfo;
                                        applicationInfo = packageManager.getApplicationInfo(
                                                SettingActivity.this.getPackageName(),
                                                PackageManager.GET_META_DATA);

                                        if (applicationInfo != null) {
                                            if (applicationInfo.metaData != null) {
                                                String resultData = applicationInfo.metaData.getString
                                                        ("UMENG_CHANNEL");
                                                if (resultData == null) {
                                                    sb.append("来源渠道: 测试包 \n");
                                                } else {
                                                    if ("360".equals(resultData)) {
                                                        sb.append(resultData+" 来源渠道: 360应用市场 \n");
                                                    } else {
                                                        sb.append(resultData+" 来源渠道: 普通应用市场 \n");
                                                    }
                                                }
                                            }
                                        }
                                    } catch (PackageManager.NameNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                }
                                if (tinker.isTinkerLoaded()) {

                                    sb.append("[补丁应用内容:] \n");

                                    String romSpace = "[占用 Rom Space] " + tinker.getTinkerRomSpace() + " k \n";
                                    sb.append(romSpace);

                                    try {
                                        String patchInfo = SPUtils.getString(SettingActivity.this, SpConstant.PATCH_INFO);
                                        if (patchInfo != null) {
                                            PatchJson patchJson = JSONObject.parseObject(patchInfo, PatchJson.class);
                                            sb.append(String.format("[补丁描述] %s \n", patchJson.getDescription()));
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    sb.append(String.format("[暂无补丁] \n"));
                                }

                                tvIntroducePatch.setText(sb.toString());
                                break;
                            case 1:
                                String saveFilePath = FileUtils.getVersionNameFilePath(SettingActivity.this);
                                File patchFile = new File(saveFilePath);
                                String[] list = patchFile.list();

                                StringBuilder sbt = new StringBuilder();
                                for (String s : list) {
                                    sbt.append(s);
                                }

                                for (File itemPatchFile : patchFile.listFiles()) {
                                    itemPatchFile.delete();
                                }
                                tvIntroducePatch.setText("删除本地文件下的:" + sbt.toString());
                                break;
                            case 2:
                                Tinker.with(getApplicationContext()).cleanPatch();
                                tvIntroducePatch.setText("已清除这个版本已经应用的补丁 ");
                                break;
                            case 3:
                                String patchPath = SPUtils.getString(SettingActivity.this, SpConstant.PATCH_PATH);
                                try {
                                    tvIntroducePatch.setText(patchPath);
                                } catch (Exception e) {
                                    tvIntroducePatch.setText("解析错误");
                                }
                                break;

                        }
                        dialog.dismiss();
                    }
                }).show();
    }
}
