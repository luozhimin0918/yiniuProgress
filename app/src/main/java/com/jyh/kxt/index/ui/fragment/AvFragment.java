package com.jyh.kxt.index.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;

/**
 * 视听
 */
public class AvFragment extends BaseFragment {

    public static AvFragment newInstance() {
        AvFragment fragment = new AvFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_av);
    }
}
