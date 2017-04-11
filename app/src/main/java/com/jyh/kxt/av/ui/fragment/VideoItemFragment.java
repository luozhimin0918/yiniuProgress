package com.jyh.kxt.av.ui.fragment;

import android.os.Bundle;

import com.jyh.kxt.R;
import com.jyh.kxt.av.presenter.VideoItemPresenter;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.constant.IntentConstant;
import com.library.widget.handmark.PullToRefreshListView;

import butterknife.BindView;

/**
 * Created by Mr'Dai on 2017/4/10.
 * 视听-ViewPager-Fragment
 */
public class VideoItemFragment extends BaseFragment {

    @BindView(R.id.plv_content) public PullToRefreshListView plvContent;

    private String name;
    private VideoItemPresenter videoItemPresenter;


    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_video_item);

        videoItemPresenter = new VideoItemPresenter(this);

        Bundle arguments = getArguments();
        name = arguments.getString(IntentConstant.NAME);
    }

    @Override
    public void userVisibleHint() {
        super.userVisibleHint();

    }
}
