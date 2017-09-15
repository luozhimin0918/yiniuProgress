package com.jyh.kxt.score.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.bean.SignInfoJson;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.base.widget.MultiDirectionSlidingDrawer;
import com.jyh.kxt.score.adapter.TaskAdapter;
import com.jyh.kxt.score.json.MyCoinJson;
import com.jyh.kxt.score.json.PunchCardJson;
import com.jyh.kxt.score.json.SignJson;
import com.jyh.kxt.score.json.TaskAllJson;
import com.jyh.kxt.score.presenter.MyCoin2Presenter;
import com.library.bean.EventBusClass;
import com.library.widget.PageLoadLayout;
import com.library.widget.flowlayout.FlowLayout;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyCoin2Activity extends BaseActivity implements
        PageLoadLayout.OnAfreshLoadListener,
        PullToRefreshBase.OnRefreshListener {

    @BindView(R.id.iv_bar_break) ImageView ivBarBreak;
    @BindView(R.id.tv_bar_title) TextView tvBarTitle;
    @BindView(R.id.iv_bar_function) TextView ivBarFunction;
    @BindView(R.id.pl_rootView) public PageLoadLayout plRootView;
    @BindView(R.id.pl_content) public PullToRefreshListView plContent;
    @BindView(R.id.fl_punch_card_tab) public FlowLayout flPunchCardTab;
    @BindView(R.id.tv_punch_card_handle) TextView tvPunchCard;
    @BindView(R.id.iv_punch_card_handle) ImageView ivPunchCard;

    /**
     * 抽屉相关视图
     */
    @BindView(R.id.mdsd_alpha_view) View vAlphaView;
    @BindView(R.id.mdsd_sign_content) MultiDirectionSlidingDrawer drawerSignContent;

    private MyCoin2Presenter myCoin2Presenter;

    //头部布局
    private View headView;

    private RelativeLayout hvRootView;
    public TextView hvTvScore;
    private TextView hvTvCoin;
    private ImageView hvIvCoin;

    public int coinNum;//金币数

    private TaskAdapter adapter;
    private int signDays;
    public boolean signed;//签到状态
    public int sign_state;
    public int task_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_coin2, StatusBarColor.THEME1);

        tvBarTitle.setText("我的金币");
        ivBarFunction.setText("金币明细");
        ivBarFunction.setTextColor(ContextCompat.getColor(this, R.color.actionBar_textColor1));

        drawerSignContent.setAlphaView(vAlphaView);
        drawerSignContent.setOnDrawerListener(new MultiDirectionSlidingDrawer.OnDrawerListener() {
            @Override
            public void onDrawerOpened() {
                ivPunchCard.setSelected(true);
                tvPunchCard.setText("点击收起");
            }

            @Override
            public void onDrawerClosed() {
                ivPunchCard.setSelected(false);
                tvPunchCard.setText("连续签到" + signDays + "天");
            }

            @Override
            public void onSlideOffset(int offset, double percentage) {

            }
        });
        initView();

        myCoin2Presenter = new MyCoin2Presenter(this);

        plRootView.loadWait();
        myCoin2Presenter.requestInitCoin(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        myCoin2Presenter.requestInitCoin(true);
    }

    private void initView() {
        plRootView.setOnAfreshLoadListener(this);
        plContent.setDividerNull();
        plContent.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        plContent.setOnRefreshListener(this);
    }

    @OnClick({R.id.iv_bar_break, R.id.iv_bar_function})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_bar_break:
                onBackPressed();
                break;
            case R.id.iv_bar_function:
                startActivity(new Intent(this, CoinHistoryActivity.class));
                break;
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        myCoin2Presenter.requestInitCoin(true);
    }


    @Override
    public void OnAfreshLoad() {
        plRootView.loadWait();
        myCoin2Presenter.requestInitCoin(false);
    }


    public void init(MyCoinJson myCoinJson) {
        /**
         * 打卡数据
         */
        sign_state = myCoinJson.getSign_state();
        signed = sign_state == 1;
        task_state = myCoinJson.getTask_state();
        EventBus.getDefault().post(new EventBusClass(EventBusClass.EVENT_COIN_SIGN, new SignInfoJson(LoginUtils.getUserInfo(this).getUid
                (), sign_state, task_state)));
        PunchCardJson punch_card = myCoinJson.getPunch_card();
        int punch_card_days = punch_card.getDays();
        signDays = punch_card_days == 0 ? 0 : punch_card_days % 7;
        tvPunchCard.setText("已经连续签到" + (++signDays) + "天");

        List<SignJson> singList = punch_card.getRules();
        //数据模拟
        PunchCardJson punchCardJson = new PunchCardJson();
        punchCardJson.setDays(signDays);
        punchCardJson.setRules(singList);
        myCoin2Presenter.initPunchCard(punchCardJson);

        String my_award_num = myCoinJson.getNum_coins();
        coinNum = my_award_num == null || my_award_num.trim().equals("") ? 0 : Integer.parseInt(my_award_num);
        List<TaskAllJson> data = myCoinJson.getTask();
        if (data == null || data.size() == 0) {
            plRootView.loadEmptyData();
        } else {
            List adapterData = new ArrayList();
            for (TaskAllJson taskAllJson : data) {
                String title = taskAllJson.getTitle();
                adapterData.add(title);
                adapterData.addAll(taskAllJson.getData());
            }
            if (headView != null) {
                plContent.getRefreshableView().removeHeaderView(headView);
            }
            initHeadViewLayout();
            plContent.getRefreshableView().addHeaderView(headView);

            if (adapter == null) {
                adapter = new TaskAdapter(adapterData, getContext());
                plContent.setAdapter(adapter);
            } else {
                adapter.setData(adapterData);
            }
            plRootView.loadOver();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    drawerSignContent.animateOpen();
                }
            }, 200);
        }
    }

    public void refresh(MyCoinJson myCoinJson) {
        PunchCardJson punch_card = myCoinJson.getPunch_card();
        int punch_card_days = punch_card.getDays();
        int signDays = punch_card_days == 0 ? 0 : punch_card_days % 7;
        List<SignJson> singList = punch_card.getRules();
        //打卡数据模拟
        PunchCardJson punchCardJson = new PunchCardJson();
        punchCardJson.setDays(signDays);
        punchCardJson.setRules(singList);
        myCoin2Presenter.initPunchCard(punchCardJson);

        String my_award_num = myCoinJson.getNum_coins();
        coinNum = my_award_num == null || my_award_num.trim().equals("") ? 0 : Integer.parseInt(my_award_num);
        List<TaskAllJson> data = myCoinJson.getTask();
        if (data == null || data.size() == 0) {
        } else {
            List adapterData = new ArrayList();
            for (TaskAllJson taskAllJson : data) {
                String title = taskAllJson.getTitle();
                adapterData.add(title);
                adapterData.addAll(taskAllJson.getData());
            }
            initHeadViewLayout();
            if (adapter == null) {
                adapter = new TaskAdapter(adapterData, getContext());
                plContent.setAdapter(adapter);
            } else {
                adapter.setData(adapterData);
            }
        }
    }

    /**
     * 初始化头部布局
     */
    public void initHeadViewLayout() {
        if (headView == null) {
            headView = LayoutInflater.from(getContext()).inflate(R.layout.head_sign, plContent.getRefreshableView(), false);
            hvRootView = ButterKnife.findById(headView, R.id.rl_rootView);
            hvTvScore = ButterKnife.findById(headView, R.id.tv_score);
            hvTvCoin = ButterKnife.findById(headView, R.id.tv_coin);
            hvIvCoin = ButterKnife.findById(headView, R.id.iv_sign);
        }
        hvTvScore.setText(coinNum + "");
    }

    @Override
    protected void onChangeTheme() {
        super.onChangeTheme();
        if (headView != null) {
            hvRootView.setBackground(ContextCompat.getDrawable(this, R.mipmap.icon_score_bg));
            hvTvScore.setTextColor(ContextCompat.getColor(this, R.color.white));
            hvTvCoin.setTextColor(ContextCompat.getColor(this, R.color.white));
            hvIvCoin.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.icon_score_coin));
        }
        if (adapter != null)
            adapter.notifyDataSetChanged();
        ivBarFunction.setTextColor(ContextCompat.getColor(this, R.color.actionBar_textColor1));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getQueue().cancelAll(MyCoin2Presenter.class.getName());
    }

    public void punchCardSucceed() {
        tvPunchCard.setText("已经连续签到" + (++signDays) + "天");
    }
}
