package com.jyh.kxt.score.presenter;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.score.json.PunchCardJson;
import com.jyh.kxt.score.json.SignJson;
import com.jyh.kxt.score.ui.MyCoin2Activity;
import com.library.util.SystemUtil;
import com.library.widget.window.ToastView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr'Dai on 2017/9/6.
 */

public class MyCoin2Presenter extends BasePresenter {

    @BindObject MyCoin2Activity myCoin2Activity;

    public MyCoin2Presenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    /**
     * 初始化金币接口
     */
    public void requestInitCoin() {

        //各种数据请求之后回调

        //打卡数据模拟
        PunchCardJson punchCardJson = new PunchCardJson();

        List<SignJson> singList = new ArrayList<>();
        singList.add(new SignJson("3", "1", "首签"));
        singList.add(new SignJson("5", "2", "第2天"));
        singList.add(new SignJson("7", "3", "第3天"));
        singList.add(new SignJson("9", "4", "第4天"));
        singList.add(new SignJson("11", "5", "第5天"));
        singList.add(new SignJson("13", "6", "第6天"));
        singList.add(new SignJson("15", "7", "第7天"));

        punchCardJson.setPunch_card_days(3);
        punchCardJson.setPubch_card_award(singList);
        initPunchCard(punchCardJson);
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
    private void initPunchCard(PunchCardJson punchCardJson) {
        int punchCardDays = punchCardJson.getPunch_card_days();

        List<SignJson> signJsonList = punchCardJson.getPubch_card_award();

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
