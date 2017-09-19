package com.jyh.kxt.score.presenter;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.bean.SignInfoJson;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.score.json.MyCoinJson;
import com.jyh.kxt.score.json.PunchCardJson;
import com.jyh.kxt.score.json.SignJson;
import com.jyh.kxt.score.ui.MyCoin2Activity;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.bean.EventBusClass;
import com.library.util.SystemUtil;
import com.library.widget.window.ToastView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by Mr'Dai on 2017/9/6.
 */

public class MyCoin2Presenter extends BasePresenter {

    @BindObject private MyCoin2Activity myCoin2Activity;

    private VolleyRequest request;

    public MyCoin2Presenter(IBaseView iBaseView) {
        super(iBaseView);
        request = new VolleyRequest(mContext, mQueue);
        request.setTag(getClass().getName());
    }

    /**
     * 初始化金币接口
     */
    public void requestInitCoin(final boolean isRefresh) {
        //各种数据请求之后回调
        JSONObject param = request.getJsonParam();
        param.put(VarConstant.HTTP_UID, LoginUtils.getUserInfo(mContext).getUid());

        request.doGet(HttpConstant.CREDITS_MAIN, param, new HttpListener<MyCoinJson>() {
            @Override
            protected void onResponse(MyCoinJson myCoinJson) {

                if (myCoinJson == null) {
                    myCoin2Activity.plRootView.loadEmptyData();
                    return;
                }

                if (isRefresh) {
                    myCoin2Activity.refresh(myCoinJson);
                    myCoin2Activity.plContent.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            myCoin2Activity.plContent.onRefreshComplete();
                        }
                    }, 200);
                } else {
                    myCoin2Activity.init(myCoinJson);
                }
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                if (isRefresh) {
                    myCoin2Activity.plContent.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            myCoin2Activity.plContent.onRefreshComplete();
                        }
                    }, 200);
                } else {
                    myCoin2Activity.plRootView.loadError();
                }
            }
        });

    }

    /**
     * 请求签到
     *
     * @param punchCardView
     * @param signJsonList
     * @param position
     */
    private void requestPunchCard(final View punchCardView, List<SignJson> signJsonList, int position) {
        JSONObject jsonParam = request.getJsonParam();
        jsonParam.put(VarConstant.HTTP_UID, LoginUtils.getUserInfo(mContext).getUid());
        final SignJson signJson = signJsonList.get(position);
        jsonParam.put(VarConstant.HTTP_CODE, signJson.getCode());
        request.doGet(HttpConstant.CREDITS_PUNCH_CARD, jsonParam, new HttpListener<String>() {

            @Override
            protected void onResponse(String s) {

                fuelPunchCardView(punchCardView);
                punchCardView.setOnClickListener(null);//点击事件清除
                myCoin2Activity.signed = true;
                myCoin2Activity.sign_state = 1;
                String award = signJson.getAward();
                int awardInt;
                try {
                    awardInt = award == null ? 0 : Integer.parseInt(award);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    awardInt = 0;
                }

                myCoin2Activity.punchCardSucceed(awardInt);
                EventBus.getDefault().post(new EventBusClass(EventBusClass.EVENT_COIN_SIGN, new SignInfoJson(LoginUtils.getUserInfo
                        (mContext).getUid(), 1, myCoin2Activity.task_state)));
                ToastView.makeText(mContext, "签到成功!");
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                ToastView.makeText(mContext, "签到失败!");
            }
        });
    }

    /**
     * 初始化签到打卡记录
     *
     * @param punchCardJson
     */
    public void initPunchCard(PunchCardJson punchCardJson) {
        final int punchCardDays = punchCardJson.getDays();

        final List<SignJson> signJsonList = punchCardJson.getRules();

        DisplayMetrics screenDisplay = SystemUtil.getScreenDisplay(mContext);
        int widthScreen = screenDisplay.widthPixels;

        int itemMargin = SystemUtil.dp2px(mContext, 5);
        int tablePadding = SystemUtil.dp2px(mContext, 10);
        int itemWidth = (widthScreen - (itemMargin * 8) - (tablePadding * 2)) / 4;

        LayoutInflater factory = LayoutInflater.from(mContext);

        for (int position = 0; position < signJsonList.size(); position++) {
            SignJson signJson = signJsonList.get(position);

            final View punchCardView = factory.inflate(R.layout.item_punch_card, myCoin2Activity.flPunchCardTab, false);

            TextView tvSignScore = (TextView) punchCardView.findViewById(R.id.tv_sign1_score);
            TextView tvSignDay = (TextView) punchCardView.findViewById(R.id.tv_sign1_day);

            tvSignDay.setText(signJson.getDescription());
            tvSignScore.setText("+" + signJson.getAward());

            ViewGroup.LayoutParams layoutParams = punchCardView.getLayoutParams();
            if (position == 6) {
                layoutParams.width = itemWidth * 2 + tablePadding;
            } else {
                layoutParams.width = itemWidth;
            }
            punchCardView.setLayoutParams(layoutParams);
            myCoin2Activity.flPunchCardTab.addView(punchCardView, layoutParams);

            if (position < punchCardDays) {
                fuelPunchCardView(punchCardView);
            }

            final int finalPosition = position;
            punchCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!myCoin2Activity.signed) {
                        if (finalPosition == punchCardDays) {
                            punchCardView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    requestPunchCard(punchCardView, signJsonList, finalPosition);
                                }
                            });
                        }
                    }
                }
            });
        }
    }

    /**
     * 处理打卡记录
     */
    private void fuelPunchCardView(View punchCardView) {
        TextView tvSignScore = (TextView) punchCardView.findViewById(R.id.tv_sign1_score);
        TextView tvSignDay = (TextView) punchCardView.findViewById(R.id.tv_sign1_day);
        ImageView ivSignGou = (ImageView) punchCardView.findViewById(R.id.iv_sign1_gou);

        tvSignDay.setSelected(true);
        tvSignScore.setSelected(true);
        punchCardView.setSelected(true);
        ivSignGou.setVisibility(View.VISIBLE);
    }

}
