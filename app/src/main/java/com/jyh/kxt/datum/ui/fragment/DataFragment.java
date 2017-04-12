package com.jyh.kxt.datum.ui.fragment;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.datum.presenter.DataPresenter;

import butterknife.BindView;

/**
 * Created by Mr'Dai on 2017/4/11.
 * 数据-数据
 */
public class DataFragment extends BaseFragment {
    @BindView(R.id.ll_data_nav) public LinearLayout llDataNav;
    @BindView(R.id.iv_left_content) ListView ivLeftContent;
    @BindView(R.id.iv_right_content) ListView ivRightContent;
    private DataPresenter dataPresenter;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_data);

        dataPresenter = new DataPresenter(this);
        dataPresenter.requestTopNavigationData();

//        List<String> stringList = new ArrayList<>();
//        for (int i = 0; i < 15; i++) {
//            stringList.add("测试" + i);
//        }
//
//        ivLeftContent.setAdapter(new BaseListAdapter<String>(stringList) {
//            @Override
//            public View getView(int position, View convertView, ViewGroup parent) {
//                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_data_left, null);
//                return convertView;
//            }
//        });
//        ivRightContent.setAdapter(new BaseListAdapter<String>(stringList) {
//            @Override
//            public View getView(int position, View convertView, ViewGroup parent) {
//                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_data_right, null);
//                return convertView;
//            }
//        });
    }
}
