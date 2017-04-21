package com.jyh.kxt.index.ui.fragment;

import android.os.Bundle;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;

/**
 * 行情
 */
public class MarketFragment extends BaseFragment {

    public static MarketFragment newInstance() {
        MarketFragment fragment = new MarketFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_market);
    }
}
