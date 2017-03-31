package com.jyh.kxt.index.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;

/**
 * 数据
 */
public class DatumFragment extends BaseFragment {

    public static DatumFragment newInstance() {
        DatumFragment fragment = new DatumFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_datum);
    }

}
