package com.jyh.kxt.av.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.BaseFragmentAdapter;
import com.jyh.kxt.base.constant.IntentConstant;
import com.library.util.SystemUtil;
import com.library.widget.tablayout.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mr'Dai on 2017/4/10.
 * 视听-排行
 */

public class RankFragment extends BaseFragment {

    @BindView(R.id.stl_navigation_bar) SlidingTabLayout stlNavigationBar;
    @BindView(R.id.vp_content) ViewPager vpContent;

    private List<Fragment> fragmentList = new ArrayList<>();

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_rank);

        initFragments();
        vpContent.setAdapter(new BaseFragmentAdapter(getChildFragmentManager(), fragmentList));
        String[] titles = {"热门播放", "热议视频", "收藏排行"};
        stlNavigationBar.setViewPager(vpContent, titles);
        stlNavigationBar.setTabWidth(SystemUtil.px2dp(getContext(), SystemUtil.getScreenDisplay(getContext()).widthPixels / 3));
    }

    private void initFragments() {
        RankItemFragment morePlay = new RankItemFragment(), moreCollect = new RankItemFragment(), moreComment = new RankItemFragment();
        Bundle bundle = new Bundle();
        bundle.putString(IntentConstant.RANK_TYPE, RankItemFragment.RANK_MORE_PLAY);
        morePlay.setArguments(bundle);
        bundle.putString(IntentConstant.RANK_TYPE, RankItemFragment.RANK_MORE_COLLECT);
        moreCollect.setArguments(bundle);
        bundle.putString(IntentConstant.RANK_TYPE, RankItemFragment.RANK_MORE_COMMENT);
        moreComment.setArguments(bundle);
        fragmentList.add(morePlay);
        fragmentList.add(moreComment);
        fragmentList.add(moreCollect);
    }

    @Override
    public void onChangeTheme() {
        super.onChangeTheme();
        if (fragmentList != null)
            for (Fragment fragment : fragmentList) {
                if (fragment instanceof BaseFragment)
                    ((BaseFragment) fragment).onChangeTheme();
            }
        if (stlNavigationBar != null)
            stlNavigationBar.notifyDataSetChanged();
    }
}
