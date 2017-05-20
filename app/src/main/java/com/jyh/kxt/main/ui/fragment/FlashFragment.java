package com.jyh.kxt.main.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.alibaba.fastjson.JSON;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.utils.JumpUtils;
import com.jyh.kxt.index.ui.MainActivity;
import com.jyh.kxt.main.adapter.FastInfoAdapter;
import com.jyh.kxt.main.json.flash.FlashJson;
import com.jyh.kxt.main.json.flash.Flash_NEWS;
import com.jyh.kxt.main.presenter.FlashPresenter;
import com.jyh.kxt.main.widget.FastInfoPinnedListView;
import com.jyh.kxt.main.widget.FastInfoPullPinnedListView;
import com.library.base.http.VarConstant;
import com.library.bean.EventBusClass;
import com.library.widget.PageLoadLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;

/**
 * 项目名:Kxt
 * 类描述: 快讯Fragment
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/12.
 */

public class FlashFragment extends BaseFragment implements PageLoadLayout.OnAfreshLoadListener {

    @BindView(R.id.lv_content) public FastInfoPullPinnedListView lvContent;
    @BindView(R.id.pl_rootView) public PageLoadLayout plRootView;
    private FlashPresenter flashPresenter;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_flash);
        flashPresenter = new FlashPresenter(this);

        plRootView.setOnAfreshLoadListener(this);
    }

    @Override
    public void userVisibleHint() {
        super.userVisibleHint();
        FastInfoPinnedListView refreshableView = lvContent.getRefreshableView();
        refreshableView.addFooterListener(flashPresenter);
        plRootView.setOnAfreshLoadListener(flashPresenter);
        lvContent.setOnRefreshListener(flashPresenter);
        lvContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List source = flashPresenter.adapter.getSource();
                Object obj = source.get(position - 1);
                if (obj != null && obj instanceof FlashJson) {
                    FlashJson flashJson = (FlashJson) obj;
                    String type = flashJson.getCode();
                    String content = flashJson.getContent();

                    MainActivity mainActivity = (MainActivity) getActivity();

                    switch (type) {
                        case VarConstant.SOCKET_FLASH_KUAIXUN:
                        case VarConstant.SOCKET_FLASH_CJRL:
                            JumpUtils.jump(mainActivity, VarConstant.OCLASS_FLASH, VarConstant.OACTION_DETAIL, flashJson.getUid(), null);
                            break;
                        case VarConstant.SOCKET_FLASH_KXTNEWS:
                            Flash_NEWS flash_news = JSON.parseObject(content, Flash_NEWS.class);
                            Flash_NEWS.Jump url = flash_news.getUrl();
                            JumpUtils.jump(mainActivity, url.getC(), VarConstant.OACTION_DETAIL, url.getI(), url.getU());
                            break;
                    }

                }
            }
        });
        flashPresenter.init();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusClass eventBus) {
        switch (eventBus.fromCode) {
            case EventBusClass.EVENT_FLASH_FILTRATE:
                flashPresenter.filtrate();
                break;
            case EventBusClass.EVENT_COLLECT_FLASH:
                FastInfoAdapter adapter = flashPresenter.adapter;
                if (adapter != null)
                    adapter.notifyDataSetChanged();
                break;
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
            getQueue().cancelAll(flashPresenter.getClass().getName());
            EventBus.getDefault().unregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnAfreshLoad() {
        flashPresenter.init();
    }

    public void flashFiltrate() {
        flashPresenter.filtrate();
    }
}
