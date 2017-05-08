package com.jyh.kxt.main.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.widget.StarView;
import com.jyh.kxt.main.presenter.FlashActivityPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 项目名:Kxt
 * 类描述:快讯详情
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/5.
 */

public class FlashActivity extends BaseActivity {

    @BindView(R.id.iv_bar_break) ImageView ivBarBreak;
    @BindView(R.id.tv_bar_title) TextView tvBarTitle;
    @BindView(R.id.iv_bar_function) TextView ivBarFunction;
    @BindView(R.id.tv_time) TextView tvTime;
    @BindView(R.id.tv_flash_title) TextView tvFlashTitle;
    @BindView(R.id.tv_flash_content) TextView tvFlashContent;
    @BindView(R.id.tv_rl_title) TextView tvRlTitle;
    @BindView(R.id.sv_star) StarView rlStar;
    @BindView(R.id.iv_flag) ImageView ivRlFlag;
    @BindView(R.id.tv2) TextView rlValue1;
    @BindView(R.id.tv3) TextView rlValue2;
    @BindView(R.id.tv4) TextView rlValue3;
    @BindView(R.id.ll_content) LinearLayout rlContent;
    @BindView(R.id.ll_tj) LinearLayout layoutTj;
    @BindView(R.id.iv_break) ImageView ivBreak;
    @BindView(R.id.iv_collect) ImageView ivCollect;
    @BindView(R.id.iv_share) ImageView ivShare;
    @BindView(R.id.iv_more) ImageView ivMore;

    @BindView(R.id.layout_tj1) View layoutTj1;
    private ImageView ivTj1Photo;
    private TextView tvTj1Title;
    private TextView tvTj1Author;
    private TextView tvTj1Time;

    @BindView(R.id.layout_tj2) View layoutTj2;
    private ImageView ivTj2Photo;
    private TextView tvTj2Title;
    private TextView tvTj2Author;
    private TextView tvTj2Time;

    private FlashActivityPresenter flashActivityPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash, StatusBarColor.THEME1);
        flashActivityPresenter = new FlashActivityPresenter(this);
        initView();

    }

    private void initView() {
        ivBarBreak.setVisibility(View.INVISIBLE);

        ivTj1Photo = (ImageView) layoutTj1.findViewById(R.id.iv_photo);
        tvTj1Time = (TextView) layoutTj1.findViewById(R.id.tv_time);
        tvTj1Title = (TextView) layoutTj1.findViewById(R.id.tv_title);
        tvTj1Author = (TextView) layoutTj1.findViewById(R.id.tv_author);

        ivTj2Photo = (ImageView) layoutTj2.findViewById(R.id.iv_photo);
        tvTj2Time = (TextView) layoutTj2.findViewById(R.id.tv_time);
        tvTj2Title = (TextView) layoutTj2.findViewById(R.id.tv_title);
        tvTj2Author = (TextView) layoutTj2.findViewById(R.id.tv_author);

    }

    @OnClick({R.id.iv_break, R.id.iv_collect, R.id.iv_share, R.id.iv_more, R.id.layout_tj1, R.id.layout_tj2})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_break:
                onBackPressed();
                break;
            case R.id.iv_collect:
                break;
            case R.id.iv_share:
                break;
            case R.id.iv_more:
                break;
            case R.id.layout_tj1:
                //推荐1
                break;
            case R.id.layout_tj2:
                //推荐2
                break;
        }
    }
}
