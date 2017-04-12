package com.jyh.kxt.datum.ui.fragment;

import android.os.Bundle;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.datum.adapter.CalendarItemAdapter;
import com.library.widget.handmark.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Mr'Dai on 2017/4/11.
 */

public class CalendarItemFragment extends BaseFragment {
    @BindView(R.id.ptrlv_content) PullToRefreshListView ptrlvContent;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_calendar_item);
    }

    @Override
    public void userVisibleHint() {
        super.userVisibleHint();
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add("xxx");
        }
        ptrlvContent.setAdapter(new CalendarItemAdapter(getContext(), list));
    }
}
