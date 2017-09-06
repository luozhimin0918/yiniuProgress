package com.jyh.kxt.score.ui;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.widget.MultiDirectionSlidingDrawer;
import com.jyh.kxt.score.presenter.MyCoin2Presenter;
import com.library.widget.flowlayout.FlowLayout;

import butterknife.BindView;

public class MyCoin2Activity extends BaseActivity  {

    @BindView(R.id.iv_bar_break) ImageView ivBarBreak;
    @BindView(R.id.tv_bar_title) TextView tvBarTitle;
    @BindView(R.id.iv_bar_function) TextView ivBarFunction;
    @BindView(R.id.fl_punch_card_tab) public FlowLayout flPunchCardTab;
    @BindView(R.id.mdsd_sign_content) MultiDirectionSlidingDrawer mdsdSignContent;

    private MyCoin2Presenter myCoin2Presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_coin2, StatusBarColor.THEME1);

        tvBarTitle.setText("我的金币");
        ivBarFunction.setText("金币明细");

        myCoin2Presenter = new MyCoin2Presenter(this);
        myCoin2Presenter.requestInitCoin();

        mdsdSignContent.open();
    }
}
