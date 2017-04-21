package com.jyh.kxt.main.ui.fragment;

import android.os.Bundle;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.library.base.LibActivity;
import com.library.widget.handmark.PullToRefreshListView;

import butterknife.BindView;

/**
 * 项目名:Kxt
 * 类描述: 快讯Fragment
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/12.
 */

public class FlashFragment extends BaseFragment {

    @BindView(R.id.tv_time_day) TextView tvTime;
    @BindView(R.id.lv_content) PullToRefreshListView lvContent;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_flash);
    }

}
