package com.jyh.kxt.datum.ui;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Spanned;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.custom.DiscolorTextView;
import com.jyh.kxt.base.widget.TrendChartLayout;
import com.jyh.kxt.datum.bean.HistoryChartBean;
import com.jyh.kxt.datum.bean.HistoryInfoBean;
import com.jyh.kxt.datum.presenter.DatumHistoryPresenter;
import com.jyh.kxt.main.widget.FastInfoPinnedListView;
import com.jyh.kxt.main.widget.FastInfoPullPinnedListView;
import com.library.widget.PageLoadLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 日历-数据-历史
 */
public class DatumHistoryActivity extends BaseActivity implements FastInfoPinnedListView.FooterListener {

    @BindView(R.id.iv_bar_break) ImageView ivBarBreak;
    @BindView(R.id.tv_bar_title) TextView tvBarTitle;
    @BindView(R.id.iv_bar_function) TextView ivBarFunction;

    @BindView(R.id.activity_datumhistory) LinearLayout activityDatumhistory;
    @BindView(R.id.tv_published_agencies) TextView tvPublishedAgencies;
    @BindView(R.id.tv_department_labor) TextView tvDepartmentLabor;
    @BindView(R.id.tv_data_affecting) TextView tvDataAffecting;
    @BindView(R.id.tv_data_explain) TextView tvDataExplain;
    @BindView(R.id.dtv_more_information) DiscolorTextView dtvMoreInformation;
    @BindView(R.id.fplv_content) public FastInfoPullPinnedListView fplvContent;

    @BindView(R.id.pll_content) public PageLoadLayout pageLoadLayout;

    @BindView(R.id.ll_list_head) public LinearLayout llListHead;
    @BindView(R.id.ll_chart_head) public LinearLayout llChartHead;
    @BindView(R.id.tcl_chart_table) TrendChartLayout trendChartLayout;

    private boolean isShownExplain = true;

    @OnClick({R.id.iv_bar_break, R.id.dtv_more_information})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.iv_bar_break:
                onBackPressed();
                break;
            case R.id.dtv_more_information:
                dtvMoreInformation.setText(isShownExplain ? "收起" : "更多信息");
                tvDataExplain.setVisibility(isShownExplain ? View.VISIBLE : View.GONE);
                isShownExplain = !isShownExplain;
                break;
        }
    }

    private DatumHistoryPresenter datumHistoryPresenter;
    public String groupId;
    public String datumCode;
    public String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_datum_history, StatusBarColor.THEME1);


        String title = getIntent().getStringExtra(IntentConstant.NAME);
        groupId = getIntent().getStringExtra("groupId");
        type = getIntent().getStringExtra("type");
        datumCode = getIntent().getStringExtra(IntentConstant.CODE);

        tvBarTitle.setText(title);

        fplvContent.getRefreshableView().setDivider(
                new ColorDrawable(ContextCompat.getColor(getContext(), com.library.R.color.line_background1)));
        fplvContent.getRefreshableView().setDividerHeight(0);

        datumHistoryPresenter = new DatumHistoryPresenter(this);
        datumHistoryPresenter.requestInitInfo();

        fplvContent.getRefreshableView().addFooterListener(this);

        activityDatumhistory.removeView(llListHead);
    }

    public void initInfoView(HistoryInfoBean historyInfoBean) {
        Spanned htmlFontAgency = datumHistoryPresenter.getHtmlFont("公布机构:",
                historyInfoBean.getData().getAgency());
        tvPublishedAgencies.setText(htmlFontAgency);

        Spanned htmlFontFrequency = datumHistoryPresenter.getHtmlFont("公布机构:",
                historyInfoBean.getData().getFrequency());
        tvDepartmentLabor.setText(htmlFontFrequency);

        Spanned htmlFontInfluence = datumHistoryPresenter.getHtmlFont("公布机构:",
                historyInfoBean.getData().getInfluence());
        tvDataAffecting.setText(htmlFontInfluence);

        Spanned htmlFontDefinitions = datumHistoryPresenter.getHtmlFont("数据释意:",
                historyInfoBean.getData().getDefinitions());
        tvDataExplain.setText(htmlFontDefinitions);
    }

    @Override
    public void startLoadMore() {
        datumHistoryPresenter.requestMoreData();
    }

    public void initChartView(HistoryChartBean historyChartBean) {
        try {
            llChartHead.setVisibility(View.VISIBLE);

            List<HistoryChartBean.DataBean> data = historyChartBean.getData();

            List<HistoryChartBean.DataBean> sortData = new ArrayList<>(data);
            Collections.sort(sortData, new Comparator<HistoryChartBean.DataBean>() {
                @Override
                public int compare(HistoryChartBean.DataBean lhs, HistoryChartBean.DataBean rhs) {
                    return lhs.getValue() - rhs.getValue() > 0 ? 1 : -1;
                }
            });

            HistoryChartBean.YAxisBean mYAxisBean = new HistoryChartBean.YAxisBean();

            double value1 = sortData.get(sortData.size() - 1).getValue();
            int max = value1 > 0 ? (int) Math.ceil(value1) : (int) Math.floor(value1);
            mYAxisBean.setMax(max);

            double value2 = sortData.get(0).getValue();
            int min = value1 > 0 ? (int) Math.floor(value2) : (int) Math.ceil(value2);
            mYAxisBean.setMin(min);

            historyChartBean.setY_axis(mYAxisBean);

            trendChartLayout.setData(historyChartBean);
        } catch (Exception e) {
            e.printStackTrace();
            llChartHead.setVisibility(View.GONE);
        }
    }
}
