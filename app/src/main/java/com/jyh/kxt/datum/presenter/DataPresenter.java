package com.jyh.kxt.datum.presenter;

import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.datum.ui.fragment.DataFragment;
import com.library.util.SystemUtil;

/**
 * Created by Mr'Dai on 2017/4/12.
 */

public class DataPresenter extends BasePresenter {

    @BindObject DataFragment dataFragment;

    public DataPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void requestTopNavigationData() {
        int tvTitleWidth = SystemUtil.dp2px(mContext, 70);
        int tvTitleHeight = SystemUtil.dp2px(mContext, 30);
        int topMargins = SystemUtil.dp2px(mContext, 8);
        int leftMargins = SystemUtil.dp2px(mContext, 15);

        for (int i = 0; i < 10; i++) {
            TextView tvTitle = new TextView(mContext);
            tvTitle.setText("测试" + i);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(tvTitleWidth, tvTitleHeight);
            lp.setMargins(leftMargins, topMargins, 0, topMargins);

            tvTitle.setGravity(Gravity.CENTER);
            tvTitle.setBackgroundResource(R.drawable.shape_data_nav_bg);
            dataFragment.llDataNav.addView(tvTitle, lp);
        }
    }

    public void requestLeftNavigationData() {

    }

    public void generateTextView() {

    }
}
