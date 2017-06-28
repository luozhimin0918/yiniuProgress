package com.jyh.kxt.user.ui;

import android.content.DialogInterface;
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
import com.jyh.kxt.base.tinker.app.BaseBuildInfo;
import com.jyh.kxt.base.tinker.app.BuildInfo;
import com.jyh.kxt.base.utils.GlideCacheUtil;
import com.jyh.kxt.index.json.PatchJson;
import com.jyh.kxt.user.presenter.SettingPresenter;
import com.library.util.FileUtils;
import com.library.util.SPUtils;
import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.loader.shareutil.ShareConstants;
import com.tencent.tinker.loader.shareutil.ShareTinkerInternals;

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
                cacheSize = GlideCacheUtil.getInstance().getCacheSize(this);
                tvClear.setText(cacheSize);
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
        vPoint.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
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
                                if (tinker.isTinkerLoaded()) {
                                    sb.append(String.format("[patch is loaded] \n"));
                                    sb.append(String.format("[buildConfig TINKER_ID] %s \n", BuildInfo.TINKER_ID));
                                    sb.append(String.format("[buildConfig BASE_TINKER_ID] %s \n", BaseBuildInfo
                                            .BASE_TINKER_ID));

                                    sb.append(String.format("[buildConfig MESSSAGE] %s \n", BuildInfo.MESSAGE));
                                    sb.append(String.format("[TINKER_ID] %s \n", tinker.getTinkerLoadResultIfPresent
                                            ().getPackageConfigByName
                                            (ShareConstants.TINKER_ID)));
                                    sb.append(String.format("[packageConfig patchMessage] %s \n", tinker
                                            .getTinkerLoadResultIfPresent().getPackageConfigByName("patchMessage")));
                                    sb.append(String.format("[TINKER_ID Rom Space] %d k \n", tinker.getTinkerRomSpace
                                            ()));
                                } else {
                                    sb.append(String.format("[patch is not loaded] \n"));
                                    sb.append(String.format("[buildConfig TINKER_ID] %s \n", BuildInfo.TINKER_ID));
                                    sb.append(String.format("[buildConfig BASE_TINKER_ID] %s \n", BaseBuildInfo
                                            .BASE_TINKER_ID));

                                    sb.append(String.format("[buildConfig MESSSAGE] %s \n", BuildInfo.MESSAGE));
                                    sb.append(String.format("[TINKER_ID] %s \n", ShareTinkerInternals
                                            .getManifestTinkerID(getApplicationContext())));
                                }
                                sb.append(String.format("[BaseBuildInfo Message] %s \n", BaseBuildInfo.TEST_MESSAGE));
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
