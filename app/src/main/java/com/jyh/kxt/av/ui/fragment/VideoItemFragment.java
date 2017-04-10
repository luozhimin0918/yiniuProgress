package com.jyh.kxt.av.ui.fragment;

import android.os.Bundle;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.constant.IntentConstant;

import butterknife.BindView;

/**
 * Created by Mr'Dai on 2017/4/10.
 * 视听-ViewPager-Fragment
 */
public class VideoItemFragment extends BaseFragment {

    @BindView(R.id.tv_test_txt) TextView tvTest;
    private String name;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_video_item);
        Bundle arguments = getArguments();
        name = arguments.getString(IntentConstant.NAME);

        tvTest.setText(name);
    }

    @Override
    public void userVisibleHint() {
        super.userVisibleHint();

    }
}
