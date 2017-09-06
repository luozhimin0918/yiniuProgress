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
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.score.json.MyCoinJson;
import com.jyh.kxt.score.json.PunchCardJson;
import com.jyh.kxt.score.json.SignJson;
import com.jyh.kxt.score.ui.MyCoin2Activity;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.util.SystemUtil;
import com.library.widget.window.ToastView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr'Dai on 2017/9/6.
 */

public class MyCoin2Presenter extends BasePresenter {

    @BindObject MyCoin2Activity myCoin2Activity;

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
                    myCoin2Activity.loadEmptyData();
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
                } else
                    myCoin2Activity.init(myCoinJson);
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
                } else
                    myCoin2Activity.loadError(error);
            }
        });

    }

    /**
     * 请求签到
     */
    private void requestPunchCard() {

    }

    /**
     * 初始化签到打卡记录
     *
     * @param punchCardJson
     */
    public void initPunchCard(PunchCardJson punchCardJson) {
        int punchCardDays = punchCardJson.getDays();

        List<SignJson> signJsonList = punchCardJson.getRules();

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

            tvSignDay.setText(signJson.getDescribe());
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

            if (position == punchCardDays) {//签到点击
                punchCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fuelPunchCardView(punchCardView);
                        punchCardView.setOnClickListener(null);//点击事件清除
                        requestPunchCard();
                        ToastView.makeText(mContext, "签到成功!");
                    }
                });
            }
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
