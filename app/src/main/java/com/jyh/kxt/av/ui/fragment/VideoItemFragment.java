package com.jyh.kxt.av.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.av.presenter.VideoItemPresenter;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.base.constant.IntentConstant;
import com.library.widget.handmark.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mr'Dai on 2017/4/10.
 * 视听-ViewPager-Fragment
 */
public class VideoItemFragment extends BaseFragment {

    @BindView(R.id.plv_content) public PullToRefreshListView plvContent;

    private String name;
    private VideoItemPresenter videoItemPresenter;

    public List<Object> list = new ArrayList<>();

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_video_item);

        videoItemPresenter = new VideoItemPresenter(this);

        Bundle arguments = getArguments();
        name = arguments.getString(IntentConstant.NAME);

        videoItemPresenter.setAdapter();
    }

    @Override
    public void userVisibleHint() {
        super.userVisibleHint();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }
}
