package com.jyh.kxt.datum.presenter;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.datum.ui.DatumHistoryActivity;
import com.jyh.kxt.datum.ui.fragment.DataFragment;
import com.library.util.ObserverCall;
import com.library.util.SystemUtil;
import com.library.widget.window.ToastView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr'Dai on 2017/4/12.
 */

public class DataPresenter extends BasePresenter {

    public interface TopTabViewClick {
        void topTabSelected(int position);
    }

    @BindObject DataFragment dataFragment;

    private TopTabViewClick topTabViewClick;

    public DataPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void setTopTabViewClick(TopTabViewClick topTabViewClick) {
        this.topTabViewClick = topTabViewClick;
    }

    public void requestTopNavigationData(ObserverCall observerCall) {
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

            tvTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (topTabViewClick != null) {
                        int position = dataFragment.llDataNav.indexOfChild(v);
                        topTabViewClick.topTabSelected(position);
                    }
                }
            });
        }
    }

    public void requestLeftNavigationData() {
        List<String> stringList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            stringList.add("测试" + i);
        }

        int leftLineColor = ContextCompat.getColor(mContext, R.color.white);
        dataFragment.ivLeftContent.setDivider(new ColorDrawable(leftLineColor));
        dataFragment.ivLeftContent.setDividerHeight(1);
        dataFragment.ivLeftContent.setAdapter(new BaseListAdapter<String>(stringList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_data_left, null);
                return convertView;
            }
        });

        dataFragment.ivLeftContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ToastView.makeText3(mContext, "位置" + position);
                view.setPressed(true);
            }
        });


        int rightLineColor = ContextCompat.getColor(mContext, R.color.line_color);
        dataFragment.ivRightContent.setDivider(new ColorDrawable(rightLineColor));
        dataFragment.ivRightContent.setDividerHeight(1);
        dataFragment.ivRightContent.setAdapter(new BaseListAdapter<String>(stringList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_data_right, null);
                return convertView;
            }
        });
        dataFragment.ivRightContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mContext.startActivity(new Intent(mContext, DatumHistoryActivity.class));
            }
        });
    }

    public void generateTextView() {

    }
}
