package com.jyh.kxt.score.ui;

import android.content.Intent;
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
import com.jyh.kxt.score.adapter.TaskAdapter;
import com.jyh.kxt.score.json.MyCoinJson;
import com.jyh.kxt.score.json.TaskAllJson;
import com.jyh.kxt.score.presenter.MyCoinPresenter;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.taobao.accs.ACCSManager.mContext;

/**
 * 项目名:KxtProfessional
 * 类描述:我的金币
 * 创建人:苟蒙蒙
 * 创建日期:2017/9/4.
 */

public class MyCoinActivity extends BaseActivity implements PageLoadLayout.OnAfreshLoadListener, PullToRefreshBase.OnRefreshListener {

    @BindView(R.id.pl_content) public PullToRefreshListView plContent;
    @BindView(R.id.pl_rootView) PageLoadLayout plRootView;
    @BindView(R.id.v_sign_show) View vSignShow;
    @BindView(R.id.tv_sign_show) TextView tvSignShow;
    @BindView(R.id.ll_sign_show) View llSignShow;

    private MyCoinPresenter presenter;
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
        setContentView(R.layout.activity_score_mycoin, StatusBarColor.THEME1);
        presenter = new MyCoinPresenter(this);

        initView();

        loadWait();
        presenter.init(false);
    }

    private void initView() {
        plRootView.setOnAfreshLoadListener(this);
        plContent.setDividerNull();
        plContent.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        plContent.setOnRefreshListener(this);
    }

    @OnClick({R.id.iv_bar_break, R.id.iv_bar_function, R.id.ll_sign_show})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_bar_break:
                onBackPressed();
                break;
            case R.id.iv_bar_function:
                Intent intent = new Intent(getContext(), CoinHistoryActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_sign_show:
                presenter.showSignView();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getQueue().cancelAll(MyCoinPresenter.class.getName());
    }

    public void loadWait() {
        llSignShow.setVisibility(View.GONE);
        plRootView.loadWait();
    }

    public void loadEmptyData() {
        llSignShow.setVisibility(View.GONE);
        plRootView.loadEmptyData();
    }

    public void loadOver() {
        llSignShow.setVisibility(View.VISIBLE);
        plRootView.loadOver();
    }

    public void loadError(VolleyError error) {
        llSignShow.setVisibility(View.GONE);
        plRootView.loadError();
    }

    public void init(MyCoinJson myCoinJson) {
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

    @Override
    public void OnAfreshLoad() {
        loadWait();
        presenter.init(false);
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        presenter.init(true);
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
