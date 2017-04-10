package com.jyh.kxt.av.ui.fragment;

import android.os.Bundle;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;

/**
 * Created by Mr'Dai on 2017/4/10.
 * 视听-排行
 */

public class RankFragment extends BaseFragment {

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_rank);

        Bundle arguments = getArguments();
    }
}
