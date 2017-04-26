package com.jyh.kxt.main.ui.fragment;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.util.PopupUtil;
import com.jyh.kxt.main.presenter.FlashPresenter;
import com.jyh.kxt.main.widget.FastInfoPinnedListView;
import com.jyh.kxt.main.widget.FastInfoPullPinnedListView;
import com.library.bean.EventBusClass;
import com.library.util.SystemUtil;
import com.library.widget.PageLoadLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;

/**
 * 项目名:Kxt
 * 类描述: 快讯Fragment
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/12.
 */

public class FlashFragment extends BaseFragment {

    @BindView(R.id.lv_content) public FastInfoPullPinnedListView lvContent;
    @BindView(R.id.pl_rootView) public PageLoadLayout plRootView;
    private FlashPresenter flashPresenter;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_flash);
        flashPresenter = new FlashPresenter(this);
    }

    @Override
    public void userVisibleHint() {
        super.userVisibleHint();
        FastInfoPinnedListView refreshableView = lvContent.getRefreshableView();
        refreshableView.addFooterListener(flashPresenter);
        plRootView.setOnAfreshLoadListener(flashPresenter);
        lvContent.setOnRefreshListener(flashPresenter);
        flashPresenter.init();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusClass eventBus) {
        if(eventBus!=null&&eventBus.fromCode==EventBusClass.EVENT_FLASH_FILTRATE){
            flashPresenter.filtrate();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            EventBus.getDefault().register(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        flashPresenter.onDestroy();
        super.onDestroyView();
        try {
            EventBus.getDefault().unregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
