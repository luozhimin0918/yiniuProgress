package com.jyh.kxt.user.ui.fragment;

import android.os.Bundle;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.utils.CollectUtils;
import com.jyh.kxt.main.widget.FastInfoPullPinnedListView;
import com.jyh.kxt.user.adapter.CollectFlashAdapter;
import com.library.base.http.VarConstant;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshBase;

import java.util.List;

import butterknife.BindView;

/**
 * 项目名:Kxt
 * 类描述:收藏-快讯
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/2.
 */

public class CollectFlashFragment extends BaseFragment {

    @BindView(R.id.lv_content) FastInfoPullPinnedListView lvContent;
    @BindView(R.id.pl_rootView) PageLoadLayout plRootView;
    private CollectFlashAdapter adapter;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_flash);

        lvContent.setMode(PullToRefreshBase.Mode.DISABLED);

        initData();

    }

    private void initData() {

        plRootView.loadWait();
        plRootView.setNullImgId(0);
        CollectUtils.getCollectData(getContext(), VarConstant.COLLECT_TYPE_FLASH, new ObserverData<List>() {
            @Override
            public void callback(List flashJsons) {
                if (flashJsons == null) {
                    plRootView.setNullImgId(R.mipmap.icon_collect_null);
                    plRootView.loadEmptyData();
                } else {
                    adapter = new CollectFlashAdapter(flashJsons, getContext());
                    lvContent.setAdapter(adapter);
                    plRootView.loadOver();
                }
            }

            @Override
            public void onError(Exception e) {
                plRootView.loadError();
            }
        });
    }

}
