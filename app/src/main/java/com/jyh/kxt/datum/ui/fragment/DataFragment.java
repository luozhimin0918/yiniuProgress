package com.jyh.kxt.datum.ui.fragment;

import android.os.Bundle;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.library.base.LibActivity;

/**
 * Created by Mr'Dai on 2017/4/11.
 * 数据-数据
 */
public class DataFragment extends BaseFragment {
    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_data, LibActivity.StatusBarColor.THEME1);

    }
}
