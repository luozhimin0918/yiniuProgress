package com.jyh.kxt.score.presenter;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.TextViewCompat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.base.util.PopupUtil;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.score.json.MyCoinJson;
import com.jyh.kxt.score.json.PunchCard;
import com.jyh.kxt.score.json.SignJson;
import com.jyh.kxt.score.ui.CoinHistoryActivity;
import com.jyh.kxt.score.ui.MyCoinActivity;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.util.SPUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * 项目名:KxtProfessional
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/9/4.
 */

public class MyCoinPresenter extends BasePresenter implements View.OnClickListener {

    @BindObject MyCoinActivity activity;

    RelativeLayout rlSign1, rlSign2, rlSign3, rlSign4, rlSign5, rlSign6, rlSign7, rlSignTab;
    TextView tvSignTitle, tvSignTitle2, tvSignScore1, tvSignScore2, tvSignScore3, tvSignScore4, tvSignScore5, tvSignScore6, tvSignScore7,
            tvSignContent1,
            tvSignContent2, tvSignContent3, tvSignContent4, tvSignContent5, tvSignContent6, tvSignContent7, tvSignShow, vFunction;
    ImageView ivSign1, ivSign2, ivSign3, ivSign4, ivSign5, ivSign6, ivSign7, vBreak;
    View vSignShow;
    LinearLayout llSignShow, llSignView;

    private VolleyRequest request;
    private PopupUtil signPop;
    private int signDays;//签到天数
    List<SignJson> signs;//签到详情

    private List<View> rlSigns = new ArrayList<>(), ivSigns = new ArrayList<>();
    private List<TextView> tvSignScores = new ArrayList<>(), tvSignContents = new ArrayList<>();

    public MyCoinPresenter(IBaseView iBaseView) {
        super(iBaseView);
        request = new VolleyRequest(mContext, mQueue);
        request.setTag(getClass().getName());
    }

    public void showSignView() {
        initPopView();
        if (signPop.isShowing())
            signPop.dismiss();
        else
            signPop.showAtLocation(activity.plContent, Gravity.TOP, 0, 0);
    }

    private void initPopView() {
        if (signPop == null) {
            signPop = new PopupUtil((Activity) mContext);
            View signView = signPop.createPopupView(R.layout.pop_sign);

            tvSignTitle = ButterKnife.findById(signView, R.id.tv_title);
            tvSignTitle2 = ButterKnife.findById(signView, R.id.tv_bar_title);
            tvSignShow = ButterKnife.findById(signView, R.id.tv_sign_show);
            vSignShow = ButterKnife.findById(signView, R.id.v_sign_show);
            llSignShow = ButterKnife.findById(signView, R.id.ll_sign_show);
            vBreak = ButterKnife.findById(signView, R.id.iv_bar_break);
            vFunction = ButterKnife.findById(signView, R.id.iv_bar_function);

            rlSignTab = ButterKnife.findById(signView, R.id.rl_sign_tab);
            llSignView = ButterKnife.findById(signView, R.id.ll_sign_view);

            rlSign1 = ButterKnife.findById(signView, R.id.rl_sign1);
            rlSign2 = ButterKnife.findById(signView, R.id.rl_sign2);
            rlSign3 = ButterKnife.findById(signView, R.id.rl_sign3);
            rlSign4 = ButterKnife.findById(signView, R.id.rl_sign4);
            rlSign5 = ButterKnife.findById(signView, R.id.rl_sign5);
            rlSign6 = ButterKnife.findById(signView, R.id.rl_sign6);
            rlSign7 = ButterKnife.findById(signView, R.id.rl_sign7);

            tvSignScore1 = ButterKnife.findById(signView, R.id.tv_sign1_score);
            tvSignScore2 = ButterKnife.findById(signView, R.id.tv_sign2_score);
            tvSignScore3 = ButterKnife.findById(signView, R.id.tv_sign3_score);
            tvSignScore4 = ButterKnife.findById(signView, R.id.tv_sign4_score);
            tvSignScore5 = ButterKnife.findById(signView, R.id.tv_sign5_score);
            tvSignScore6 = ButterKnife.findById(signView, R.id.tv_sign6_score);
            tvSignScore7 = ButterKnife.findById(signView, R.id.tv_sign7_score);

            tvSignContent1 = ButterKnife.findById(signView, R.id.tv_sign1_day);
            tvSignContent2 = ButterKnife.findById(signView, R.id.tv_sign2_day);
            tvSignContent3 = ButterKnife.findById(signView, R.id.tv_sign3_day);
            tvSignContent4 = ButterKnife.findById(signView, R.id.tv_sign4_day);
            tvSignContent5 = ButterKnife.findById(signView, R.id.tv_sign5_day);
            tvSignContent6 = ButterKnife.findById(signView, R.id.tv_sign6_day);
            tvSignContent7 = ButterKnife.findById(signView, R.id.tv_sign7_day);

            ivSign1 = ButterKnife.findById(signView, R.id.iv_sign1);
            ivSign2 = ButterKnife.findById(signView, R.id.iv_sign2);
            ivSign3 = ButterKnife.findById(signView, R.id.iv_sign3);
            ivSign4 = ButterKnife.findById(signView, R.id.iv_sign4);
            ivSign5 = ButterKnife.findById(signView, R.id.iv_sign5);
            ivSign6 = ButterKnife.findById(signView, R.id.iv_sign6);
            ivSign7 = ButterKnife.findById(signView, R.id.iv_sign7);

            PopupUtil.Config config = new PopupUtil.Config();
            config.animationStyle = R.style.PopupWindow_Style3;
            config.width = WindowManager.LayoutParams.MATCH_PARENT;
            config.height = WindowManager.LayoutParams.WRAP_CONTENT;
            config.outsideTouchable = true;
            config.alpha = 0.5f;
            config.bgColor = 0X00000000;

            signPop.setConfig(config);

            llSignShow.setOnClickListener(this);
            vBreak.setOnClickListener(this);
            vFunction.setOnClickListener(this);
            rlSign1.setOnClickListener(this);
            rlSign2.setOnClickListener(this);
            rlSign3.setOnClickListener(this);
            rlSign4.setOnClickListener(this);
            rlSign5.setOnClickListener(this);
            rlSign6.setOnClickListener(this);
            rlSign7.setOnClickListener(this);

            rlSigns.add(rlSign1);
            rlSigns.add(rlSign2);
            rlSigns.add(rlSign3);
            rlSigns.add(rlSign4);
            rlSigns.add(rlSign5);
            rlSigns.add(rlSign6);
            rlSigns.add(rlSign7);
            tvSignScores.add(tvSignScore1);
            tvSignScores.add(tvSignScore2);
            tvSignScores.add(tvSignScore3);
            tvSignScores.add(tvSignScore4);
            tvSignScores.add(tvSignScore5);
            tvSignScores.add(tvSignScore6);
            tvSignScores.add(tvSignScore7);
            tvSignContents.add(tvSignContent1);
            tvSignContents.add(tvSignContent2);
            tvSignContents.add(tvSignContent3);
            tvSignContents.add(tvSignContent4);
            tvSignContents.add(tvSignContent5);
            tvSignContents.add(tvSignContent6);
            tvSignContents.add(tvSignContent7);
            ivSigns.add(ivSign1);
            ivSigns.add(ivSign2);
            ivSigns.add(ivSign3);
            ivSigns.add(ivSign4);
            ivSigns.add(ivSign5);
            ivSigns.add(ivSign6);
            ivSigns.add(ivSign7);
            onChangeTheme();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_bar_break:
                signPop.dismiss();
                break;
            case R.id.iv_bar_function:
                mContext.startActivity(new Intent(mContext, CoinHistoryActivity.class));
                break;
            case R.id.ll_sign_show:
                signPop.dismiss();
                break;
            case R.id.rl_sign1:
                sign(1);
                break;
            case R.id.rl_sign2:
                sign(2);
                break;
            case R.id.rl_sign3:
                sign(3);
                break;
            case R.id.rl_sign4:
                sign(4);
                break;
            case R.id.rl_sign5:
                sign(5);
                break;
            case R.id.rl_sign6:
                sign(6);
                break;
            case R.id.rl_sign7:
                sign(7);
                break;
        }
    }

    private void sign(int position) {
        switch (position) {
            case 1:
                if (signDays == 0)
                    setSignTheme(tvSignScore1, tvSignContent1, rlSign1, ivSign1, true);
                break;
            case 2:
                if (signDays == 1)
                    setSignTheme(tvSignScore2, tvSignContent2, rlSign2, ivSign2, true);
                break;
            case 3:
                if (signDays == 2)
                    setSignTheme(tvSignScore3, tvSignContent3, rlSign3, ivSign3, true);
                break;
            case 4:
                if (signDays == 3)
                    setSignTheme(tvSignScore4, tvSignContent4, rlSign4, ivSign4, true);
                break;
            case 5:
                if (signDays == 4)
                    setSignTheme(tvSignScore5, tvSignContent5, rlSign5, ivSign5, true);
                break;
            case 6:
                if (signDays == 5)
                    setSignTheme(tvSignScore6, tvSignContent6, rlSign6, ivSign6, true);
                break;
            case 7:
                if (signDays == 6)
                    setSignTheme(tvSignScore7, tvSignContent7, rlSign7, ivSign7, true);
                break;
        }
    }

    public void setSignTheme(TextView tvScore, TextView tvContent, View rlSign, View ivSign, boolean isSel) {
        Boolean isNight = SPUtils.getBoolean(mContext, SpConstant.SETTING_DAY_NIGHT);
        if (isSel) {
            tvScore.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            tvContent.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            ivSign.setVisibility(View.VISIBLE);
            rlSign.setSelected(true);
        } else {
            rlSign.setSelected(false);
            ivSign.setVisibility(View.GONE);
            if (isNight) {
                tvScore.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                tvContent.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            } else {
                tvScore.setTextColor(ContextCompat.getColor(mContext, R.color.font_color2));
                tvContent.setTextColor(ContextCompat.getColor(mContext, R.color.font_color2));
            }
        }
    }

    public void onChangeTheme() {
        if (signPop != null) {
            tvSignTitle.setTextColor(ContextCompat.getColor(mContext, R.color.font_color64));
            TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    tvSignTitle,
                    R.mipmap.icon_score_sign,
                    0,
                    0,
                    0);
            tvSignShow.setTextColor(ContextCompat.getColor(mContext, R.color.font_color64));
            tvSignTitle2.setTextColor(ContextCompat.getColor(mContext, R.color.actionBar_textColor1));
            vFunction.setTextColor(ContextCompat.getColor(mContext, R.color.font_color64));
            vBreak.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.icon_nav_break));

            rlSignTab.setBackgroundColor(ContextCompat.getColor(mContext, R.color.theme1));
            llSignView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.theme1));
            llSignShow.setBackground(ContextCompat.getDrawable(mContext, R.mipmap.icon_score_down_bg));
            vSignShow.setBackground(ContextCompat.getDrawable(mContext, R.mipmap.icon_score_arrow_up));

            for (int i = 0; i < 7; i++) {
                if (i < signDays)
                    setSignTheme(tvSignScores.get(i), tvSignContents.get(i), rlSigns.get(i), ivSigns.get(i), true);
                else
                    setSignTheme(tvSignScores.get(i), tvSignContents.get(i), rlSigns.get(i), ivSigns.get(i), false);
            }
        }
    }

    public void init(final boolean isRefresh) {
        JSONObject param = request.getJsonParam();
        param.put(VarConstant.HTTP_UID, LoginUtils.getUserInfo(mContext).getUid());
        request.doGet(HttpConstant.CREDITS_MAIN, param, new HttpListener<MyCoinJson>() {
            @Override
            protected void onResponse(MyCoinJson myCoinJson) {
                if (myCoinJson == null) {
                    activity.loadEmptyData();
                    return;
                }
                PunchCard punch_card = myCoinJson.getPunch_card();
                String punch_card_days = punch_card.getPunch_card_days();
                signDays = ((punch_card_days == null || "".equals(punch_card_days.trim())) ? 0 : Integer.parseInt(punch_card_days)) % 7;
                signs = punch_card.getPubch_card_award();
                if (isRefresh) {
                    activity.refresh(myCoinJson);
                    activity.plContent.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            activity.plContent.onRefreshComplete();
                        }
                    }, 200);
                } else
                    activity.init(myCoinJson);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                if (isRefresh) {
                    activity.plContent.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            activity.plContent.onRefreshComplete();
                        }
                    }, 200);
                } else
                    activity.loadError(error);
            }
        });
    }
}
