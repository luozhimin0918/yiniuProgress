package com.jyh.kxt.score.ui;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.widget.MultiDirectionSlidingDrawer;
import com.jyh.kxt.score.adapter.TaskAdapter;
import com.jyh.kxt.score.json.MyCoinJson;
import com.jyh.kxt.score.json.PunchCardJson;
import com.jyh.kxt.score.json.SignJson;
import com.jyh.kxt.score.json.TaskAllJson;
import com.jyh.kxt.score.presenter.MyCoin2Presenter;
import com.library.widget.PageLoadLayout;
import com.library.widget.flowlayout.FlowLayout;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.taobao.accs.ACCSManager.mContext;

public class MyCoin2Activity extends BaseActivity implements PageLoadLayout.OnAfreshLoadListener, PullToRefreshBase.OnRefreshListener {

    @BindView(R.id.iv_bar_break) ImageView ivBarBreak;
    @BindView(R.id.tv_bar_title) TextView tvBarTitle;
    @BindView(R.id.iv_bar_function) TextView ivBarFunction;
    @BindView(R.id.pl_rootView) PageLoadLayout plRootView;
    @BindView(R.id.pl_content) public PullToRefreshListView plContent;
    @BindView(R.id.fl_punch_card_tab) public FlowLayout flPunchCardTab;
    @BindView(R.id.mdsd_sign_content) MultiDirectionSlidingDrawer mdsdSignContent;

    private MyCoin2Presenter myCoin2Presenter;

    private int coinNum;//金币数
    //头部布局
    private View headView;
    private RelativeLayout hvRootView;
    private TextView hvTvScore;
    private TextView hvTvCoin;
    private ImageView hvIvCoin;

    private TaskAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_coin2, StatusBarColor.THEME1);

        tvBarTitle.setText("我的金币");
        ivBarFunction.setText("金币明细");

        initView();

        myCoin2Presenter = new MyCoin2Presenter(this);
        myCoin2Presenter.requestInitCoin(false);

    }

    private void initView() {
        plRootView.setOnAfreshLoadListener(this);
        plContent.setDividerNull();
        plContent.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        plContent.setOnRefreshListener(this);
    }

    @Override
    public void OnAfreshLoad() {
        plRootView.loadWait();
        myCoin2Presenter.requestInitCoin(false);
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        myCoin2Presenter.requestInitCoin(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getQueue().cancelAll(MyCoin2Presenter.class.getName());
    }

    public void loadWait() {
        mdsdSignContent.close();
        plRootView.loadWait();
    }

    public void loadEmptyData() {
        mdsdSignContent.close();
        plRootView.loadEmptyData();
    }

    public void loadOver() {
        mdsdSignContent.open();
        plRootView.loadOver();
    }

    public void loadError(VolleyError error) {
        mdsdSignContent.close();
        plRootView.loadError();
    }

    public void init(MyCoinJson myCoinJson) {
        PunchCardJson punch_card = myCoinJson.getPunch_card();
        int punch_card_days = punch_card.getPunch_card_days();
        int signDays = punch_card_days == 0 ? 0 : punch_card_days % 7;
        List<SignJson> singList = punch_card.getPubch_card_award();
        //打卡数据模拟
        PunchCardJson punchCardJson = new PunchCardJson();
        punchCardJson.setPunch_card_days(signDays);
        punchCardJson.setPubch_card_award(singList);
        myCoin2Presenter.initPunchCard(punchCardJson);

        String my_award_num = myCoinJson.getMy_award_num();
        coinNum = my_award_num == null || my_award_num.trim().equals("") ? 0 : Integer.parseInt(my_award_num);
        List<TaskAllJson> data = myCoinJson.getData();
        if (data == null || data.size() == 0) {
            loadEmptyData();
            return;
        } else {
            List adapterData = new ArrayList();
            for (TaskAllJson taskAllJson : data) {
                String title = taskAllJson.getTitle();
                adapterData.add(title);
                adapterData.addAll(taskAllJson.getData());
            }
            if (headView != null)
                plContent.getRefreshableView().removeHeaderView(headView);
            initHeadViewLayout();
            plContent.getRefreshableView().addHeaderView(headView);
            if (adapter == null) {
                adapter = new TaskAdapter(adapterData, mContext);
                plContent.setAdapter(adapter);
            } else {
                adapter.setData(adapterData);
            }
            loadOver();
        }

    }

    public void refresh(MyCoinJson myCoinJson) {
        PunchCardJson punch_card = myCoinJson.getPunch_card();
        int punch_card_days = punch_card.getPunch_card_days();
        int signDays = punch_card_days == 0 ? 0 : punch_card_days % 7;
        List<SignJson> singList = punch_card.getPubch_card_award();
        //打卡数据模拟
        PunchCardJson punchCardJson = new PunchCardJson();
        punchCardJson.setPunch_card_days(signDays);
        punchCardJson.setPubch_card_award(singList);
        myCoin2Presenter.initPunchCard(punchCardJson);

        String my_award_num = myCoinJson.getMy_award_num();
        coinNum = my_award_num == null || my_award_num.trim().equals("") ? 0 : Integer.parseInt(my_award_num);
        List<TaskAllJson> data = myCoinJson.getData();
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
                adapter = new TaskAdapter(adapterData, mContext);
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
            headView = LayoutInflater.from(mContext).inflate(R.layout.head_sign, null, false);
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
            hvRootView.setBackground(ContextCompat.getDrawable(mContext, R.mipmap.icon_score_bg));
            hvTvScore.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            hvTvCoin.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            hvIvCoin.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.icon_score_coin));
        }
    }
}
