package com.jyh.kxt.index.ui.fragment;

import android.os.Bundle;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;

/**
 * 首页-探索
 */
public class ProbeFragment extends BaseFragment {

    public static ProbeFragment newInstance() {
        ProbeFragment fragment = new ProbeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_probe);
    }
}
