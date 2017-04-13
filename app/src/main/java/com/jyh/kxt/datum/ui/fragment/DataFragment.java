package com.jyh.kxt.datum.ui.fragment;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.datum.presenter.DataPresenter;
import com.library.util.ObserverCall;

import butterknife.BindView;

/**
 * Created by Mr'Dai on 2017/4/11.
 * 数据-数据
 */
public class DataFragment extends BaseFragment implements DataPresenter.TopTabViewClick {

    @BindView(R.id.ll_data_nav) public LinearLayout llDataNav;
    @BindView(R.id.iv_left_content) public ListView ivLeftContent;
    @BindView(R.id.iv_right_content) public ListView ivRightContent;

    private DataPresenter dataPresenter;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_data);

        dataPresenter = new DataPresenter(this);
        dataPresenter.setTopTabViewClick(this);

        dataPresenter.requestTopNavigationData(new ObserverCall() {
            @Override
            public void onComplete(Object o) {

            }
        });
        dataPresenter.requestLeftNavigationData();
    }

    @Override
    public void topTabSelected(int position) {

    }
}
