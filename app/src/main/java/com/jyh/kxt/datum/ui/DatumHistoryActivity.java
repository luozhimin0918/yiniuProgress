package com.jyh.kxt.datum.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.datum.presenter.DatumHistoryPresenter;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 日历-数据-历史
 */
public class DatumHistoryActivity extends BaseActivity {

    @BindView(R.id.iv_bar_break) ImageView ivBarBreak;
    @BindView(R.id.tv_bar_title) TextView tvBarTitle;
    @BindView(R.id.iv_bar_function) TextView ivBarFunction;
    @BindView(R.id.activity_datumhistory) LinearLayout activityDatumhistory;


    @OnClick({R.id.iv_bar_break})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.iv_bar_break:
                onBackPressed();
                break;
        }
    }

    private DatumHistoryPresenter datumHistoryPresenter;

    public String datumCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datum_history, StatusBarColor.THEME1);

        String title = getIntent().getStringExtra(IntentConstant.NAME);
        datumCode = getIntent().getStringExtra(IntentConstant.CODE);

        tvBarTitle.setText(title);

        datumHistoryPresenter = new DatumHistoryPresenter(this);
        datumHistoryPresenter.requestInitInfo();
    }
}
