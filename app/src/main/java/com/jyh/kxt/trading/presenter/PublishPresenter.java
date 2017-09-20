package com.jyh.kxt.trading.presenter;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.dao.EmojeBean;
import com.jyh.kxt.base.util.SoftKeyBoardListener;
import com.jyh.kxt.base.util.emoje.EmoticonLinearLayout;
import com.jyh.kxt.base.util.emoje.EmoticonsEditText;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.trading.ui.PublishActivity;
import com.jyh.kxt.user.json.UserJson;
import com.jyh.kxt.user.ui.LoginOrRegisterActivity;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;
import com.library.util.SystemUtil;
import com.library.widget.window.ToastView;

import io.valuesfeng.picker.Picker;
import io.valuesfeng.picker.engine.GlideEngine;

/**
 * Created by Mr'Dai on 2017/8/21.
 */

public class PublishPresenter extends BasePresenter {

    @BindObject PublishActivity publishActivity;

    private boolean isShowEmoJiView = false;
    private EmoticonLinearLayout emoJeContentView;

    public PublishPresenter(IBaseView iBaseView) {
        super(iBaseView);


        publishActivity.publishContentEt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isShowEmoJiView) {

                    isShowEmoJiView = false;

                    hideEmoJeContent();

                    hideSoftInputFromWindow();

                    return false;
                }
                return false;
            }
        });

        publishActivity.publishContentEt.setOnTextChangedListener(new EmoticonsEditText.OnTextChangedListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int currentSize;
                if (s != null) {
                    currentSize = s.length();
                } else {
                    currentSize = 0;
                }
                publishActivity.ivPublishContentSize.setText(currentSize + "/500");
            }
        });

        if (emoJeContentView == null) {
            ViewGroup.LayoutParams emoJeParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            emoJeContentView = (EmoticonLinearLayout) inflater.inflate(R.layout.view_emoje_content, publishActivity.flEmoJe, false);
            emoJeContentView.setLayoutParams(emoJeParams);
            emoJeContentView.setOnlyAllowSmallEmoJe(true);
            emoJeContentView.initData();

            emoJeContentView.setOnItemClick(new EmoticonLinearLayout.OnItemClick() {
                @Override
                public void itemEmoJeClick(EmojeBean emojeBean) {
                    publishActivity.publishContentEt.itemEmoJeClick(emojeBean);
                }

                @Override
                public void deleteEmoJeClick() {
                    publishActivity.publishContentEt.deleteEmoJeClick();
                }
            });
        }
    }


    /**
     * 发起观点网络请求
     */
    public void postViewPoint() {
        boolean netWorkConnected = SystemUtil.isConnected(mContext);
        if (!netWorkConnected) {
            ToastView.makeText3(mContext, "当前网络不可用");
            return;
        }

        publishActivity.onClick(R.id.iv_publish_arrows);

        UserJson userInfo = LoginUtils.getUserInfo(mContext);

        String editContent = publishActivity.publishContentEt.getText().toString();
        if (editContent == null || editContent.length() == 0) {
            ToastView.makeText3(mContext, "观点内容不能为空喔");
            return;
        }
        if (userInfo == null) {
            mContext.startActivity(new Intent(mContext, LoginOrRegisterActivity.class));
            return;
        }

        showUpLoadView();
        VolleyRequest volleyRequest = new VolleyRequest(mContext, mQueue);
        JSONObject jsonParam = volleyRequest.getJsonParam();
        jsonParam.put("uid", userInfo.getUid());

        jsonParam.put("writer_id", userInfo.getWriter_id());
        jsonParam.put("content", editContent);

        if (publishActivity.base64List.size() != 0) {
            jsonParam.put("picture", publishActivity.base64List);
        }

        if (publishActivity.publishType == 1) {
            jsonParam.put("forward_id", publishActivity.transmitBean.o_id);
        }
        volleyRequest.setTag("uploading");
        volleyRequest.doPost(HttpConstant.VIEWPOINT_PUBLISH, jsonParam, new HttpListener<String>() {
            @Override
            protected void onResponse(String s) {
                if (publishActivity.publishType == 0) {
                    ToastView.makeText(mContext, "发布成功!");
                } else {
                    ToastView.makeText(mContext, "转发成功!");
                }
                alertDialog.dismiss();
                publishActivity.saveDraught(true);
                publishActivity.finish();
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                alertDialog.dismiss();
                ToastView.makeText3(mContext, "发布失败");
            }
        });
    }

    private AlertDialog alertDialog;

    private void showUpLoadView() {
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.publish_upload_ing, null);

        AlertDialog.Builder upLoadDialog = new AlertDialog.Builder(mContext, R.style.dialog3);
        alertDialog = upLoadDialog.create();

        Window window = alertDialog.getWindow();
        alertDialog.setView(contentView);
        alertDialog.show();
        alertDialog.setCancelable(false);
        window.setGravity(Gravity.BOTTOM);

        DisplayMetrics screenDisplay = SystemUtil.getScreenDisplay(mContext);
        View layoutView = contentView.findViewById(R.id.publish_upload_layout);
        ViewGroup.LayoutParams layoutParams = layoutView.getLayoutParams();
        layoutParams.width = screenDisplay.widthPixels;
        layoutView.setLayoutParams(layoutParams);


        View ivClose = contentView.findViewById(R.id.publish_upload_close);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                mQueue.cancelAll("uploading");
            }
        });
    }

    /**
     * 显示图片仓库
     */
    public void showPictureStorage() {

        int currentPictureCount = publishActivity.publishPicturesLayout.getChildCount();
        int residueCount = 4 - currentPictureCount;

        Picker.from(publishActivity)
                .count(residueCount)
                .enableCamera(true)
                .setEngine(new GlideEngine())
                .forResult(1000);

    }

    public void showOrHideEmoJiView() {
        isShowEmoJiView = !isShowEmoJiView;

        if (isShowEmoJiView) {
            SystemUtil.closeSoftInputWindow(publishActivity);

            ViewGroup.LayoutParams layoutParams = publishActivity.flEmoJe.getLayoutParams();
            layoutParams.height = 0;
            publishActivity.flEmoJe.setLayoutParams(layoutParams);


            publishActivity.flEmoJe.addView(emoJeContentView);
            publishActivity.ivEmoJeIcon.setImageResource(R.mipmap.ico_keybor);


            RelativeLayout.LayoutParams navigationParams = (RelativeLayout.LayoutParams)
                    publishActivity.publishNavigation.getLayoutParams();

            int bottomMargin = navigationParams.bottomMargin;
            if (bottomMargin == 0) {
                showEmoJeContent();
            } else {
                publishActivity.setSoftKeyBoardListener(new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
                    @Override
                    public void keyBoardShow(int height) {

                    }

                    @Override
                    public void keyBoardHide(int height) {
                        showEmoJeContent();
                    }
                });
            }
        } else {
            hideEmoJeContent();
            hideSoftInputFromWindow();
        }
    }

    private void hideSoftInputFromWindow() {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void showEmoJeContent() {
        int emoJeHeight = SystemUtil.dp2px(publishActivity, 215);

        ViewGroup.LayoutParams layoutParams = publishActivity.flEmoJe.getLayoutParams();
        layoutParams.height = emoJeHeight;
        publishActivity.flEmoJe.setLayoutParams(layoutParams);

        publishActivity.setSoftKeyBoardListener(null);
    }

    public void hideEmoJeContent() {
        isShowEmoJiView = false;
        publishActivity.ivEmoJeIcon.setImageResource(R.mipmap.icon_emoje);
        if (emoJeContentView != null) {
            publishActivity.flEmoJe.removeView(emoJeContentView);
        }

        ViewGroup.LayoutParams layoutParams = publishActivity.flEmoJe.getLayoutParams();
        layoutParams.height = 0;
        publishActivity.flEmoJe.setLayoutParams(layoutParams);
    }

}
