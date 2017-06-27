package com.jyh.kxt.datum.ui.fragment;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.datum.presenter.DataPresenter;
import com.library.widget.PageLoadLayout;

import butterknife.BindView;

/**
 * Created by Mr'Dai on 2017/4/11.
 * 数据-数据
 */
public class DataFragment extends BaseFragment implements DataPresenter.TopTabViewClick {

    @BindView(R.id.ll_data_nav) public LinearLayout llDataNav;
    @BindView(R.id.iv_left_content) public ListView ivLeftContent;
    @BindView(R.id.iv_right_content) public ListView ivRightContent;
    @BindView(R.id.pll_right_content) public PageLoadLayout pllRightContent;

    private DataPresenter dataPresenter;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_data);

        dataPresenter = new DataPresenter(this);
        dataPresenter.setTopTabViewClick(this);

        //初始化左侧List
        int leftLineColor = ContextCompat.getColor(getContext(), R.color.theme1);
        ivLeftContent.setDivider(new ColorDrawable(leftLineColor));
        ivLeftContent.setDividerHeight(1);

        //初始化右侧List
        int rightLineColor = ContextCompat.getColor(getContext(), R.color.line_color6);
        ivRightContent.setDivider(new ColorDrawable(rightLineColor));
        ivRightContent.setDividerHeight(0);

        //网络请求
        dataPresenter.requestTopNavigationData();
        dataPresenter.requestLeftNavigationData();
    }

    @Override
    public void topTabSelected(int position) {
    }

    @Override
    public void onChangeTheme() {
        super.onChangeTheme();
        dataPresenter.onChangeTheme();

        int childCount = llDataNav.getChildCount();
        for (int i = 0; i < childCount; i++) {
            TextView tvNav = (TextView) llDataNav.getChildAt(i);
            ivLeftContent.setDivider(new ColorDrawable(ContextCompat.getColor(getContext(), R.color.theme1)));
            ivLeftContent.setDividerHeight(1);
            tvNav.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_data_nav_bg));
            tvNav.setTextColor(ContextCompat.getColor(getContext(), R.color.font_color5));
        }
    }
}
